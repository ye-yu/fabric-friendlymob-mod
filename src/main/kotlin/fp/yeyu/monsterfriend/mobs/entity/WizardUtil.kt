package fp.yeyu.monsterfriend.mobs.entity

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionUtil.setPotion
import net.minecraft.potion.Potions
import net.minecraft.util.registry.Registry
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.pow

object WizardUtil {

    object PotionUtil {

        private val forbiddenPotions = listOf(
            Potions.EMPTY, Potions.WATER, Potions.AWKWARD, Potions.THICK, Potions.MUNDANE
        )

        private val potionType = listOf(
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
            Items.COMMAND_BLOCK,
            Items.COMMAND_BLOCK_MINECART,
            Items.CHAIN_COMMAND_BLOCK,
            Items.REPEATING_COMMAND_BLOCK,
            Items.STRUCTURE_BLOCK,
            Items.STRUCTURE_VOID,
            Items.JIGSAW,
            Items.WRITTEN_BOOK,
            Items.AIR
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

        fun createRandomItem(random: Random): ItemStack {
            return ItemStack(getRandomItem(random))
        }

        fun createRandomFlower(random: Random): ItemStack {
            return ItemStack(random.choice(flowers))
        }

        private fun getRandomItem(random: Random): Item {
            var item: Item
            do {
                item = Registry.ITEM.getRandom(random)
            } while(forbiddenItems.contains(item) || flowers.contains(item))
            return item
        }
    }

    object EnchantmentBookUtil {
        private val BOOK = ItemStack(Items.BOOK)

        fun createRandomEnchantedBook(level: Int, random: Random): ItemStack {
            val book = BOOK.copy()
            val possibleEntries = EnchantmentHelper.getPossibleEntries(level, book, false)
            val enchantment: HashMap<Enchantment, Int> = random.choice(possibleEntries).toMap()

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
            } while(reduce >= 0)
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
            } while(reduce >= 0)

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
        throw RuntimeException("Unknown error!")
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
