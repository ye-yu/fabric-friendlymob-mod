package fp.yeyu.monsterfriend.utils.wondertrade

import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag

class WonderProps(val item: ItemStack, val msg: String) {
    fun toTag(): CompoundTag {
        val itemTag = CompoundTag()
        val tag = CompoundTag()
        item.toTag(itemTag)
        tag.put(WonderTrade.wonderItemTag, itemTag)
        tag.putString(WonderTrade.wonderMsgTag, msg)
        return tag
    }

    companion object {
        fun fromTag(tag: CompoundTag): WonderProps {
            val item = ItemStack.fromTag(tag.getCompound(WonderTrade.wonderItemTag))
            val msg = tag.getString(WonderTrade.wonderMsgTag)
            return WonderProps(item, msg)
        }
    }
}
