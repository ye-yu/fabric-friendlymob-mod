package fp.yeyu.monsterfriend.mobs.entity

import fp.yeyu.monsterfriend.annotation.Development
import io.github.yeyu.util.Logger
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
import net.minecraft.nbt.CompoundTag
import net.minecraft.potion.PotionUtil
import net.minecraft.potion.Potions
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import java.util.*

class Wizard(entityType: EntityType<out PathAwareEntity>?, world: World?) : PathAwareEntity(entityType, world),
    RangedAttackMob, Angerable {

    private val anger = Anger(-1, null)
    private val learntRecipe = LearntRecipe()
    var experience = 0

    val currentLevel get() = WizardUtil.LevelUtil.getCurrentLevel(experience)
    val remainingExp get() = WizardUtil.LevelUtil.getRemainderExp(experience)
    var debugTick = 0

    init {
        if (world is ServerWorld) makeNewCraft()
    }

    @Development
    private fun debugTick() {
    }

    private fun craftSuccessful(reward: Int) {
        if (currentLevel < WizardUtil.LevelUtil.MAX_LEVEL) {
            if (WizardUtil.LevelUtil.canLevelUp(experience, reward)) {
                playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, (0.8f + random.nextDouble() * 0.4f).toFloat())
                makeNewCraft()
            }
            experience += reward
        }
    }

    private fun makeNewCraft() {
        val index = currentLevel - 1
        val craftMaker: () -> ItemStack = { WizardUtil.ItemUtil.createRandomItem(random, true) }
        val bookMaker: () -> ItemStack = {
            WizardUtil.EnchantmentBookUtil.createRandomEnchantedBook(
                currentLevel.coerceAtLeast(1).coerceAtMost(3),
                random
            )
        }
        val itemMaker: () -> ItemStack = { WizardUtil.ItemUtil.createRandomItem(random, false) }
        val flowerMaker: () -> ItemStack = { WizardUtil.ItemUtil.createRandomFlower(random) }
        val potionMaker: () -> ItemStack = { WizardUtil.PotionUtil.createRandomPotion(random) }
        learntRecipe.recipes[index] = CustomRecipe(
            if (random.nextBoolean()) craftMaker() else bookMaker(),
            itemMaker(),
            itemMaker(),
            flowerMaker(),
            potionMaker(),
            currentLevel * 2
        )
        Logger.info("Created: ${learntRecipe.recipes[index]}")
    }

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

    override fun getAngerTime(): Int = anger.angerTime

    override fun setAngerTime(ticks: Int) {
        anger.angerTime = ticks
    }

    override fun chooseRandomAngerTime() {
        anger.angerTime = 50 + random.nextInt(50)
    }

    override fun getAngryAt(): UUID? = anger.angryAt

    override fun setAngryAt(uuid: UUID?) {
        anger.angryAt = uuid
    }

    override fun tickMovement() {
        super.tickMovement()
        if (world is ServerWorld) tickAngerLogic(world as ServerWorld, true)
        if (world is ServerWorld) debugTick()
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        val compoundTag = super.toTag(tag)
        learntRecipe.toTag(compoundTag)
        return compoundTag
    }

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)
        learntRecipe.fromTag(tag)
    }

    data class Anger(var angerTime: Int, var angryAt: UUID?)

    class AngerableProjectileAttackGoal<T>(val mob: T, mobSpeed: Double, intervalTicks: Int, maxShootRange: Float) :
        ProjectileAttackGoal(mob, mobSpeed, intervalTicks, maxShootRange) where T : RangedAttackMob, T : Angerable {

        override fun canStart(): Boolean {
            if (mob.angryAt == null) return false
            return super.canStart()
        }
    }

    data class CustomRecipe(
        val toCraft: ItemStack,
        val item1: ItemStack,
        val item2: ItemStack,
        val flower: ItemStack,
        val potion: ItemStack,
        val expReward: Int
    ) {
        fun craft(item1: ItemStack, item2: ItemStack, flower: ItemStack, potion: ItemStack): ItemStack {
            return if (isIdentical(this.item1, item1) && isIdentical(this.item2, item2) && isIdentical(
                    this.flower,
                    flower
                ) && isIdentical(this.potion, potion)
            )
                toCraft
            else ItemStack.EMPTY
        }

        private fun isIdentical(item1: ItemStack, item2: ItemStack): Boolean {
            return ScreenHandler.canStacksCombine(item1, item2)
        }

        override fun toString(): String {
            return if (toCraft == ItemStack.EMPTY) "Empty recipe"
            else "${toCraft.item} <- ${item1.item} + ${item2.item} + ${flower.item} + ${potion.item} : $expReward exp"
        }

        companion object {
            private const val TAG_PREFIX = "wizard-recipe-"
            private const val CRAFT = "-item-craft"
            private const val ITEM1 = "-item-item1"
            private const val ITEM2 = "-item-item2"
            private const val FLOWER = "-item-flower"
            private const val POTION = "-item-potion"
            private const val REWARD = "-reward"

            val EMPTY =
                CustomRecipe(
                    ItemStack.EMPTY,
                    ItemStack.EMPTY,
                    ItemStack.EMPTY,
                    ItemStack.EMPTY,
                    ItemStack.EMPTY,
                    0
                )

            fun toTag(customRecipe: CustomRecipe, index: Int, tag: CompoundTag) {
                val itemTag = List(5) { CompoundTag() }
                customRecipe.toCraft.toTag(itemTag[0])
                customRecipe.item1.toTag(itemTag[1])
                customRecipe.item2.toTag(itemTag[2])
                customRecipe.flower.toTag(itemTag[3])
                customRecipe.potion.toTag(itemTag[4])

                tag.put("$TAG_PREFIX$index$CRAFT", itemTag[0])
                tag.put("$TAG_PREFIX$index$ITEM1", itemTag[1])
                tag.put("$TAG_PREFIX$index$ITEM2", itemTag[2])
                tag.put("$TAG_PREFIX$index$FLOWER", itemTag[3])
                tag.put("$TAG_PREFIX$index$POTION", itemTag[4])
                tag.putInt("$TAG_PREFIX$index$REWARD", customRecipe.expReward)
            }

            fun fromTag(index: Int, tag: CompoundTag): CustomRecipe {
                return if (!tag.contains("$TAG_PREFIX$index$CRAFT")) EMPTY
                else CustomRecipe(
                    ItemStack.fromTag(tag.getCompound("$TAG_PREFIX$index$CRAFT")),
                    ItemStack.fromTag(tag.getCompound("$TAG_PREFIX$index$ITEM1")),
                    ItemStack.fromTag(tag.getCompound("$TAG_PREFIX$index$ITEM2")),
                    ItemStack.fromTag(tag.getCompound("$TAG_PREFIX$index$FLOWER")),
                    ItemStack.fromTag(tag.getCompound("$TAG_PREFIX$index$POTION")),
                    tag.getInt("$TAG_PREFIX$index$REWARD")
                )
            }
        }
    }

    class LearntRecipe {
        val recipes = MutableList(WizardUtil.LevelUtil.MAX_LEVEL) { CustomRecipe.EMPTY }
        fun fromTag(tag: CompoundTag) {
            for (i in recipes.indices) {
                recipes[i] = CustomRecipe.fromTag(i, tag)
                Logger.info("Deserialised: ${recipes[i]}")
            }
        }

        fun toTag(tag: CompoundTag) {
            for (i in recipes.indices) {
                CustomRecipe.toTag(recipes[i], i, tag)
            }
        }
    }
}