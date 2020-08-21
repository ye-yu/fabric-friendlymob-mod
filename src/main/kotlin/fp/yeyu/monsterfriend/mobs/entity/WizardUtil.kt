package fp.yeyu.monsterfriend.mobs.entity

import fp.yeyu.monsterfriend.mobs.MobRegistry
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionUtil.setPotion
import net.minecraft.potion.Potions
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting
import net.minecraft.util.registry.Registry
import org.apache.commons.lang3.StringUtils
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.pow

object WizardUtil {

    object PotionUtil {

        private val forbiddenPotions = listOf(
            Potions.EMPTY, Potions.WATER, Potions.AWKWARD, Potions.THICK, Potions.MUNDANE
        )

        val potionType = listOf(
            Items.POTION,
            Items.SPLASH_POTION,
            Items.LINGERING_POTION
        )

        private val potionWeight = intArrayOf(
            5, 4, 1
        )

        fun createRandomPotion(random: Random): ItemStack {
            val potion: Potion = getRandomPotion(random)
            return setPotion(ItemStack(random.weightedChoice(potionType, potionWeight)), potion)
        }

        private fun getRandomPotion(random: Random): Potion {
            var potion: Potion
            do {
                potion = Registry.POTION.getRandom(random)
            } while (forbiddenPotions.contains(potion))
            return potion
        }
    }

    object ItemUtil {
        private val forbiddenItems = listOf(
            // command blocks
            Items.COMMAND_BLOCK,
            Items.COMMAND_BLOCK_MINECART,
            Items.CHAIN_COMMAND_BLOCK,
            Items.REPEATING_COMMAND_BLOCK,

            // structure blocks
            Items.STRUCTURE_BLOCK,
            Items.STRUCTURE_VOID,
            Items.JIGSAW,

            // can't give an empty written book
            Items.WRITTEN_BOOK,
            Items.KNOWLEDGE_BOOK,

            // forbidden blocks
            Items.AIR,
            Items.BEDROCK,
            Items.END_PORTAL_FRAME,
            Items.BARRIER,

            // armorer
            Items.CHAINMAIL_BOOTS,
            Items.CHAINMAIL_CHESTPLATE,
            Items.CHAINMAIL_HELMET,
            Items.CHAINMAIL_LEGGINGS,

            // cartographer
            Items.MAP,
            Items.FILLED_MAP
        )

        private val flowers = listOf(
            Items.DANDELION,
            Items.POPPY,
            Items.BLUE_ORCHID,
            Items.ALLIUM,
            Items.AZURE_BLUET,
            Items.ORANGE_TULIP,
            Items.PINK_TULIP,
            Items.RED_TULIP,
            Items.WHITE_TULIP,
            Items.OXEYE_DAISY,
            Items.CORNFLOWER,
            Items.LILY_OF_THE_VALLEY,
            Items.SUNFLOWER,
            Items.LILAC,
            Items.ROSE_BUSH,
            Items.PEONY
        )

        private val unobtainables = listOf(
            Items.SPAWNER,
            Items.BAT_SPAWN_EGG,
            Items.BEE_SPAWN_EGG,
            Items.BLAZE_SPAWN_EGG,
            Items.CAT_SPAWN_EGG,
            Items.CAVE_SPIDER_SPAWN_EGG,
            Items.CHICKEN_SPAWN_EGG,
            Items.COD_SPAWN_EGG,
            Items.COW_SPAWN_EGG,
            Items.CREEPER_SPAWN_EGG,
            Items.DOLPHIN_SPAWN_EGG,
            Items.DONKEY_SPAWN_EGG,
            Items.DROWNED_SPAWN_EGG,
            Items.ELDER_GUARDIAN_SPAWN_EGG,
            Items.ENDERMAN_SPAWN_EGG,
            Items.ENDERMITE_SPAWN_EGG,
            Items.EVOKER_SPAWN_EGG,
            Items.FOX_SPAWN_EGG,
            Items.GHAST_SPAWN_EGG,
            Items.GUARDIAN_SPAWN_EGG,
            Items.HOGLIN_SPAWN_EGG,
            Items.HORSE_SPAWN_EGG,
            Items.HUSK_SPAWN_EGG,
            Items.LLAMA_SPAWN_EGG,
            Items.MAGMA_CUBE_SPAWN_EGG,
            Items.MOOSHROOM_SPAWN_EGG,
            Items.MULE_SPAWN_EGG,
            Items.OCELOT_SPAWN_EGG,
            Items.PANDA_SPAWN_EGG,
            Items.PARROT_SPAWN_EGG,
            Items.PHANTOM_SPAWN_EGG,
            Items.PIG_SPAWN_EGG,
            Items.PIGLIN_SPAWN_EGG,
            Items.PILLAGER_SPAWN_EGG,
            Items.POLAR_BEAR_SPAWN_EGG,
            Items.PUFFERFISH_SPAWN_EGG,
            Items.RABBIT_SPAWN_EGG,
            Items.RAVAGER_SPAWN_EGG,
            Items.SALMON_SPAWN_EGG,
            Items.SHEEP_SPAWN_EGG,
            Items.SHULKER_SPAWN_EGG,
            Items.SILVERFISH_SPAWN_EGG,
            Items.SKELETON_SPAWN_EGG,
            Items.SKELETON_HORSE_SPAWN_EGG,
            Items.SLIME_SPAWN_EGG,
            Items.SPIDER_SPAWN_EGG,
            Items.SQUID_SPAWN_EGG,
            Items.STRAY_SPAWN_EGG,
            Items.STRIDER_SPAWN_EGG,
            Items.TRADER_LLAMA_SPAWN_EGG,
            Items.TROPICAL_FISH_SPAWN_EGG,
            Items.TURTLE_SPAWN_EGG,
            Items.VEX_SPAWN_EGG,
            Items.VILLAGER_SPAWN_EGG,
            Items.VINDICATOR_SPAWN_EGG,
            Items.WANDERING_TRADER_SPAWN_EGG,
            Items.WITCH_SPAWN_EGG,
            Items.WITHER_SKELETON_SPAWN_EGG,
            Items.WOLF_SPAWN_EGG,
            Items.ZOGLIN_SPAWN_EGG,
            Items.ZOMBIE_SPAWN_EGG,
            Items.ZOMBIE_HORSE_SPAWN_EGG,
            Items.ZOMBIE_VILLAGER_SPAWN_EGG,
            Items.ZOMBIFIED_PIGLIN_SPAWN_EGG,
            MobRegistry.vindor.egg,
            MobRegistry.evione.egg,
            MobRegistry.wizard.egg,

            Items.INFESTED_CHISELED_STONE_BRICKS,
            Items.INFESTED_COBBLESTONE,
            Items.INFESTED_CRACKED_STONE_BRICKS,
            Items.INFESTED_MOSSY_STONE_BRICKS,
            Items.INFESTED_STONE,
            Items.INFESTED_STONE_BRICKS,

            Items.GRASS_PATH,
            Items.FARMLAND,
            Items.COBWEB
        )

