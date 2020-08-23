package fp.yeyu.monsterfriend.mobs.entity

import fp.yeyu.monsterfriend.screens.Screens
import fp.yeyu.monsterfriend.screens.vindor.ServerVindorScreenHandler
import fp.yeyu.monsterfriend.utils.wondertrade.WonderTrade
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.mob.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.function.Predicate

class Vindor(entityType: EntityType<Vindor>, world: World?) : PathAwareEntity(entityType, world), Angerable,
    GuiProvider {

    private val guiHandler = VindorGuiHandler(this)
    private val inventory = VindorInventory()

    override var currentUser: PlayerEntity? = null
    var wonderTick = -1 // means, no item to trade
    var wonderState = WonderState.NEUTRAL
        set(value) {
            field = if (world.isClient) value
            else {
                if (field != value) this.world.sendEntityStatus(this, (90 + value.ordinal).toByte())
                value
            }
        }
    private var receivedMessage = ""
    var senderMessage = ""
    private var angerTime: Int = -1
    private var angryAt: UUID? = null

    init {
        equipStack(EquipmentSlot.MAINHAND, ItemStack(Items.IRON_AXE))
    }

    override fun canBeLeashedBy(player: PlayerEntity?): Boolean {
        return false
    }

    companion object {
        fun createVindorAttributes(): DefaultAttributeContainer.Builder {
            return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3499999940395355)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12.0).add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
        }

        val LOGGER: Logger = LogManager.getLogger()
        private const val WONDER_SLOT_ONE = "wonder_one"
        private const val WONDER_SLOT_TWO = "wonder_two"
        private const val WONDER_TIMEOUT = "wonder_timeout"
        private const val WONDER_SENDER_MSG = "wonder_sender_msg"
        private const val WONDER_RECEIVER_MSG = "wonder_receiver_msg"

        private val WONDER_STATE = DataTracker.registerData(Vindor::class.java, TrackedDataHandlerRegistry.INTEGER)
    }

    override fun initDataTracker() {
        super.initDataTracker()
        this.dataTracker.startTracking(WONDER_STATE, WonderState.NEUTRAL.ordinal)
    }

    @Environment(EnvType.CLIENT)
    fun getState(): State? {
        return if (isAttacking) State.ATTACKING
        else State.CROSSED
    }

    @Environment(EnvType.CLIENT)
    enum class State {
        CROSSED, ATTACKING
    }

    enum class WonderState {
        NEUTRAL, READY, RECEIVED
    }

    private fun receivedWonderItem(): Boolean {
        return !inventory.getStack(1).isEmpty
    }

    override fun initGoals() {
        goalSelector.add(0, LookAtCustomerGoal(this))
        goalSelector.add(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.add(2, WanderNearTargetGoal(this, 0.9, 32.0f))
        goalSelector.add(2, WanderAroundPointOfInterestGoal(this, 0.6, false))
        goalSelector.add(7, LookAtEntityGoal(this, PlayerEntity::class.java, 6.0f))
        goalSelector.add(8, LookAroundGoal(this))
        targetSelector.add(2, RevengeGoal(this, *arrayOfNulls(0)))
        targetSelector.add(
            3, FollowTargetGoal(this,
                PlayerEntity::class.java, 10, true, false,
                Predicate { entity -> shouldAngerAt(entity) }
            )
        )
        targetSelector.add(
            3, FollowTargetGoal(this,
                MobEntity::class.java, 5, false, false,
                Predicate { livingEntity -> livingEntity is Monster && livingEntity !is CreeperEntity }
            )
        )
        targetSelector.add(4, UniversalAngerGoal(this, false))
    }

    override fun interactMob(player: PlayerEntity?, hand: Hand?): ActionResult {
        val interactMob = super.interactMob(player, hand)
        if (interactMob == ActionResult.PASS || interactMob.isAccepted) {
            return trade(player)
        }
        return interactMob
    }

    private fun trade(player: PlayerEntity?): ActionResult {
        if (player == null) return ActionResult.success(this.world.isClient)
        if (currentUser != null) return ActionResult.success(this.world.isClient)
        player.openHandledScreen(guiHandler)
        currentUser = player
        return ActionResult.success(this.world.isClient)
    }

    override fun tryAttack(target: Entity): Boolean {
        var f = getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE).toFloat()
        var g = getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK).toFloat()
        if (target is LivingEntity) {
            f += EnchantmentHelper.getAttackDamage(this.mainHandStack, target.group)
            g += EnchantmentHelper.getKnockback(this).toFloat()
        }
        val i = EnchantmentHelper.getFireAspect(this)
        if (i > 0) {
            target.setOnFireFor(i * 4)
        }
        val bl = target.damage(DamageSource.mob(this), f)
        if (bl) {
            if (g > 0.0f && target is LivingEntity) {
                target.takeKnockback(
                    g * 0.5f,
                    MathHelper.sin(yaw * 0.017453292f).toDouble(),
                    (-MathHelper.cos(yaw * 0.017453292f)).toDouble()
                )
                velocity = velocity.multiply(0.6, 1.0, 0.6)
            }
            if (target is PlayerEntity) {
                disablePlayerShield(
                    target,
                    this.mainHandStack,
                    if (target.isUsingItem) target.activeItem else ItemStack.EMPTY
                )
            }
            dealDamage(this, target)
            onAttacking(target)
        }
        return bl
    }

    private fun disablePlayerShield(
        player: PlayerEntity,
        mobStack: ItemStack,
        playerStack: ItemStack
    ) {
        if (!mobStack.isEmpty && !playerStack.isEmpty && mobStack.item is AxeItem && playerStack.item === Items.SHIELD) {
            val f = 0.25f + EnchantmentHelper.getEfficiency(this).toFloat() * 0.05f
            if (random.nextFloat() < f) {
                player.itemCooldownManager[Items.SHIELD] = 100
                world.sendEntityStatus(player, 30.toByte())
            }
        }
    }

    override fun writeCustomDataToTag(tag: CompoundTag) {
        super.writeCustomDataToTag(tag)
        tag.putInt(WONDER_TIMEOUT, wonderTick)
        if (!inventory.isEmpty) {
            val vindorInvTag = CompoundTag()
            val slot1 = inventory.getStack(0)
            val slot2 = inventory.getStack(1)
            if (!slot1.isEmpty)
                tag.put(WONDER_SLOT_ONE, slot1.toTag(vindorInvTag))
            if (!slot2.isEmpty)
                tag.put(WONDER_SLOT_TWO, slot2.toTag(vindorInvTag))
            tag.putString(WONDER_SENDER_MSG, senderMessage)
            tag.putString(WONDER_RECEIVER_MSG, receivedMessage)
        }
    }

    override fun readCustomDataFromTag(tag: CompoundTag?) {
        super.readCustomDataFromTag(tag)
        if (tag == null) return

        if (tag.contains(WONDER_SLOT_ONE)) {
            inventory.setStack(0, ItemStack.fromTag(tag.getCompound(WONDER_SLOT_ONE)))
        }

        if (tag.contains(WONDER_SLOT_TWO)) {
            inventory.setStack(1, ItemStack.fromTag(tag.getCompound(WONDER_SLOT_TWO)))
        }

        if (tag.contains(WONDER_TIMEOUT)) {
            wonderTick = tag.getInt(WONDER_TIMEOUT)
        }

        if (tag.contains(WONDER_SENDER_MSG)) {
            senderMessage = tag.getString(WONDER_SENDER_MSG)
        }

        if (tag.contains(WONDER_RECEIVER_MSG)) {
            receivedMessage = tag.getString(WONDER_RECEIVER_MSG)
        }
    }

    override fun getAmbientSound(): SoundEvent? {
        if (receivedWonderItem()) return SoundEvents.ENTITY_VILLAGER_TRADE
        return SoundEvents.ENTITY_VILLAGER_AMBIENT
    }

    override fun mobTick() {
        super.mobTick()

        if (world !is ServerWorld) return
        if (wonderTick > 0) {
            wonderTick--
        } else if (wonderTick == 0) {
            if (WonderTrade.lock()) {
                val poppedItem = WonderTrade.popWonderItem(inventory.getStack(0), senderMessage)
                inventory.clear()
                inventory.setStack(1, poppedItem.item)
                receivedMessage = poppedItem.msg
                senderMessage = ""
                WonderTrade.unlock()
                wonderTick = -1
                this.world.playSound(null, this.blockPos, getReceivedWonderTradeSound(), SoundCategory.VOICE, 1f, 1f)
                validateWonderState()
            }
        }

        tickAngerLogic(world as ServerWorld, true)
    }

    private fun flushMessage() {
        if (receivedMessage.isEmpty()) return
        if (this.world !is ServerWorld) return
        if (currentUser == null) return
        currentUser!!.sendMessage(LiteralText("Wonder trade succesful! They said: $receivedMessage"), false)
        currentUser!!.sendMessage(LiteralText("\"$receivedMessage\""), true)
        receivedMessage = ""
        this.world.playSound(null, this.blockPos, getTradedSound(), SoundCategory.VOICE, 1f, 1f)
    }

    private fun getTradedSound(): SoundEvent = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP

    private fun getReceivedWonderTradeSound(): SoundEvent = SoundEvents.ENTITY_PLAYER_LEVELUP

    override fun getHurtSound(source: DamageSource?): SoundEvent? = SoundEvents.ENTITY_VINDICATOR_HURT

    override fun getDeathSound(): SoundEvent? = SoundEvents.ENTITY_VINDICATOR_DEATH

    fun finishTrading() {
        val itemSlot1 = inventory.getStack(0)
        val itemSlot2 = inventory.getStack(1)
        if (!itemSlot1.isEmpty) {
            getInventory().setStack(0, itemSlot1)
        } else {
            getInventory().setStack(0, ItemStack.EMPTY)
        }

        getInventory().setStack(1, ItemStack.EMPTY)

        if (!itemSlot2.isEmpty) {
            currentUser?.dropItem(itemSlot2, false)
        }

        wonderTick = if (inventory.getStack(0).isEmpty) -1
        else 20 * 30 + this.world.random.nextInt(20 * 30)

        flushMessage()
        currentUser = null
        this.world.playSound(null, this.blockPos, getHappySound(), SoundCategory.VOICE, 1f, 1f)
        validateWonderState()
    }

    private fun validateWonderState() {
        if (getInventory().getStack(0).isEmpty && !getInventory().getStack(1).isEmpty) {
            wonderState = WonderState.RECEIVED
        } else if (!getInventory().getStack(0).isEmpty && getInventory().getStack(1).isEmpty) {
            wonderState = WonderState.READY
        } else if (getInventory().getStack(0).isEmpty && getInventory().getStack(1).isEmpty) {
            wonderState = WonderState.NEUTRAL
        } else {
            LOGGER.warn(
                "Error in programming. \n"
                        + "Send stack: ${getInventory().getStack(0).item} -> is empty? ${getInventory().getStack(0).isEmpty}\n"
                        + "Receive stack: ${getInventory().getStack(1).item} -> is empty? ${getInventory().getStack(
                    1
                ).isEmpty}"
            )
            LOGGER.trace(Throwable())
        }
    }

    override fun handleStatus(status: Byte) {
        when (status) {
            in 90 until (90 + WonderState.values().size) -> wonderState = WonderState.values()[status - 90]
            else -> super.handleStatus(status)
        }
    }

    private fun getHappySound(): SoundEvent = SoundEvents.ENTITY_VILLAGER_YES

    fun getInventory(): Inventory = inventory

    class VindorGuiHandler(private val who: Vindor) : NamedScreenHandlerFactory {
        override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity?): ScreenHandler? {
            who.wonderTick = -1
            return ServerVindorScreenHandler(
                Screens.VINDOR_SCREEN.screenHandlerType,
                syncId,
                inv,
                who
            )
        }

        override fun getDisplayName(): Text {
            return TranslatableText(Screens.VINDOR_SCREEN.translationKey)
        }
    }

    class VindorInventory : Inventory {
        private val slots = arrayOf(ItemStack.EMPTY, ItemStack.EMPTY)

        override fun markDirty() {
        }

        override fun clear() {
            slots[0] = ItemStack.EMPTY
            slots[1] = ItemStack.EMPTY
        }

        override fun setStack(slot: Int, stack: ItemStack) {
            slots[slot] = stack
        }

        override fun isEmpty(): Boolean = slots[0] == ItemStack.EMPTY && slots[1] == ItemStack.EMPTY

        override fun removeStack(slot: Int, amount: Int): ItemStack = slots[slot].split(amount)

        override fun removeStack(slot: Int): ItemStack {
            val stack = slots[slot]
            slots[slot] = ItemStack.EMPTY
            return stack
        }

        override fun getStack(slot: Int): ItemStack = slots[slot]

        override fun canPlayerUse(player: PlayerEntity?): Boolean = false

        override fun size(): Int = 2
    }

    override fun getAngerTime(): Int = angerTime

    override fun setAngerTime(ticks: Int) {
        angerTime = ticks
    }

    override fun chooseRandomAngerTime() {
        angerTime = 50
    }

    override fun getAngryAt(): UUID? = angryAt

    override fun setAngryAt(uuid: UUID?) {
        angryAt = uuid
    }
}