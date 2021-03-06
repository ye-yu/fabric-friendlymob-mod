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
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

object Particle {
    private val PARTICLE_ID = Identifier(BefriendMinecraft.NAMESPACE, "particle")
    private val ZEROS = arrayOf(0, 0, 0)
    private val random = Random()

    fun registerClient() {
        ClientSidePacketRegistryImpl.INSTANCE.register(PARTICLE_ID, ::onParticlePacket)
    }

    fun spawnLightParticle(
        world: World,
        pos: BlockPos,
        color: Int = DrawerUtil.constructColor(0xAA, 0x50, 0x70, 0xFF),
        particle: Particles
    ) {
        Thread {
            check(!world.isClient) { "Cannot send particle from client!" }
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeBlockPos(pos)
            buf.writeInt(8)
            buf.writeEnumConstant(particle)
            buf.writeInt(color)
            PlayerStream.watching(world, pos).forEach {
                ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(it, PARTICLE_ID, buf)
            }
        }.start()
    }

    fun spawnHeavyParticle(
        world: World,
        pos: BlockPos,
        color: Int = DrawerUtil.constructColor(0xAA, 0x50, 0x70, 0xFF),
        particle: Particles
    ) {
        Thread {
            check(!world.isClient) { "Cannot send particle from client!" }
            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeBlockPos(pos)
            buf.writeInt(30)
            buf.writeEnumConstant(particle)
            buf.writeInt(color)
            PlayerStream.watching(world, pos).forEach {
                ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(it, PARTICLE_ID, buf)
            }
        }.start()
    }

    private fun onParticlePacket(context: PacketContext, buf: PacketByteBuf) {
        val position = buf.readBlockPos()
        val count = buf.readInt()
        val particle = buf.readEnumConstant(Particles::class.java)
        val color = if (particle == Particles.POOF) ZEROS else decomposeColor(buf.readInt())

        context.taskQueue.execute {
            for (i in 0 until count) {
                MinecraftClient.getInstance().particleManager.addParticle(
                    particle.instance,
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

    private fun decomposeColor(color: Int): Array<Int> {
        val a = (color shr 24) and 255
        val r = (color shr 16) and 255
        val g = (color shr 8) and 255
        val b = color and 255
        return arrayOf(r, g, b, a)
    }

    enum class Particles(val instance: DefaultParticleType) {
        POOF(ParticleTypes.POOF), ENTITY(ParticleTypes.ENTITY_EFFECT);
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
}
