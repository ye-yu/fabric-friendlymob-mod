package fp.yeyu.mcvisualmod.mobs.entity

import fp.yeyu.mcvisualmod.screens.VindorGUI
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.CreeperEntity
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
import net.minecraft.screen.*
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.function.Predicate

class Vindor(entityType: EntityType<out IronGolemEntity>?, world: World?) : IronGolemEntity(entityType, world),
    PropertyDelegateHolder {

    init {
        equipStack(EquipmentSlot.MAINHAND, ItemStack(Items.IRON_AXE))
    }

    override fun canBeLeashedBy(player: PlayerEntity?): Boolean {
        return false
    }

    companion object {
        const val NAME = "vindor"
        val LOGGER: Logger = LogManager.getLogger()
        const val WONDER_SLOT_ONE = "wonder_one"
        const val WONDER_SLOT_TWO = "wonder_two"
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
            LOGGER.info(String.format("%s is interacting with a Vindor.", player?.displayName))
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

    override fun writeCustomDataToTag(tag: CompoundTag?) {
        super.writeCustomDataToTag(tag)
        if (!inventory.isEmpty) {
            val vindorInvTag = CompoundTag()
            val slot1 = inventory.getStack(0)
            val slot2 = inventory.getStack(1)
            if (!slot1.isEmpty)
                tag?.put(WONDER_SLOT_ONE, slot1.toTag(vindorInvTag))
            if (!slot2.isEmpty)
                tag?.put(WONDER_SLOT_TWO, slot2.toTag(vindorInvTag))
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
    }

    override fun getAmbientSound(): SoundEvent? {
        return SoundEvents.ENTITY_VILLAGER_AMBIENT
    }

    override fun getHurtSound(source: DamageSource?): SoundEvent? {
        return SoundEvents.ENTITY_VINDICATOR_HURT
    }

    private val inventory = VindorInventory()
    fun getInventory(): Inventory {
        LOGGER.info("Accessing Vindor's inventory")
        return inventory
    }

    private val propertyDelegate = ArrayPropertyDelegate(1)
    override fun getPropertyDelegate(): PropertyDelegate {
        return propertyDelegate
    }

    class VindorGuiHandler(val who: Vindor): NamedScreenHandlerFactory {
        override fun createMenu(syncId: Int, inv: PlayerInventory?, player: PlayerEntity?): ScreenHandler? {
            return VindorGUI(syncId, inv, ScreenHandlerContext.create(player?.world, player?.blockPos), who)
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
            val stack = slots[slot].split(amount)
            LOGGER.info(String.format("Vindor's inventory slot %d is empty: %s", slot, slots[slot].isEmpty))
            return stack
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