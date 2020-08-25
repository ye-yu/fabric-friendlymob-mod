package fp.yeyu.monsterfriend.mobs.entity

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.*
import net.minecraft.entity.ai.RangedAttackMob
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.Angerable
import net.minecraft.entity.mob.CreeperEntity
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.PersistentProjectileEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.RangedWeaponItem
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.Difficulty
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.util.*

class Skelly(entityType: EntityType<Skelly>, world: World) : HostileEntity(entityType, world), RangedAttackMob,
    Angerable {

    private val bowAttackGoal = SkellyBowAttackGoal(this, 1.0, 20, 15.0f)
    private val meleeAttackGoal: MeleeAttackGoal = object : MeleeAttackGoal(this, 1.2, false) {
        override fun stop() {
            super.stop()
            this@Skelly.isAttacking = false
        }

        override fun start() {
            super.start()
            this@Skelly.isAttacking = true
        }

        override fun canStart(): Boolean {
            if (this@Skelly.angryAt == null) return false
            return super.canStart()
        }
    }

    override fun initGoals() {
        goalSelector.add(2, AvoidSunlightGoal(this))
        goalSelector.add(3, EscapeSunlightGoal(this, 1.0))
        goalSelector.add(3, FleeEntityGoal(this, WolfEntity::class.java, 6.0f, 1.0, 1.2))
        goalSelector.add(5, WanderAroundFarGoal(this, 1.0))
        goalSelector.add(6, LookAtEntityGoal(this, PlayerEntity::class.java, 8.0f))
        goalSelector.add(6, LookAroundGoal(this))
        targetSelector.add(1, RevengeGoal(this, *arrayOfNulls(0)))
    }

    object DefaultAttribute {
        fun build(): DefaultAttributeContainer =
            createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25).build()
    }

    override fun playStepSound(pos: BlockPos?, state: BlockState?) {
        playSound(getStepSound(), 0.15f, 1.0f)
    }

    private fun getStepSound(): SoundEvent = SoundEvents.ENTITY_SKELETON_STEP

    override fun getGroup(): EntityGroup = EntityGroup.UNDEAD

    override fun tickMovement() {
        var bl = this.isInDaylight
        if (bl) {
            val itemStack = getEquippedStack(EquipmentSlot.HEAD)
            if (!itemStack.isEmpty) {
                if (itemStack.isDamageable) {
                    itemStack.damage = itemStack.damage + random.nextInt(2)
                    if (itemStack.damage >= itemStack.maxDamage) {
                        sendEquipmentBreakStatus(EquipmentSlot.HEAD)
                        equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY)
                    }
                }
                bl = false
            }
            if (bl) setOnFireFor(8)
        }
        super.tickMovement()
        if (world is ServerWorld) tickAngerLogic(world as ServerWorld, true)
    }

    override fun tickRiding() {
        super.tickRiding()
        if (vehicle is PathAwareEntity) {
            val pathAwareEntity = vehicle as PathAwareEntity
            bodyYaw = pathAwareEntity.bodyYaw
        }
    }

    override fun initEquipment(difficulty: LocalDifficulty?) {
        super.initEquipment(difficulty)
        equipStack(EquipmentSlot.MAINHAND, ItemStack(Items.BOW))
    }

    override fun initialize(
        world: WorldAccess?,
        difficulty: LocalDifficulty,
        spawnReason: SpawnReason?,
        entityData: EntityData?,
        entityTag: CompoundTag?
    ): EntityData? {
        val superEntityData = super.initialize(world, difficulty, spawnReason, entityData, entityTag)
        initEquipment(difficulty)
        updateEnchantments(difficulty)
        updateAttackType()
        setCanPickUpLoot(random.nextFloat() < 0.55f * difficulty.clampedLocalDifficulty)
        if (getEquippedStack(EquipmentSlot.HEAD).isEmpty) {
            val localDate = LocalDate.now()
            val day = localDate[ChronoField.DAY_OF_MONTH]
            val month = localDate[ChronoField.MONTH_OF_YEAR]
            if (month == 10 && day == 31 && random.nextFloat() < 0.25f) {
                equipStack(
                    EquipmentSlot.HEAD,
                    ItemStack(if (random.nextFloat() < 0.1f) Blocks.JACK_O_LANTERN else Blocks.CARVED_PUMPKIN)
                )
                armorDropChances[EquipmentSlot.HEAD.entitySlotId] = 0.0f
            }
        }
        return superEntityData
    }

    private fun updateAttackType() {
        if (world != null && !world.isClient) {
            goalSelector.remove(meleeAttackGoal)
            goalSelector.remove(bowAttackGoal)
            val itemStack = getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW))
            if (itemStack.item === Items.BOW) {
                var i = 20
                if (world.difficulty != Difficulty.HARD) {
                    i = 40
                }
                bowAttackGoal.setAttackInterval(i)
                goalSelector.add(4, bowAttackGoal)
            } else {
                goalSelector.add(4, meleeAttackGoal)
            }
        }
    }

    override fun attack(target: LivingEntity, pullProgress: Float) {
        if (angryAt != target.uuid) return
        val itemStack =
            getArrowType(getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW)))
        val persistentProjectileEntity = createArrowProjectile(itemStack, pullProgress)
        val d = target.x - this.x
        val e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.y
        val f = target.z - this.z
        val g = MathHelper.sqrt(d * d + f * f).toDouble()
        persistentProjectileEntity.setVelocity(
            d,
            e + g * 0.20000000298023224,
            f,
            1.6f,
            (14 - world.difficulty.id * 4).toFloat()
        )
        playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0f, 1.0f / (getRandom().nextFloat() * 0.4f + 0.8f))
        world.spawnEntity(persistentProjectileEntity)
    }

    private fun createArrowProjectile(
        arrow: ItemStack?,
        damageModifier: Float
    ): PersistentProjectileEntity = ProjectileUtil.createArrowProjectile(this, arrow, damageModifier)

    override fun canUseRangedWeapon(weapon: RangedWeaponItem): Boolean {
        return weapon === Items.BOW
    }

    override fun readCustomDataFromTag(tag: CompoundTag?) {
        super.readCustomDataFromTag(tag)
        updateAttackType()
    }

    override fun equipStack(slot: EquipmentSlot?, stack: ItemStack?) {
        super.equipStack(slot, stack)
        if (!world.isClient) {
            updateAttackType()
        }
    }

    override fun getActiveEyeHeight(pose: EntityPose?, dimensions: EntityDimensions?): Float = 1.74f

    override fun getHeightOffset(): Double = -0.6

    override fun getAmbientSound(): SoundEvent = SoundEvents.ENTITY_SKELETON_AMBIENT

    override fun getHurtSound(source: DamageSource?): SoundEvent = SoundEvents.ENTITY_SKELETON_HURT

    override fun getDeathSound(): SoundEvent = SoundEvents.ENTITY_SKELETON_DEATH

    override fun dropEquipment(source: DamageSource, lootingMultiplier: Int, allowDrops: Boolean) {
        super.dropEquipment(source, lootingMultiplier, allowDrops)
        val entity = source.attacker
        if (entity is CreeperEntity) {
            if (entity.shouldDropHead()) {
                entity.onHeadDropped()
                this.dropItem(Items.SKELETON_SKULL)
            }
        }
    }

    class SkellyBowAttackGoal(private val actor: Skelly, speed: Double, attackInterval: Int, range: Float) :
        BowAttackGoal<Skelly>(actor, speed, attackInterval, range) {
        override fun canStart(): Boolean {
            if (actor.angryAt == null) return false
            return super.canStart()
        }
    }

    private var angerTime: Int = 0
    private var angryAt: UUID? = null

    override fun getAngerTime(): Int = angerTime

    override fun setAngerTime(ticks: Int) {
        this.angerTime = ticks
    }

    override fun chooseRandomAngerTime() {
        setAngerTime(50)
    }

    override fun getAngryAt(): UUID? = angryAt

    override fun setAngryAt(uuid: UUID?) {
        angryAt = uuid
    }

}