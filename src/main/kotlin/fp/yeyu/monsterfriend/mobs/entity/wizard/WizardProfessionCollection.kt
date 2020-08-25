package fp.yeyu.monsterfriend.mobs.entity.wizard

import io.github.yeyu.util.Logger
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.registry.Registry
import java.util.*
import kotlin.collections.HashMap
import kotlin.streams.toList

object WizardProfessionCollection {
    val professionMap = HashMap<Item, WizardProfession>().apply {
        this[Items.DANDELION] = Enchanter
        this[Items.POPPY] = Enchanter2
        this[Items.BLUE_ORCHID] = Enchanter3
        this[Items.ALLIUM] = Spawner
        this[Items.AZURE_BLUET] = Spawner2
        this[Items.ORANGE_TULIP] = Spawner3
        this[Items.PINK_TULIP] = Armorer
        this[Items.RED_TULIP] = Armorer2
        this[Items.WHITE_TULIP] = Weaponry
        this[Items.OXEYE_DAISY] = Toolsmith
        this[Items.CORNFLOWER] = Electrician
        this[Items.SUNFLOWER] = Miner
        this[Items.LILAC] = BiomeEnthusiast
        this[Items.ROSE_BUSH] = MobDropHoarder
        this[Items.PEONY] = Foodist
        this[Items.WITHER_ROSE] = BlackMarketer
    }

