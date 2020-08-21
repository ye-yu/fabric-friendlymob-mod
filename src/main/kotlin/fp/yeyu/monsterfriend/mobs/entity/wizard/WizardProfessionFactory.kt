package fp.yeyu.monsterfriend.mobs.entity.wizard

import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

object WizardProfessionFactory {
    val professionMap = HashMap<Item, WizardProfession>()
    object Enchanter : WizardProfession {
        init {
            professionMap[Items.DANDELION] = this
        }

        override val firstLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.VANISHING_CURSE] = Enchantments.VANISHING_CURSE.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.BINDING_CURSE] = Enchantments.BINDING_CURSE.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.BANE_OF_ARTHROPODS] = Enchantments.BANE_OF_ARTHROPODS.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.LUCK_OF_THE_SEA] = Enchantments.LUCK_OF_THE_SEA.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.LURE] = Enchantments.LURE.maxLevel }, this) },

            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.PROTECTION] = Enchantments.PROTECTION.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.FIRE_PROTECTION] = Enchantments.FIRE_PROTECTION.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.FEATHER_FALLING] = Enchantments.FEATHER_FALLING.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.BLAST_PROTECTION] = Enchantments.BLAST_PROTECTION.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.PROJECTILE_PROTECTION] = Enchantments.PROJECTILE_PROTECTION.maxLevel }, this) }
        )

        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.LOYALTY] = Enchantments.LOYALTY.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.IMPALING] = Enchantments.IMPALING.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.RIPTIDE] = Enchantments.RIPTIDE.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.CHANNELING] = Enchantments.CHANNELING.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.MULTISHOT] = Enchantments.MULTISHOT.maxLevel }, this) },

            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.RESPIRATION] = Enchantments.RESPIRATION.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.AQUA_AFFINITY] = Enchantments.AQUA_AFFINITY.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.THORNS] = Enchantments.THORNS.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.DEPTH_STRIDER] = Enchantments.DEPTH_STRIDER.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.FROST_WALKER] = Enchantments.FROST_WALKER.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.SOUL_SPEED] = Enchantments.SOUL_SPEED.maxLevel }, this) }
        )

        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.MENDING] = Enchantments.MENDING.maxLevel }, this) },

            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.POWER] = Enchantments.POWER.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.PUNCH] = Enchantments.PUNCH.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.FLAME] = Enchantments.FLAME.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.INFINITY] = Enchantments.INFINITY.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.QUICK_CHARGE] = Enchantments.QUICK_CHARGE.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.PIERCING] = Enchantments.PIERCING.maxLevel }, this) },

            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.EFFICIENCY] = Enchantments.EFFICIENCY.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.SILK_TOUCH] = Enchantments.SILK_TOUCH.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.UNBREAKING] = Enchantments.UNBREAKING.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.FORTUNE] = Enchantments.FORTUNE.maxLevel }, this) }

        )

        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.SHARPNESS] = Enchantments.SHARPNESS.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.SMITE] = Enchantments.SMITE.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.KNOCKBACK] = Enchantments.KNOCKBACK.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.FIRE_ASPECT] = Enchantments.FIRE_ASPECT.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.LOOTING] = Enchantments.LOOTING.maxLevel }, this) },
            ItemStack(Items.ENCHANTED_BOOK).apply { EnchantmentHelper.set(HashMap<Enchantment, Int>().apply{ this[Enchantments.SWEEPING] = Enchantments.SWEEPING.maxLevel }, this) }
        )
    }
}