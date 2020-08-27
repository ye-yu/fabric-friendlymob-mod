package fp.yeyu.monsterfriend.mobs.entity

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.NamedScreenHandlerFactory

interface GuiProvider {
    var currentUser: PlayerEntity?
    val guiFactory: NamedScreenHandlerFactory
}
