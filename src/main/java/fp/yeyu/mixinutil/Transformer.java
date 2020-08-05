package fp.yeyu.mixinutil;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface Transformer {
    MobEntity getEntity();

    @Nullable
    default MobEntity transformTo(EntityType<? extends MobEntity> factory) {
        final MobEntity oldEntity = getEntity();
        if (oldEntity.removed) return null;
        MobEntity newEntity = factory.create(oldEntity.world);
        if (newEntity == null) return null;

        newEntity.copyPositionAndRotation(oldEntity);
        newEntity.setCanPickUpLoot(oldEntity.canPickUpLoot());
        newEntity.setAiDisabled(oldEntity.isAiDisabled());
        EquipmentSlot[] equipmentSlots = EquipmentSlot.values();

        for (EquipmentSlot equipmentSlot : equipmentSlots) {
            ItemStack itemStack = oldEntity.getEquippedStack(equipmentSlot);
            if (!itemStack.isEmpty()) {
                newEntity.equipStack(equipmentSlot, itemStack.copy());
                newEntity.setEquipmentDropChance(equipmentSlot, 0.082F);
                itemStack.setCount(0);
            }
        }

        if (oldEntity.hasCustomName()) {
            newEntity.setCustomName(oldEntity.getCustomName());
            newEntity.setCustomNameVisible(oldEntity.isCustomNameVisible());
        }

        if (oldEntity.isPersistent()) {
            newEntity.setPersistent();
        }

        newEntity.setInvulnerable(oldEntity.isInvulnerable());
        oldEntity.world.spawnEntity(newEntity);
        oldEntity.remove();
        return newEntity;
    }
}
