package fp.yeyu.monsterfriend.mixins;

import fp.yeyu.monsterfriend.BefriendMinecraft;
import fp.yeyu.monsterfriend.mobs.entity.Vindor;
import fp.yeyu.monsterfriend.statics.immutable.ConfigFile;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VindicatorEntity.class)
public abstract class VindicatorEntityMixin extends IllagerEntity {
    protected VindicatorEntityMixin(EntityType<? extends IllagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.world instanceof ServerWorld)) return;
        final ServerWorld serverWorld = (ServerWorld) this.world;
        final HitResult blockHit = this.rayTrace(20, 0, false);
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            final Vec3d pos = blockHit.getPos();
            final BlockState block = serverWorld.getBlockState(new BlockPos(pos));
            if (block.getBlock() == Blocks.EMERALD_BLOCK) {
                if (this.random.nextFloat() < ConfigFile.INSTANCE.getFloat(ConfigFile.Defaults.VINDOR_TRANSFORM_CHANCE)) {
                    convertToVindor();
                }
            }
        }
    }

    private void convertToVindor() {
        if (this.removed) return;
        Vindor vindor = (Vindor) BefriendMinecraft.Mobs.VINDOR.getEntry().create(this.world);
        if (vindor == null) {
            LOGGER.warn("Cannot instantiate a Vindor");
            LOGGER.trace(new Throwable());
            return;
        }

        vindor.copyPositionAndRotation(this);
        vindor.setCanPickUpLoot(this.canPickUpLoot());
        vindor.setAiDisabled(this.isAiDisabled());
        EquipmentSlot[] var3 = EquipmentSlot.values();

        for (EquipmentSlot equipmentSlot : var3) {
            ItemStack itemStack = this.getEquippedStack(equipmentSlot);
            if (!itemStack.isEmpty()) {
                vindor.equipStack(equipmentSlot, itemStack.copy());
                vindor.setEquipmentDropChance(equipmentSlot, this.getDropChance(equipmentSlot));
                itemStack.setCount(0);
            }
        }

        if (this.hasCustomName()) {
            vindor.setCustomName(this.getCustomName());
            vindor.setCustomNameVisible(this.isCustomNameVisible());
        }

        if (this.isPersistent()) {
            vindor.setPersistent();
        }

        vindor.setInvulnerable(this.isInvulnerable());
        this.world.spawnEntity(vindor);
        this.remove();
        LOGGER.info("Spawned Vindor");
    }
}
