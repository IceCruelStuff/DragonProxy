/*
 * DragonProxy
 * Copyright (C) 2016-2019 Dragonet Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You can view the LICENSE file for more details.
 *
 * https://github.com/DragonetMC/DragonProxy
 */
package org.dragonet.proxy.network.translator.bedrock;

import com.nukkitx.protocol.bedrock.packet.AnimatePacket;
import org.dragonet.proxy.network.session.ProxySession;
import org.dragonet.proxy.network.translator.PacketTranslator;
import org.dragonet.proxy.network.translator.annotations.PEPacketTranslator;

@PEPacketTranslator(packetClass = AnimatePacket.class)
public class PEAnimateTranslator extends PacketTranslator<AnimatePacket> {
    public static final PEAnimateTranslator INSTANCE = new PEAnimateTranslator();

    @Override
    public void translate(ProxySession session, AnimatePacket packet) {
        switch(packet.getAction()) {
            case SWING_ARM:
                //ClientPlayerSwingArmPacket swingArmPacket = new ClientPlayerSwingArmPacket(Hand.MAIN_HAND);
                //session.sendRemotePacket(swingArmPacket);
        }
    }
}
