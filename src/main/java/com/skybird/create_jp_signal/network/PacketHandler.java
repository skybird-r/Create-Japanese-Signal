package com.skybird.create_jp_signal.network;

import com.skybird.create_jp_signal.JpSignals;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(JpSignals.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );
    

    public static void init() {
        int id = 0;
        
        CHANNEL.registerMessage(id++, FinalizeLinkPacket.class,
            FinalizeLinkPacket::encode,
            FinalizeLinkPacket::decode,
            FinalizeLinkPacket::handle
        );
        CHANNEL.registerMessage(id++, ConfigureMastPacket.class,
            ConfigureMastPacket::encode,
            ConfigureMastPacket::decode,
            ConfigureMastPacket::handle
        );
        CHANNEL.registerMessage(id++, UpdateControlBoxDataPacket.class,
            UpdateControlBoxDataPacket::encode,
            UpdateControlBoxDataPacket::decode,
            UpdateControlBoxDataPacket::handle
        );
        CHANNEL.registerMessage(id++, UnlinkIndexPacket.class,
            UnlinkIndexPacket::encode,
            UnlinkIndexPacket::decode,
            UnlinkIndexPacket::handle
        );
        CHANNEL.registerMessage(id++, SignalLinkPacket.class,
            SignalLinkPacket::encode,
            SignalLinkPacket::decode,
            SignalLinkPacket::handle
        );
    }

    public static void sendToServer(Object msg) {
        CHANNEL.sendToServer(msg);
    }
}