        fun createRandomItem(random: Random, withUnobtainables: Boolean): ItemStack {
            return ItemStack(if (withUnobtainables) getRandomItemWithUnobtainables(random) else getRandomItem(random))
        }

        fun createRandomFlower(random: Random): ItemStack {
            return ItemStack(random.choice(flowers))
        }

        private fun getRandomItem(random: Random): Item {
            var item: Item
            do {
                item = Registry.ITEM.getRandom(random)
            } while (forbiddenItems.contains(item) || flowers.contains(item) || PotionUtil.potionType.contains(item) || unobtainables.contains(
                    item
                )
            )
            return item
        }

        private fun getRandomItemWithUnobtainables(random: Random): Item {
            var item: Item
            do {
                item = Registry.ITEM.getRandom(random)
            } while (forbiddenItems.contains(item) || flowers.contains(item) || PotionUtil.potionType.contains(item))
            return item
        }
    }

    object EnchantmentBookUtil {
        private val BOOK = ItemStack(Items.BOOK).apply {
            this.removeCustomName()
            this.setCustomName(LiteralText("Enchanted Book").formatted(Formatting.RESET, Formatting.YELLOW))
        }

        fun createRandomEnchantedBook(level: Int, random: Random): ItemStack {
            val book = BOOK.copy()
            val possibleEntries = EnchantmentHelper.getPossibleEntries(level, book, false)
            val enchantment: HashMap<Enchantment, Int> = random.choice(possibleEntries).toMap().apply {
                keys.forEach {
                    this[it] = it.maxLevel
                }
            }

            EnchantmentHelper.set(enchantment, book)
            return book
        }
    }

    object LevelUtil {
        private const val BASE_EXP_PER_LEVEL = 10
        private const val EXP_MULTIPLIER = 1.8f
        const val MAX_LEVEL = 5

        fun canLevelUp(currentExp: Int, incomingExp: Int): Boolean {
            return getRemainingToLevelUp(currentExp) <= incomingExp
        }

        fun getCurrentLevel(exp: Int): Int {
            var count = 0
            var reduce = exp
            do {
                count++
                reduce -= BASE_EXP_PER_LEVEL * EXP_MULTIPLIER.toDouble().pow(count.toDouble()).toInt()
            } while (reduce >= 0)
            return count
        }

        fun getRemainingToLevelUp(currentExp: Int): Int {
            var count = 0
            var upperExp = 0
            do {
                count++
                upperExp += BASE_EXP_PER_LEVEL * EXP_MULTIPLIER.toDouble().pow(count.toDouble()).toInt()
            } while (upperExp <= currentExp)
            return upperExp - currentExp
        }

        fun getRemainderExp(currentExp: Int): Int {
            var count = 0
            var reduce = currentExp
            do {
                count++
                reduce -= BASE_EXP_PER_LEVEL * EXP_MULTIPLIER.toDouble().pow(count.toDouble()).toInt()
            } while (reduce >= 0)

            // recover last
            reduce += BASE_EXP_PER_LEVEL * EXP_MULTIPLIER.toDouble().pow(count.toDouble()).toInt()
            return reduce
        }
    }

    private fun <T> Random.weightedChoice(list: List<T>, probabilityWeight: IntArray): T {
        val total = probabilityWeight.sum()
        val random = nextInt(total)
        var cumulative = 0
        for (index in list.indices) {
            val element = list[index]
            val weight = probabilityWeight[index]
            cumulative += weight
            if (random < cumulative) return element
        }
        throw RuntimeException("Unknown error! Weights: ${StringUtils.join(probabilityWeight, ",")}, Rolled: $random")
    }

    private fun <T> Random.choice(list: List<T>): T {
        return list[nextInt(list.size)]
    }

    private fun EnchantmentLevelEntry.toMap(): java.util.HashMap<Enchantment, Int> {
        val map = HashMap<Enchantment, Int>(1)
        map[enchantment] = level
        return map
    }
}
