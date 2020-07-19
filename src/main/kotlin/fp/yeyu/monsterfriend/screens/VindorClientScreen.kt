package fp.yeyu.monsterfriend.screens

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class VindorClientScreen(description: VindorGUI, player: PlayerInventory, title: Text) :
    CottonInventoryScreen<VindorGUI>(description, player.player, title)
