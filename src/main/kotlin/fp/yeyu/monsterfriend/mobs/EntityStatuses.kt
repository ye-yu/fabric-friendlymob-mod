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

    /**
     * @see net.minecraft.entity.passive.AnimalEntity
     * */
    ANIMAL_HEART(18),

    /**
     * @see net.minecraft.entity.passive.FoxEntity
     * */
    FOX_MAINHAND(45),

    /**
     * @see net.minecraft.entity.mob.HoglinEntity
     * */
    HOGLIN_ATTACK(4),

    /**
     * @see net.minecraft.entity.passive.HorseBaseEntity
     * */
    HORSE_DISLIKE(6),
    HORSE_LIKE(7),

    OCELOT_DISLIKE(40),
    OCELOT_LIKE(41),

}