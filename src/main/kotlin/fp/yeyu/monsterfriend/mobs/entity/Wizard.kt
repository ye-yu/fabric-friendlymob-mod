package fp.yeyu.monsterfriend.mobs.entity

import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.RangedAttackMob
import net.minecraft.entity.ai.goal.*
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.Angerable
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.thrown.PotionEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.potion.PotionUtil
import net.minecraft.potion.Potions
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import java.util.*

class Wizard(entityType: EntityType<out PathAwareEntity>?, world: World?) : PathAwareEntity(entityType, world),
    RangedAttackMob, Angerable {

    private val anger = Anger(-1, null)

    override fun initGoals() {
        super.initGoals()
        this.goalSelector.add(1, SwimGoal(this))
        this.goalSelector.add(2, AngerableProjectileAttackGoal(this, 1.0, 60, 10.0f))
        this.goalSelector.add(2, RevengeGoal(this, *arrayOfNulls(0)))
        this.goalSelector.add(3, UniversalAngerGoal(this, true))
        this.goalSelector.add(3, LookAtEntityGoal(this, PlayerEntity::class.java, 8.0f))
        this.goalSelector.add(4, LookAroundGoal(this))
    }

    override fun attack(target: LivingEntity, pullProgress: Float) {
        val targetVelocity = target.velocity
        val throwX = target.x + targetVelocity.x - this.x
        val throwY = target.eyeY - 1.1 - this.y
        val throwZ = target.z + targetVelocity.z - this.z
        val rootSquaredDistance = MathHelper.sqrt(throwX * throwX + throwZ * throwZ)
        var potion = Potions.HARMING

        if (rootSquaredDistance >= 8.0f && !target.hasStatusEffect(StatusEffects.SLOWNESS)) {
            potion = Potions.SLOWNESS
        } else if (target.health >= 8.0f && !target.hasStatusEffect(StatusEffects.POISON)) {
            potion = Potions.POISON
        } else if (rootSquaredDistance <= 3.0f && !target.hasStatusEffect(StatusEffects.WEAKNESS) && random.nextFloat() < 0.25f) {
            potion = Potions.WEAKNESS
        }

        val potionEntity = PotionEntity(world, this)
        potionEntity.setItem(PotionUtil.setPotion(ItemStack(Items.SPLASH_POTION), potion))
        potionEntity.pitch -= -20.0f
        potionEntity.setVelocity(throwX, throwY + (rootSquaredDistance * 0.2f).toDouble(), throwZ, 0.75f, 8.0f)
        if (!this.isSilent) {
            world.playSound(
                null as PlayerEntity?,
                this.x,
                this.y,
                this.z,
                SoundEvents.ENTITY_WITCH_THROW,
                this.soundCategory,
                1.0f,
                0.8f + random.nextFloat() * 0.4f
            )
        }
        world.spawnEntity(potionEntity)
    }

    override fun getAngerTime(): Int {
        return anger.angerTime
    }

    override fun setAngerTime(ticks: Int) {
        anger.angerTime = ticks
    }

    override fun chooseRandomAngerTime() {
        anger.angerTime = 50 + random.nextInt(50)
    }

    override fun getAngryAt(): UUID? {
        return anger.angryAt
    }

    override fun setAngryAt(uuid: UUID?) {
        anger.angryAt = uuid
    }

    override fun tickMovement() {
        super.tickMovement()
        if (world is ServerWorld) tickAngerLogic(world as ServerWorld, true)
    }

    data class Anger(var angerTime: Int, var angryAt: UUID?)

    class AngerableProjectileAttackGoal<T>(val mob: T, mobSpeed: Double, intervalTicks: Int, maxShootRange: Float) :
        ProjectileAttackGoal(mob, mobSpeed, intervalTicks, maxShootRange) where T : RangedAttackMob, T : Angerable {

        override fun canStart(): Boolean {
            if (mob.angryAt == null) return false
            return super.canStart()
        }
    }
}