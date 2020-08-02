package fp.yeyu.monsterfriend.item

import fp.yeyu.monsterfriend.BefriendMinecraft
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity
import net.minecraft.util.registry.Registry

object ItemRegistry {
    val vexEssence: Item = Item(Item.Settings().group(ItemGroup.MISC).rarity(Rarity.RARE).maxCount(64))

    fun registerItem() {
        Registry.register(Registry.ITEM, Identifier(BefriendMinecraft.NAMESPACE, "vex_essence"), vexEssence)
    }
}