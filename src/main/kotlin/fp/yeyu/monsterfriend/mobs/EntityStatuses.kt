package fp.yeyu.monsterfriend.mobs

enum class EntityStatuses(val statusNumber: Byte?) {
    HURT_GENERIC(2),
    HURT_THORN(33),
    HURT_DROWN(36),
    HURT_FIRE(37),
    HURT_BERRY(44),

    DEATH_GENERIC(3),

    SHIELD_BLOCK(29),

    SHIELD_BREAK(30),

    PORTAL(46),
    MAINHAND_BREAK(47),

    OFFHAND_BREAK(48),

    HELMET_BREAK(49),
    CHESTPLATE_BREAK(50),
    LEGGINGS_BREAK(51),
    BOOTS_BREAK(52),
    HONEY_REGULAR(53),

    HONEY_RICH(54),
    SWAP_HAND(55),

    UNRESERVED(null),

    /** @see net.minecraft.entity.passive.AnimalEntity */
    ANIMAL_HEART(18),

    /** @see net.minecraft.entity.passive.FoxEntity */
    FOX_MAINHAND(45),

    /** @see net.minecraft.entity.mob.HoglinEntity */
    HOGLIN_ATTACK(4),

    /** @see net.minecraft.entity.passive.HorseBaseEntity */
    HORSE_DISLIKE(6),

    /** @see net.minecraft.entity.passive.HorseBaseEntity */
    HORSE_LIKE(7),

    /** @see net.minecraft.entity.passive.OcelotEntity */
    OCELOT_DISLIKE(40),

    /** @see net.minecraft.entity.passive.OcelotEntity */
    OCELOT_LIKE(41),

    /** @see net.minecraft.entity.passive.RabbitEntity */
    RABBIT_JUMP(1),

    /** @see net.minecraft.entity.passive.SheepEntity */
    SHEEP_EATING_TIMER(10),

    /** @see net.minecraft.entity.passive.TameableEntity */
    ANIMAL_DISLIKE(6),

    /** @see net.minecraft.entity.passive.TameableEntity */
    ANIMAL_LIKE(7),

    /** @see net.minecraft.entity.passive.WolfEntity */
    WOLF_SHAKE_WATER(8),

    /** @see net.minecraft.entity.passive.DolphinEntity */
    DOLPHIN_LIKE(38),

    /** @see net.minecraft.entity.passive.IronGolemEntity */
    IRON_GOLEM_HURT(4),

    /** @see net.minecraft.entity.passive.IronGolemEntity */
    IRON_GOLEM_LOOK_VILLAGER(11),

    /** @see net.minecraft.entity.passive.IronGolemEntity */
    IRON_GOLEM_UNLOOK_VILLAGER(34),

    /** @see net.minecraft.entity.mob.RavagerEntity */
    RAVAGER_HURT(4),

    /** @see net.minecraft.entity.mob.RavagerEntity */
    RAVAGER_STUN(39),

    /** @see net.minecraft.entity.passive.SquidEntity */
    SQUID_MISC(19),

    /** @see net.minecraft.entity.passive.VillagerEntity */
    VILLAGER_HEART(12),

    /** @see net.minecraft.entity.passive.VillagerEntity */
    VILLAGER_ANGRY(13),

    /** @see net.minecraft.entity.passive.VillagerEntity */
    VILLAGER_HAPPY(14),

    /** @see net.minecraft.entity.passive.VillagerEntity */
    VILLAGER_SPLASH(42),

    /** @see net.minecraft.entity.mob.WitchEntity */
    WITCH_MISC(15),

    /** @see net.minecraft.entity.mob.ZoglinEntity */
    ZOGLIN_ATTACK(41),

    /** @see net.minecraft.entity.mob.ZombieVillagerEntity */
    ZOMBIE_MISC(16),

    /** @see net.minecraft.client.network.ClientPlayerEntity */
    CLIENT_PERMISSION_1(24),

    /** @see net.minecraft.client.network.ClientPlayerEntity */
    CLIENT_PERMISSION_2(25),

    /** @see net.minecraft.client.network.ClientPlayerEntity */
    CLIENT_PERMISSION_3(26),

    /** @see net.minecraft.client.network.ClientPlayerEntity */
    CLIENT_PERMISSION_4(27),

    /** @see net.minecraft.client.network.ClientPlayerEntity */
    CLIENT_PERMISSION_5(28);

    companion object {
        operator fun get(statusNumber: Byte?): List<EntityStatuses> {
            val filtered = values().filter { it.statusNumber == statusNumber }
            if (filtered.isEmpty()) return listOf(UNRESERVED)
            return filtered
        }
    }
}