    object Enchanter : WizardProfession {
        override val firstLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.VANISHING_CURSE] = Enchantments.VANISHING_CURSE.maxLevel
                }, this)
            },

            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.VANISHING_CURSE] = Enchantments.VANISHING_CURSE.maxLevel
                }, this)
            },

            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.BLAST_PROTECTION] = Enchantments.BLAST_PROTECTION.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.PROJECTILE_PROTECTION] = Enchantments.PROJECTILE_PROTECTION.maxLevel
                }, this)
            }
        )

        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.LOYALTY] = Enchantments.LOYALTY.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.MULTISHOT] = Enchantments.MULTISHOT.maxLevel
                }, this)
            },

            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.RESPIRATION] = Enchantments.RESPIRATION.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.DEPTH_STRIDER] = Enchantments.DEPTH_STRIDER.maxLevel
                }, this)
            }
        )

        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.MENDING] = Enchantments.MENDING.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.POWER] = Enchantments.POWER.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.PUNCH] = Enchantments.PUNCH.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.UNBREAKING] = Enchantments.UNBREAKING.maxLevel
                }, this)
            }
        )

        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.SHARPNESS] = Enchantments.SHARPNESS.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.SMITE] = Enchantments.SMITE.maxLevel
                }, this)
            }
        )
    }

    object Enchanter2 : WizardProfession {

        override val firstLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.BINDING_CURSE] = Enchantments.BINDING_CURSE.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.BINDING_CURSE] = Enchantments.BINDING_CURSE.maxLevel
                }, this)
            },

            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.LUCK_OF_THE_SEA] = Enchantments.LUCK_OF_THE_SEA.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.FIRE_PROTECTION] = Enchantments.FIRE_PROTECTION.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.FEATHER_FALLING] = Enchantments.FEATHER_FALLING.maxLevel
                }, this)
            }
        )
        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.IMPALING] = Enchantments.IMPALING.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.RIPTIDE] = Enchantments.RIPTIDE.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.FROST_WALKER] = Enchantments.FROST_WALKER.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.SOUL_SPEED] = Enchantments.SOUL_SPEED.maxLevel
                }, this)
            }
        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.FLAME] = Enchantments.FLAME.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.INFINITY] = Enchantments.INFINITY.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.QUICK_CHARGE] = Enchantments.QUICK_CHARGE.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.PIERCING] = Enchantments.PIERCING.maxLevel
                }, this)
            }
        )
        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.KNOCKBACK] = Enchantments.KNOCKBACK.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.FIRE_ASPECT] = Enchantments.FIRE_ASPECT.maxLevel
                }, this)
            }
        )
    }

    object Enchanter3 : WizardProfession {

        override val firstLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.BANE_OF_ARTHROPODS] = Enchantments.BANE_OF_ARTHROPODS.maxLevel
                }, this)
            },

            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.LURE] = Enchantments.LURE.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.BANE_OF_ARTHROPODS] = Enchantments.BANE_OF_ARTHROPODS.maxLevel
                }, this)
            },

            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.LURE] = Enchantments.LURE.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.PROTECTION] = Enchantments.PROTECTION.maxLevel
                }, this)
            }
        )
        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.CHANNELING] = Enchantments.CHANNELING.maxLevel
                }, this)
            },

            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.AQUA_AFFINITY] = Enchantments.AQUA_AFFINITY.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.THORNS] = Enchantments.THORNS.maxLevel
                }, this)
            }
        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.EFFICIENCY] = Enchantments.EFFICIENCY.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.SILK_TOUCH] = Enchantments.SILK_TOUCH.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.FORTUNE] = Enchantments.FORTUNE.maxLevel
                }, this)
            }
        )
        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.LOOTING] = Enchantments.LOOTING.maxLevel
                }, this)
            },
            ItemStack(Items.ENCHANTED_BOOK).apply {
                EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                    this[Enchantments.SWEEPING] = Enchantments.SWEEPING.maxLevel
                }, this)
            }
        )
    }

    object Spawner : WizardProfession {

        override val firstLevelGroup: List<ItemStack> = listOf(
            // passives
            ItemStack(Items.BAT_SPAWN_EGG),
            ItemStack(Items.BEE_SPAWN_EGG),
            ItemStack(Items.DOLPHIN_SPAWN_EGG),

            // passives
            ItemStack(Items.BAT_SPAWN_EGG),
            ItemStack(Items.BEE_SPAWN_EGG),
            ItemStack(Items.DOLPHIN_SPAWN_EGG)
        )
        override val secondLevelGroup: List<ItemStack> = listOf(
            // tameable
            ItemStack(Items.OCELOT_SPAWN_EGG),
            ItemStack(Items.CAT_SPAWN_EGG),
            ItemStack(Items.DONKEY_SPAWN_EGG),

            // edibles
            ItemStack(Items.COD_SPAWN_EGG),
            ItemStack(Items.CHICKEN_SPAWN_EGG),

            // tameable
            ItemStack(Items.OCELOT_SPAWN_EGG),
            ItemStack(Items.CAT_SPAWN_EGG),
            ItemStack(Items.DONKEY_SPAWN_EGG),

            // edibles
            ItemStack(Items.COD_SPAWN_EGG),
            ItemStack(Items.CHICKEN_SPAWN_EGG)

        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.COW_SPAWN_EGG),

            // neutral
            ItemStack(Items.POLAR_BEAR_SPAWN_EGG),

            // hostiles
            ItemStack(Items.BLAZE_SPAWN_EGG),
            ItemStack(Items.CAVE_SPIDER_SPAWN_EGG),
            ItemStack(Items.CREEPER_SPAWN_EGG),
            ItemStack(Items.DROWNED_SPAWN_EGG),

            ItemStack(Items.COW_SPAWN_EGG),

            // neutral
            ItemStack(Items.POLAR_BEAR_SPAWN_EGG),

            // hostiles
            ItemStack(Items.BLAZE_SPAWN_EGG),
            ItemStack(Items.CAVE_SPIDER_SPAWN_EGG),
            ItemStack(Items.CREEPER_SPAWN_EGG),
            ItemStack(Items.DROWNED_SPAWN_EGG)
        )
        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ELDER_GUARDIAN_SPAWN_EGG),
            ItemStack(Items.ENDERMAN_SPAWN_EGG),
            ItemStack(Items.ENDERMITE_SPAWN_EGG),
            ItemStack(Items.EVOKER_SPAWN_EGG),
            ItemStack(Items.GHAST_SPAWN_EGG),
            ItemStack(Items.GUARDIAN_SPAWN_EGG),
            ItemStack(Items.HOGLIN_SPAWN_EGG),
            ItemStack(Items.ELDER_GUARDIAN_SPAWN_EGG),
            ItemStack(Items.ENDERMAN_SPAWN_EGG),
            ItemStack(Items.ENDERMITE_SPAWN_EGG),
            ItemStack(Items.EVOKER_SPAWN_EGG),
            ItemStack(Items.GHAST_SPAWN_EGG),
            ItemStack(Items.GUARDIAN_SPAWN_EGG),
            ItemStack(Items.HOGLIN_SPAWN_EGG)
        )

        override fun getRandom(random: Random, level: Int): ItemStack {
            return super.getRandom(random, level).apply { count = 20 + random.nextInt(10) }
        }
    }

    object Spawner2 : WizardProfession {

        override val firstLevelGroup: List<ItemStack> = listOf(
            // passives
            ItemStack(Items.FOX_SPAWN_EGG),
            ItemStack(Items.LLAMA_SPAWN_EGG),
            ItemStack(Items.PANDA_SPAWN_EGG),
            ItemStack(Items.SQUID_SPAWN_EGG),

            // tameable
            ItemStack(Items.HORSE_SPAWN_EGG),
            ItemStack(Items.MULE_SPAWN_EGG)
        )
        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.PARROT_SPAWN_EGG),

            // edibles
            ItemStack(Items.MOOSHROOM_SPAWN_EGG),
            ItemStack(Items.PIG_SPAWN_EGG),
            ItemStack(Items.RABBIT_SPAWN_EGG),

            // neutral
            ItemStack(Items.TRADER_LLAMA_SPAWN_EGG)
        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            // hostiles
            ItemStack(Items.HUSK_SPAWN_EGG),
            ItemStack(Items.MAGMA_CUBE_SPAWN_EGG),
            ItemStack(Items.PHANTOM_SPAWN_EGG),
            ItemStack(Items.PIGLIN_SPAWN_EGG),
            ItemStack(Items.PILLAGER_SPAWN_EGG)
        )
        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.PUFFERFISH_SPAWN_EGG),
            ItemStack(Items.RAVAGER_SPAWN_EGG),
            ItemStack(Items.SHULKER_SPAWN_EGG),
            ItemStack(Items.SILVERFISH_SPAWN_EGG),
            ItemStack(Items.SKELETON_SPAWN_EGG),
            ItemStack(Items.SLIME_SPAWN_EGG)
        )

        override fun getRandom(random: Random, level: Int): ItemStack {
            return super.getRandom(random, level).apply { count = 20 + random.nextInt(10) }
        }
    }

    object Spawner3 : WizardProfession {

        override val firstLevelGroup: List<ItemStack> = listOf(
            // passives
            ItemStack(Items.TROPICAL_FISH_SPAWN_EGG),
            ItemStack(Items.TURTLE_SPAWN_EGG),
            ItemStack(Items.VILLAGER_SPAWN_EGG),
            ItemStack(Items.WANDERING_TRADER_SPAWN_EGG),
            ItemStack(Items.ZOMBIE_HORSE_SPAWN_EGG),

            // tameable
            ItemStack(Items.SKELETON_HORSE_SPAWN_EGG)
        )
        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.STRIDER_SPAWN_EGG),
            ItemStack(Items.WOLF_SPAWN_EGG),

            // edibles
            ItemStack(Items.SALMON_SPAWN_EGG),
            ItemStack(Items.SHEEP_SPAWN_EGG)

        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            // hostiles
            ItemStack(Items.SPIDER_SPAWN_EGG),
            ItemStack(Items.STRAY_SPAWN_EGG),
            ItemStack(Items.VEX_SPAWN_EGG),
            ItemStack(Items.VINDICATOR_SPAWN_EGG)
        )
        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.WITCH_SPAWN_EGG),
            ItemStack(Items.WITHER_SKELETON_SPAWN_EGG),
            ItemStack(Items.ZOGLIN_SPAWN_EGG),
            ItemStack(Items.ZOMBIE_SPAWN_EGG),
            ItemStack(Items.ZOMBIE_VILLAGER_SPAWN_EGG),
            ItemStack(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG)
        )

        override fun getRandom(random: Random, level: Int): ItemStack {
            return super.getRandom(random, level).apply { count = 20 + random.nextInt(10) }
        }
    }

    object ToolUtil {

        private fun getPossibleEnchants(itemStack: ItemStack): MutableList<Enchantment> {
            return Registry.ENCHANTMENT.stream().filter { it.isAcceptableItem(itemStack) }.toList().toMutableList()
        }

        fun enchant(itemStack: ItemStack, level: Int): ItemStack {
            val enchantments = getPossibleEnchants(itemStack)
            enchantments.shuffle()
            Logger.info("Shuffled entries for $itemStack")
            Logger.info("Entries: ${enchantments.map { it::class.simpleName }.joinToString()}")
            EnchantmentHelper.set(HashMap<Enchantment, Int>().apply {
                var count = 0
                for (enchantment in enchantments) {
                    if (!this.keys.all { it.canCombine(enchantment) }) continue
                    this[enchantment] = enchantment.maxLevel
                    count++
                    if (count > 2) break
                }
                this[Enchantments.UNBREAKING] = 3 + level
            }, itemStack)
            return itemStack
        }
    }

    object Armorer : WizardProfession {

        private val helmets = listOf(
            ItemStack(Items.LEATHER_HELMET),
            ItemStack(Items.CHAINMAIL_HELMET),
            ItemStack(Items.IRON_HELMET),
            ItemStack(Items.GOLDEN_HELMET),
            ItemStack(Items.DIAMOND_HELMET),
            ItemStack(Items.NETHERITE_HELMET)
        )

        private val chestplates = listOf(
            ItemStack(Items.LEATHER_CHESTPLATE),
            ItemStack(Items.CHAINMAIL_CHESTPLATE),
            ItemStack(Items.IRON_CHESTPLATE),
            ItemStack(Items.GOLDEN_CHESTPLATE),
            ItemStack(Items.DIAMOND_CHESTPLATE),
            ItemStack(Items.NETHERITE_CHESTPLATE)
        )

        // helmet & chestplates
        override val firstLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.LEATHER_CHESTPLATE),
            ItemStack(Items.LEATHER_HELMET),
            ItemStack(Items.CHAINMAIL_CHESTPLATE),
            ItemStack(Items.CHAINMAIL_HELMET),
            ItemStack(Items.LEATHER_CHESTPLATE),
            ItemStack(Items.LEATHER_HELMET),
            ItemStack(Items.CHAINMAIL_CHESTPLATE),
            ItemStack(Items.CHAINMAIL_HELMET)
        )
        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.IRON_CHESTPLATE),
            ItemStack(Items.IRON_HELMET),
            ItemStack(Items.GOLDEN_CHESTPLATE),
            ItemStack(Items.GOLDEN_HELMET),
            ItemStack(Items.IRON_CHESTPLATE),
            ItemStack(Items.IRON_HELMET),
            ItemStack(Items.GOLDEN_CHESTPLATE),
            ItemStack(Items.GOLDEN_HELMET)
        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.DIAMOND_CHESTPLATE),
            ItemStack(Items.DIAMOND_HELMET),
            ItemStack(Items.DIAMOND_CHESTPLATE),
            ItemStack(Items.DIAMOND_HELMET)
        )
        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.DIAMOND_CHESTPLATE),
            ItemStack(Items.DIAMOND_HELMET),
            ItemStack(Items.NETHERITE_CHESTPLATE),
            ItemStack(Items.NETHERITE_HELMET)
        )

        override fun getRandom(random: Random, level: Int): ItemStack {
            return ToolUtil.enchant(super.getRandom(random, level), level)
        }
    }

    object Armorer2 : WizardProfession {
        // boots & leggings
        override val firstLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.LEATHER_BOOTS),
            ItemStack(Items.LEATHER_LEGGINGS),
            ItemStack(Items.CHAINMAIL_BOOTS),
            ItemStack(Items.CHAINMAIL_LEGGINGS),
            ItemStack(Items.LEATHER_BOOTS),
            ItemStack(Items.LEATHER_LEGGINGS),
            ItemStack(Items.CHAINMAIL_BOOTS),
            ItemStack(Items.CHAINMAIL_LEGGINGS)
        )
        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.IRON_BOOTS),
            ItemStack(Items.IRON_LEGGINGS),
            ItemStack(Items.GOLDEN_BOOTS),
            ItemStack(Items.GOLDEN_LEGGINGS),
            ItemStack(Items.IRON_BOOTS),
            ItemStack(Items.IRON_LEGGINGS),
            ItemStack(Items.GOLDEN_BOOTS),
            ItemStack(Items.GOLDEN_LEGGINGS)
        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.DIAMOND_BOOTS),
            ItemStack(Items.DIAMOND_LEGGINGS),
            ItemStack(Items.DIAMOND_BOOTS),
            ItemStack(Items.DIAMOND_LEGGINGS)
        )
        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.DIAMOND_BOOTS),
            ItemStack(Items.DIAMOND_LEGGINGS),
            ItemStack(Items.NETHERITE_BOOTS),
            ItemStack(Items.NETHERITE_LEGGINGS)
        )

        override fun getRandom(random: Random, level: Int): ItemStack {
            return ToolUtil.enchant(super.getRandom(random, level), level)
        }
    }

    object Weaponry : WizardProfession {

        override val firstLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.WOODEN_SWORD),
            ItemStack(Items.STONE_SWORD),
            ItemStack(Items.WOODEN_AXE),
            ItemStack(Items.WOODEN_SWORD),
            ItemStack(Items.STONE_SWORD),
            ItemStack(Items.WOODEN_AXE)
        )

        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.STONE_AXE),
            ItemStack(Items.GOLDEN_SWORD),
            ItemStack(Items.GOLDEN_AXE),
            ItemStack(Items.STONE_AXE),
            ItemStack(Items.GOLDEN_SWORD),
            ItemStack(Items.GOLDEN_AXE)
        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.IRON_SWORD),
            ItemStack(Items.IRON_AXE),
            ItemStack(Items.DIAMOND_SWORD),
            ItemStack(Items.IRON_SWORD),
            ItemStack(Items.IRON_AXE),
            ItemStack(Items.DIAMOND_SWORD),
            ItemStack(Items.BOW)
        )
        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.DIAMOND_AXE),
            ItemStack(Items.DIAMOND_AXE),
            ItemStack(Items.DIAMOND_SWORD),
            ItemStack(Items.NETHERITE_SWORD),
            ItemStack(Items.NETHERITE_AXE),
            ItemStack(Items.BOW),
            ItemStack(Items.CROSSBOW)
        )

        override fun getRandom(random: Random, level: Int): ItemStack {
            return ToolUtil.enchant(super.getRandom(random, level), level)
        }
    }

    object Toolsmith : WizardProfession {

        override val firstLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.WOODEN_SHOVEL),
            ItemStack(Items.STONE_SHOVEL),
            ItemStack(Items.WOODEN_PICKAXE),
            ItemStack(Items.WOODEN_SHOVEL),
            ItemStack(Items.STONE_SHOVEL),
            ItemStack(Items.WOODEN_PICKAXE),
            ItemStack(Items.WOODEN_HOE),
            ItemStack(Items.STONE_HOE)
        )

        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.STONE_PICKAXE),
            ItemStack(Items.GOLDEN_SHOVEL),
            ItemStack(Items.GOLDEN_PICKAXE),
            ItemStack(Items.STONE_PICKAXE),
            ItemStack(Items.GOLDEN_SHOVEL),
            ItemStack(Items.GOLDEN_PICKAXE),
            ItemStack(Items.STONE_HOE),
            ItemStack(Items.GOLDEN_HOE)
        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.IRON_SHOVEL),
            ItemStack(Items.IRON_PICKAXE),
            ItemStack(Items.DIAMOND_SHOVEL),
            ItemStack(Items.IRON_SHOVEL),
            ItemStack(Items.IRON_PICKAXE),
            ItemStack(Items.DIAMOND_SHOVEL),
            ItemStack(Items.GOLDEN_HOE),
            ItemStack(Items.IRON_HOE)
        )
        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.DIAMOND_PICKAXE),
            ItemStack(Items.DIAMOND_PICKAXE),
            ItemStack(Items.DIAMOND_SHOVEL),
            ItemStack(Items.NETHERITE_SHOVEL),
            ItemStack(Items.NETHERITE_PICKAXE),
            ItemStack(Items.DIAMOND_HOE),
            ItemStack(Items.NETHERITE_HOE)
        )

        override fun getRandom(random: Random, level: Int): ItemStack {
            return ToolUtil.enchant(super.getRandom(random, level), level)
        }
    }

    object Electrician : WizardProfession {

        override val firstLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.TORCH),
            ItemStack(Items.SOUL_TORCH),
            ItemStack(Items.LANTERN),
            ItemStack(Items.JACK_O_LANTERN),
            ItemStack(Items.REDSTONE_TORCH)
        )
        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.REDSTONE),
            ItemStack(Items.REDSTONE_BLOCK),
            ItemStack(Items.REDSTONE_LAMP),
            ItemStack(Items.COMPARATOR),
            ItemStack(Items.REPEATER)

        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.SEA_LANTERN),
            ItemStack(Items.SEA_PICKLE),
            ItemStack(Items.GLOWSTONE),
            ItemStack { Items.OBSERVER },
            ItemStack { Items.DISPENSER },
            ItemStack { Items.DROPPER }
        )
        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.TORCH),
            ItemStack(Items.SOUL_TORCH),
            ItemStack(Items.LANTERN),
            ItemStack(Items.JACK_O_LANTERN),
            ItemStack(Items.REDSTONE_TORCH),
            ItemStack(Items.DAYLIGHT_DETECTOR),
            ItemStack(Items.GLOWSTONE)
        )

        override fun getRandom(random: Random, level: Int): ItemStack {
            return super.getRandom(random, level).apply {
                count = (maxCount - random.nextInt((0.2 * maxCount).toInt().coerceAtLeast(1))).coerceAtLeast(1)
            }
        }
    }

    object Musician : WizardProfession {
        override val firstLevelGroup: List<ItemStack>
            get() = buffet
        override val secondLevelGroup: List<ItemStack>
            get() = buffet
        override val thirdLevelGroup: List<ItemStack>
            get() = buffet
        override val fourthLevelGroup: List<ItemStack>
            get() = buffet

        private val buffet = listOf(
            ItemStack(Items.MUSIC_DISC_BLOCKS),
            ItemStack(Items.MUSIC_DISC_13),
            ItemStack(Items.MUSIC_DISC_CAT),
            ItemStack(Items.MUSIC_DISC_CHIRP),
            ItemStack(Items.MUSIC_DISC_FAR),
            ItemStack(Items.MUSIC_DISC_MALL),
            ItemStack(Items.MUSIC_DISC_MELLOHI),
            ItemStack(Items.MUSIC_DISC_STAL),
            ItemStack(Items.MUSIC_DISC_STRAD),
            ItemStack(Items.MUSIC_DISC_WARD),
            ItemStack(Items.MUSIC_DISC_11),
            ItemStack(Items.MUSIC_DISC_WAIT),
            ItemStack(Items.MUSIC_DISC_PIGSTEP)
        )

        override fun getRandom(random: Random, level: Int): ItemStack {
            return buffet[random.nextInt(buffet.size)].copy()
        }
    }

    object Miner : WizardProfession {

        private val expensiveToCheap = mapOf(
            Items.NETHERITE_BLOCK to 1,
            Items.NETHERITE_INGOT to 3,
            Items.NETHERITE_SCRAP to 6,
            Items.DIAMOND_BLOCK to 2,
            Items.DIAMOND_ORE to 8,
            Items.DIAMOND to 21,
            Items.EMERALD_BLOCK to 2,
            Items.EMERALD_ORE to 18,
            Items.EMERALD to 21,
            Items.GOLD_BLOCK to 3,
            Items.GOLD_ORE to 25,
            Items.GOLD_INGOT to 31,
            Items.LAPIS_BLOCK to 4,
            Items.LAPIS_ORE to 32,
            Items.LAPIS_LAZULI to 40,
            Items.IRON_BLOCK to 4,
            Items.IRON_ORE to 32,
            Items.IRON_INGOT to 40,
            Items.COAL_BLOCK to 8,
            Items.COAL_ORE to 52,
            Items.COAL to 64
        )

        override val firstLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.COAL),
            ItemStack(Items.COAL_BLOCK),
            ItemStack(Items.COAL_ORE),
            ItemStack(Items.COAL),
            ItemStack(Items.COAL_BLOCK),
            ItemStack(Items.COAL_ORE)
        )
        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.IRON_ORE),
            ItemStack(Items.IRON_INGOT),
            ItemStack(Items.GOLD_ORE),
            ItemStack(Items.GOLD_INGOT),
            ItemStack(Items.IRON_ORE),
            ItemStack(Items.IRON_INGOT),
            ItemStack(Items.GOLD_ORE),
            ItemStack(Items.GOLD_INGOT)
        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.GOLD_BLOCK),
            ItemStack(Items.IRON_BLOCK),
            ItemStack(Items.DIAMOND_ORE),
            ItemStack(Items.GOLD_BLOCK),
            ItemStack(Items.IRON_BLOCK),
            ItemStack(Items.DIAMOND_ORE),
            ItemStack(Items.LAPIS_LAZULI),
            ItemStack(Items.LAPIS_LAZULI)
        )

        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.DIAMOND_BLOCK),
            ItemStack(Items.DIAMOND_BLOCK),
            ItemStack(Items.EMERALD),
            ItemStack(Items.EMERALD),
            ItemStack(Items.EMERALD_BLOCK),
            ItemStack(Items.EMERALD_BLOCK),
            ItemStack(Items.NETHERITE_SCRAP),
            ItemStack(Items.NETHERITE_INGOT),
            ItemStack(Items.LAPIS_BLOCK),
            ItemStack(Items.LAPIS_BLOCK)
        )

        override fun getRandom(random: Random, level: Int): ItemStack {
            return super.getRandom(random, level).apply {
                count = (expensiveToCheap[item] ?: 1 + random.nextInt(3)).coerceAtMost(maxCount)
            }
        }
    }

    object BiomeEnthusiast : WizardProfession {
        override val firstLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.GRASS_PATH),
            ItemStack(Items.FARMLAND),
            ItemStack(Items.PODZOL),
            ItemStack(Items.GRASS_BLOCK),
            ItemStack(Items.COARSE_DIRT),
            ItemStack(Items.SNOW_BLOCK),
            ItemStack(Items.BLUE_ICE),
            ItemStack(Items.PACKED_ICE)
        )
        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.BRAIN_CORAL),
            ItemStack(Items.BUBBLE_CORAL),
            ItemStack(Items.FIRE_CORAL),
            ItemStack(Items.HORN_CORAL),
            ItemStack(Items.TUBE_CORAL),
            ItemStack(Items.BRAIN_CORAL_FAN),
            ItemStack(Items.BUBBLE_CORAL_FAN),
            ItemStack(Items.FIRE_CORAL_FAN),
            ItemStack(Items.HORN_CORAL_FAN),
            ItemStack(Items.TUBE_CORAL_FAN)
        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.BEEHIVE),
            ItemStack(Items.BEE_NEST),
            ItemStack(Items.HONEYCOMB_BLOCK),
            ItemStack(Items.HONEY_BLOCK)
        )
        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.GILDED_BLACKSTONE),
            ItemStack(Items.NETHER_GOLD_ORE),
            ItemStack(Items.BLACKSTONE),
            ItemStack(Items.BASALT)
        )

        override fun getRandom(random: Random, level: Int): ItemStack {
            return super.getRandom(random, level).apply { count = 21 + random.nextInt(8) }
        }
    }

    object MobDropHoarder : WizardProfession {
        override val firstLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.LEATHER),
            ItemStack(Items.STRING),
            ItemStack(Items.FEATHER),
            ItemStack(Items.WHITE_WOOL),
            ItemStack(Items.BAMBOO)
        )
        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.PUFFERFISH),
            ItemStack(Items.RABBIT_FOOT),
            ItemStack(Items.RABBIT_HIDE),
            ItemStack(Items.TROPICAL_FISH),
            ItemStack(Items.ARROW),
            ItemStack(Items.INK_SAC)
        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.BONE),
            ItemStack(Items.ROTTEN_FLESH),
            ItemStack(Items.SNOWBALL),
            ItemStack(Items.PRISMARINE_SHARD),
            ItemStack(Items.PRISMARINE_CRYSTALS),
            ItemStack(Items.ENDER_PEARL),
            ItemStack(Items.GHAST_TEAR),
            ItemStack(Items.GUNPOWDER)
        )
        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.BLAZE_ROD),
            ItemStack(Items.MAGMA_CREAM),
            ItemStack(Items.PHANTOM_MEMBRANE),
            ItemStack(Items.SHULKER_SHELL),
            ItemStack(Items.SLIME_BALL),
            ItemStack(Items.EMERALD),
            ItemStack(Items.GLOWSTONE_DUST),
            ItemStack(Items.GOLD_NUGGET)
        )

        override fun getRandom(random: Random, level: Int): ItemStack {
            return super.getRandom(random, level).apply { count = 13 + random.nextInt(15) }
        }
    }

    object Foodist : WizardProfession {
        override val firstLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.COD),
            ItemStack(Items.SALMON),
            ItemStack(Items.CHICKEN),
            ItemStack(Items.MUTTON),
            ItemStack(Items.PORKCHOP),
            ItemStack(Items.RABBIT)
        )
        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.WHEAT),
            ItemStack(Items.BREAD),
            ItemStack(Items.POTATO),
            ItemStack(Items.CARROT),
            ItemStack(Items.BEEF)
        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.PUMPKIN),
            ItemStack(Items.PUMPKIN_PIE),
            ItemStack(Items.MELON),
            ItemStack(Items.MELON_SLICE),
            ItemStack(Items.BEETROOT_SOUP),
            ItemStack(Items.MUSHROOM_STEW),
            ItemStack(Items.GOLDEN_CARROT)
        )
        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.COOKED_BEEF),
            ItemStack(Items.COOKED_CHICKEN),
            ItemStack(Items.COOKED_MUTTON),
            ItemStack(Items.COOKED_PORKCHOP),
            ItemStack(Items.COOKED_RABBIT),
            ItemStack(Items.COOKED_COD),
            ItemStack(Items.COOKED_SALMON),
            ItemStack(Items.GOLDEN_APPLE)
        )

        override fun getRandom(random: Random, level: Int): ItemStack {
            return super.getRandom(random, level).apply { count = (25 + random.nextInt(8)).coerceAtMost(maxCount) }
        }
    }

    object BlackMarketer : WizardProfession {
        override val firstLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.CREEPER_HEAD),
            ItemStack(Items.SKELETON_SKULL),
            ItemStack(Items.ZOMBIE_HEAD)
        )
        override val secondLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.TOTEM_OF_UNDYING),
            ItemStack(Items.SHULKER_SHELL)
        )
        override val thirdLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.ENCHANTED_GOLDEN_APPLE),
            ItemStack(Items.SPAWNER)
        )
        override val fourthLevelGroup: List<ItemStack> = listOf(
            ItemStack(Items.WITHER_SKELETON_SKULL),
            ItemStack(Items.ELYTRA),
            ItemStack(Items.DRAGON_HEAD)
        )
    }
}