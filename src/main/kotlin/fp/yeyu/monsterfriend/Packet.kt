package fp.yeyu.monsterfriend

import io.github.yeyu.util.DrawerUtil
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.fabric.api.server.PlayerStream
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl
import net.minecraft.client.MinecraftClient
import net.minecraft.client.particle.ParticleManager
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

object Packet {
    private val PARTICLE_ID = Identifier(BefriendMinecraft.NAMESPACE, "particle")
    private val random = Random()

    fun registerClient() {
        ClientSidePacketRegistryImpl.INSTANCE.register(PARTICLE_ID, ::onParticlePacket)
    }

    fun spawnParticle(world: World, pos: BlockPos, color: Int = DrawerUtil.constructColor(0xAA, 0x50, 0x70, 0xFF)) {
        check(!world.isClient) { "Cannot send particle from client!" }
        val buf = PacketByteBuf(Unpooled.buffer())
        buf.writeBlockPos(pos)
        buf.writeInt(8)
        buf.writeInt(color)
        PlayerStream.watching(world, pos).forEach {
            ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(it, PARTICLE_ID, buf)
        }
    }

    private fun onParticlePacket(context: PacketContext, buf: PacketByteBuf) {
        val position = buf.readBlockPos()
        val count = buf.readInt()
        val color = decomposeColor(buf.readInt())

        for (i in 0 until count) {
            context.taskQueue.execute {
                MinecraftClient.getInstance().particleManager.addParticle(
                    ParticleTypes.POOF,
                    position.x,
                    position.y,
                    position.z,
                    color[0],
                    color[1],
                    color[2],
                    random
                )
            }
        }
    }

    private fun decomposeColor(color: Int) : Array<Int> {
        val a = (color shr 24) and 255
        val r = (color shr 16) and 255
        val g = (color shr  8) and 255
        val b = color and 255
        return arrayOf(r, g, b, a)
    }
}

private fun ParticleManager.addParticle(
    particle: DefaultParticleType,
    x: Int,
    y: Int,
    z: Int,
    r: Int,
    g: Int,
    b: Int,
    random: Random
) {
    this.addParticle(
        particle,
        x.toDouble() + random.nextDouble(),
        y.toDouble() + random.nextDouble(),
        z.toDouble() + random.nextDouble(),
        r.toDouble(),
        g.toDouble(),
        b.toDouble()
    )
}
