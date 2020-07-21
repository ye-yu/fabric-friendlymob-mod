package fp.yeyu.monsterfriend.screens

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class EvioneClientScreen(screenDescription: EvioneScreenDescription, playerInventory: PlayerInventory, title: Text) :
    CottonInventoryScreen<EvioneScreenDescription>(screenDescription, playerInventory.player, title) {
}