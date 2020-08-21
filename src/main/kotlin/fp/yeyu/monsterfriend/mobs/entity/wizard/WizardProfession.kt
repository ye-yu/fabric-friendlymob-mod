package fp.yeyu.monsterfriend.mobs.entity.wizard

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.util.*

interface WizardProfession {
    val firstLevelGroup: List<ItemStack>
    val secondLevelGroup: List<ItemStack>
    val thirdLevelGroup: List<ItemStack>
    val fourthLevelGroup: List<ItemStack>

    val groups: List<List<ItemStack>>
        get() = listOf(firstLevelGroup, secondLevelGroup, thirdLevelGroup, fourthLevelGroup)

    fun getRandom(random: Random, level: Int): ItemStack {
        if (random.nextFloat() < 0.15) { // lucky roll
            val list = groups[random.nextInt(groups.size)]
            return list[random.nextInt(list.size)].copy()
        }
        val lowerGroup = groups[(level - 1).coerceAtLeast(0)]
        val upperGroup = groups[(level).coerceAtMost(groups.size - 1)]
        val index = random.nextInt(lowerGroup.size + upperGroup.size)
        val item = if (index < lowerGroup.size) lowerGroup[index]
        else upperGroup[index - lowerGroup.size]
        return item.copy()
    }
}