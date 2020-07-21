package fp.yeyu.monsterfriend.screens

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class VindorClientScreen(description: VindorScreenDescription, player: PlayerInventory, title: Text) :
    CottonInventoryScreen<VindorScreenDescription>(description, player.player, title)
