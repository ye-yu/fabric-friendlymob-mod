package fp.yeyu.monsterfriend.screens

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

class VindorClientScreen(description: VindorGUI, player: PlayerEntity, title: Text) :
    CottonInventoryScreen<VindorGUI>(description, player, title)
