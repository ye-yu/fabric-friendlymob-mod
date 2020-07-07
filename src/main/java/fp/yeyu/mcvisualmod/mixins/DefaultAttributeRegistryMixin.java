package fp.yeyu.mcvisualmod.mixins;

import fp.yeyu.mcvisualmod.mobs.entity.AttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DefaultAttributeRegistry.class)
public class DefaultAttributeRegistryMixin {
    @Inject(at = @At("RETURN"), method = "get", cancellable = true)
    private static void getMixin(EntityType<? extends LivingEntity> type, CallbackInfoReturnable<DefaultAttributeContainer> cir) {
        if (cir.getReturnValue() == null) {
            System.out.println(String.format("Requested attribute for %s", type.getClass().getSimpleName()));
            cir.setReturnValue(AttributeRegistry.Companion.getATTRS().get(type));
        }
    }
}
