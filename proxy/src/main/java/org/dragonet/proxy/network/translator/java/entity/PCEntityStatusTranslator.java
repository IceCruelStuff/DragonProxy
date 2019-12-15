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
package org.dragonet.proxy.network.translator.java.entity;

import com.github.steveice10.mc.protocol.data.game.entity.EntityStatus;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityStatusPacket;
import com.nukkitx.protocol.bedrock.data.EntityDataDictionary;
import com.nukkitx.protocol.bedrock.packet.EntityEventPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityDataPacket;
import lombok.extern.log4j.Log4j2;
import org.dragonet.proxy.network.session.ProxySession;
import org.dragonet.proxy.network.session.cache.object.CachedEntity;
import org.dragonet.proxy.network.translator.PacketTranslator;
import org.dragonet.proxy.network.translator.annotations.PCPacketTranslator;
import org.dragonet.proxy.network.translator.types.EntityMetaTranslator;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@PCPacketTranslator(packetClass = ServerEntityStatusPacket.class)
public class PCEntityStatusTranslator extends PacketTranslator<ServerEntityStatusPacket> {
    private static Map<EntityStatus, EntityEventPacket.Event> eventMap = new HashMap<>();

    static {
        eventMap.put(EntityStatus.LIVING_HURT, EntityEventPacket.Event.HURT_ANIMATION);
        eventMap.put(EntityStatus.LIVING_HURT_SWEET_BERRY_BUSH, EntityEventPacket.Event.HURT_ANIMATION);
        eventMap.put(EntityStatus.LIVING_DEATH, EntityEventPacket.Event.DEATH_ANIMATION);
        eventMap.put(EntityStatus.FIREWORK_EXPLODE, EntityEventPacket.Event.FIREWORK_PARTICLES);
    }

    @Override
    public void translate(ProxySession session, ServerEntityStatusPacket packet) {
        CachedEntity cachedEntity = session.getEntityCache().getByRemoteId(packet.getEntityId());
        if(cachedEntity == null) {
            //log.info("(debug) Cached entity is null");
            return;
        }

        EntityEventPacket entityEventPacket = new EntityEventPacket();
        entityEventPacket.setRuntimeEntityId(cachedEntity.getProxyEid());

        EntityEventPacket.Event bedrockEvent = eventMap.get(packet.getStatus());
        if(bedrockEvent == null) {
            log.info("(debug) Unhandled entity status: " + packet.getStatus().name());
            return;
        }

        entityEventPacket.setEvent(bedrockEvent);
        entityEventPacket.setData(0);

        session.sendPacket(entityEventPacket);
    }
}
