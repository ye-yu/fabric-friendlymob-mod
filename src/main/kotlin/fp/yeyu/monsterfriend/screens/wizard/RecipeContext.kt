package fp.yeyu.monsterfriend.screens.wizard

import fp.yeyu.monsterfriend.mobs.entity.Wizard.CustomRecipe
import fp.yeyu.monsterfriend.mobs.entity.Wizard.LearntRecipe
import io.github.yeyu.gui.handler.ServerScreenHandler
import io.github.yeyu.packet.ScreenPacket
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

class RecipeContext(recipes: LearntRecipe?) {
    val learntRecipe: LearntRecipe = recipes ?: LearntRecipe()
    var level = 0

    init {
        while(level < 5 && !learntRecipe.recipes[level].toCraft.isEmpty) level++
    }

    fun sync(serverScreenHandler: ServerScreenHandler, packetName: String, player: ServerPlayerEntity) {
        ScreenPacket.sendPacket(serverScreenHandler.syncId, packetName, false, player) {
            it.writeInt(level)
            for (recipe in learntRecipe.recipes) {
                it.writeItemStack(recipe.toCraft)
                it.writeItemStack(recipe.item1)
                it.writeItemStack(recipe.item2)
                it.writeItemStack(recipe.flower)
                it.writeItemStack(recipe.potion)
                it.writeInt(recipe.expReward)
            }
        }
    }

    fun sync(buf: PacketByteBuf) {
        level = buf.readInt()
        for (index in learntRecipe.recipes.indices) {
            learntRecipe.recipes[index] = CustomRecipe(
                buf.readItemStack(),
                buf.readItemStack(),
                buf.readItemStack(),
                buf.readItemStack(),
                buf.readItemStack(),
                buf.readInt()
            )
            if (learntRecipe.recipes[index].toCraft.isEmpty) continue
        }
    }
}