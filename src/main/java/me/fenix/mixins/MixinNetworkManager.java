package me.fenix.mixins;

import com.darkmagician6.eventapi.EventManager;
import io.netty.channel.ChannelHandlerContext;
import me.fenix.event.events.PacketEvent;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Inject(method = "channelRead0", at = @At("HEAD"))
    protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, Packet p_channelRead0_2_, CallbackInfo callbackInfo) throws Exception {
        PacketEvent packetEvent = new PacketEvent(p_channelRead0_2_, EnumPacketDirection.CLIENTBOUND);
        EventManager.call(packetEvent);
    }

    @Inject(method = "sendPacket", at = @At("HEAD"))
    public void sendPacket(Packet packetIn, CallbackInfo callbackInfo) {
        PacketEvent packetEvent = new PacketEvent(packetIn, EnumPacketDirection.CLIENTBOUND);
        EventManager.call(packetEvent);
    }
}
