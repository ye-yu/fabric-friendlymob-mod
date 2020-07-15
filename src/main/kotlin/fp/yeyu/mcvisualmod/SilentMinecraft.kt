package fp.yeyu.mcvisualmod

import fp.yeyu.mcvisualmod.mobs.egg.VindorEgg
import fp.yeyu.mcvisualmod.mobs.entity.Vindor
import fp.yeyu.mcvisualmod.packets.PacketHandlers
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.SpawnGroup
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class SilentMinecraft : ModInitializer {
    companion object {
        val NAMESPACE = "friendlymob"
        val VINDOR_ENTITY_ID = Identifier(NAMESPACE, Vindor.NAME)
        val LOGGER: Logger = LogManager.getLogger()

        fun registerItem(id: String, instantiation: Item) {
            Registry.register(
                Registry.ITEM, Identifier(NAMESPACE, id), instantiation
            )

        }
    }

    enum class Mobs(val entry: EntityType<out LivingEntity>) {
        VINDOR(
            Registry.register(
                Registry.ENTITY_TYPE,
                VINDOR_ENTITY_ID,
                FabricEntityTypeBuilder.create<Vindor>(
                    SpawnGroup.CREATURE,
                    ::Vindor
                ).dimensions(EntityDimensions(0.6f, 1.95f, true)).build()
            )
        );
    }

    override fun onInitialize() {
        LOGGER.info("Mod is loaded. [Main]")
        registerItem(
            VindorEgg.NAME,
            VindorEgg(
                Mobs.VINDOR.entry,
                3407872,
                12369084,
                Item.Settings().maxDamage(1).group(ItemGroup.MISC)
            )
        )

        PacketHandlers.values().forEach {
            if (it.toServer) {
                ServerSidePacketRegistry.INSTANCE.register(it.id, it.handler)
                LOGGER.info("[Server] Registered $it")
            }
        }
    }
}