package fp.yeyu.monsterfriend.mobs.entity

import net.minecraft.entity.player.PlayerEntity

interface GuiProvider {
    var currentUser: PlayerEntity?
}
