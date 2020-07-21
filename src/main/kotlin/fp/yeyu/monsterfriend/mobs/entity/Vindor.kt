package fp.yeyu.monsterfriend.mobs.entity

import fp.yeyu.monsterfriend.screens.VindorScreenDescription
import fp.yeyu.monsterfriend.statics.mutable.WonderTrade
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
import net.minecraft.entity.mob.CreeperEntity
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.IronGolemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.AxeItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
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
import java.util.function.Predicate

class Vindor(entityType: EntityType<out IronGolemEntity>?, world: World?) : IronGolemEntity(entityType, world) {

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
        this.dataTracker.startTracking(WONDER_STATE, WonderState.NEUTRAL.index)
    }

    @Environment(EnvType.CLIENT)
    fun getState(): State? {
        return if (isAttacking) {
            State.ATTACKING
        } else {
            State.CROSSED
        }
    }

    @Environment(EnvType.CLIENT)
    enum class State {
        CROSSED, ATTACKING
    }

    @Environment(EnvType.CLIENT)
    enum class WonderState(val index: Int) {
        NEUTRAL(0), READY(1), RECEIVED(2);

        companion object {
            operator fun get(index: Int): WonderState {
                return when (index) {
                    0 -> NEUTRAL
                    1 -> READY
                    2 -> RECEIVED
                    else -> throw IndexOutOfBoundsException(index.toString())
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    fun getWonderState(): WonderState {
        return WonderState[this.dataTracker.get(WONDER_STATE)]
    }

    private fun setWonderState(wonderState: WonderState) {
        this.dataTracker.set(WONDER_STATE, wonderState.index)
    }

    private fun receivedWonderItem(): Boolean {
        return !inventory.getStack(1).isEmpty
    }

    var currentCustomer: PlayerEntity? = null

    override fun initGoals() {
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

    private val guiHandler = VindorGuiHandler(this)
    private fun trade(player: PlayerEntity?): ActionResult {
        if (player == null) return ActionResult.success(this.world.isClient)
        if (currentCustomer != null) return ActionResult.success(this.world.isClient)
        player.openHandledScreen(guiHandler)
        currentCustomer = player
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

    var wonderTick = -1 // means, no item to trade
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
            tag.putString(WONDER_SENDER_MSG, getSenderMessage())
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
            setSenderMessage(tag.getString(WONDER_SENDER_MSG))
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
                val poppedItem = WonderTrade.popWonderItem(inventory.getStack(0), senderMsg)
                inventory.clear()
                inventory.setStack(1, poppedItem.item)
                receivedMessage = poppedItem.msg
                setSenderMessage("")
                WonderTrade.unlock()
                wonderTick = -1
                this.world.playSound(null, this.blockPos, getReceivedWonderTradeSound(), SoundCategory.VOICE, 1f, 1f)
                validateWonderState()
            }
        }
    }

    private var receivedMessage = ""
    var senderMsg = ""

    fun setSenderMessage(msg: String) {
        senderMsg = msg
    }

    fun getSenderMessage(): String {
        return senderMsg
    }

    private fun flushMessage() {
        if (receivedMessage.isEmpty()) return
        if (this.world !is ServerWorld) return
        if (currentCustomer == null) return
        currentCustomer!!.sendMessage(LiteralText("Wonder trade succesful! They said: $receivedMessage"), false)
        currentCustomer!!.sendMessage(LiteralText("\"$receivedMessage\""), true)
        receivedMessage = ""
        this.world.playSound(null, this.blockPos, getTradedSound(), SoundCategory.VOICE, 1f, 1f)
    }

    private fun getTradedSound(): SoundEvent {
        return SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP
    }

    private fun getReceivedWonderTradeSound(): SoundEvent {
        return SoundEvents.ENTITY_PLAYER_LEVELUP
    }

    override fun getHurtSound(source: DamageSource?): SoundEvent? {
        return SoundEvents.ENTITY_VINDICATOR_HURT
    }

    override fun getDeathSound(): SoundEvent? {
        return SoundEvents.ENTITY_VINDICATOR_DEATH
    }

    fun finishTrading(itemSlot1: ItemStack?, itemSlot2: ItemStack?) {
        if (itemSlot1 != null && !itemSlot1.isEmpty) {
            getInventory().setStack(0, itemSlot1)
        } else {
            getInventory().setStack(0, ItemStack.EMPTY)
        }

        getInventory().setStack(1, ItemStack.EMPTY)

        if (itemSlot2 != null && !itemSlot2.isEmpty) {
            currentCustomer!!.dropStack(itemSlot2)
        }

        wonderTick = if (inventory.getStack(0).isEmpty) -1
        else 20 * 30 + this.world.random.nextInt(20 * 30)

        flushMessage()
        currentCustomer = null
        this.world.playSound(null, this.blockPos, getHappySound(), SoundCategory.VOICE, 1f, 1f)
        validateWonderState()
    }

    private fun validateWonderState() {
        if (getInventory().getStack(0).isEmpty && !getInventory().getStack(1).isEmpty) {
            setWonderState(WonderState.RECEIVED)
        } else if (!getInventory().getStack(0).isEmpty && getInventory().getStack(1).isEmpty) {
            setWonderState(WonderState.READY)
        } else if (getInventory().getStack(0).isEmpty && getInventory().getStack(1).isEmpty) {
            setWonderState(WonderState.NEUTRAL)
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

    private fun getHappySound(): SoundEvent {
        return SoundEvents.ENTITY_VILLAGER_YES
    }

    private val inventory = VindorInventory()
    fun getInventory(): Inventory {
        return inventory
    }

    class VindorGuiHandler(private val who: Vindor?) : NamedScreenHandlerFactory {
        override fun createMenu(syncId: Int, inv: PlayerInventory?, player: PlayerEntity?): ScreenHandler? {
            if (who != null) who.wonderTick = -1
            return VindorScreenDescription(syncId, inv, ScreenHandlerContext.create(player?.world, player?.blockPos), who)
        }

        override fun getDisplayName(): Text {
            return TranslatableText("container.friendlymob.vindor")
        }
    }

    class VindorInventory : Inventory {
        private val slots = arrayOf(ItemStack.EMPTY, ItemStack.EMPTY)

        override fun markDirty() {
            throw IllegalAccessError("Access is not relevant.")
        }

        override fun clear() {
            slots[0] = ItemStack.EMPTY
            slots[1] = ItemStack.EMPTY
        }

        override fun setStack(slot: Int, stack: ItemStack) {
            slots[slot] = stack
        }

        override fun isEmpty(): Boolean {
            return slots[0] == ItemStack.EMPTY && slots[1] == ItemStack.EMPTY
        }

        override fun removeStack(slot: Int, amount: Int): ItemStack {
            return slots[slot].split(amount)
        }

        override fun removeStack(slot: Int): ItemStack {
            val stack = slots[slot]
            slots[slot] = ItemStack.EMPTY
            return stack
        }

        override fun getStack(slot: Int): ItemStack {
            return slots[slot]
        }

        override fun canPlayerUse(player: PlayerEntity?): Boolean {
            return false
        }

        override fun size(): Int {
            return 2
        }
    }
}