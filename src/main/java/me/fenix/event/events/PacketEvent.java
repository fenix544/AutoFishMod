package me.fenix.event.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;

public class PacketEvent extends EventCancellable {
    private final Packet<?> packet;
    private final EnumPacketDirection direction;

    public PacketEvent(Packet<?> packet, EnumPacketDirection direction) {
        this.packet = packet;
        this.direction = direction;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public EnumPacketDirection getDirection() {
        return direction;
    }
}
