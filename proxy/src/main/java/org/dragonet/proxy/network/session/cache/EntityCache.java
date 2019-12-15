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
package org.dragonet.proxy.network.session.cache;

import com.github.steveice10.mc.auth.data.GameProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dragonet.proxy.data.entity.EntityType;
import org.dragonet.proxy.network.session.cache.object.CachedEntity;
import org.dragonet.proxy.network.session.cache.object.CachedPlayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
@Getter
public class EntityCache implements Cache {
    private Map<Long, CachedEntity> entities = new HashMap<>();
    private Map<UUID, Long> bossbars = new HashMap<>();

    private final AtomicLong nextClientEntityId = new AtomicLong(1L); // 1 is for client
    private final Map<Integer, Long> remoteToClientMap = Collections.synchronizedMap(new HashMap<>());
    private final Map<Long, Integer> clientToRemoteMap = Collections.synchronizedMap(new HashMap<>());

    public CachedEntity getByProxyId(long entityId) {
        if(!clientToRemoteMap.containsKey(entityId)) {
            return null;
        }
        return entities.get(entityId);
    }

    public CachedEntity getByRemoteId(int entityId) {
        if(remoteToClientMap.containsKey(entityId)) {
            return entities.get(remoteToClientMap.get(entityId));
        }
        return null;
    }

    public CachedEntity newEntity(EntityType type, int entityId) {
        CachedEntity entity = new CachedEntity(type, nextClientEntityId.getAndIncrement(), entityId);

        entities.put(entity.getProxyEid(), entity);
        clientToRemoteMap.put(entity.getProxyEid(), entity.getRemoteEid());
        remoteToClientMap.put(entity.getRemoteEid(), entity.getProxyEid());
        return entity;
    }

    public CachedEntity newLocalEntity(EntityType type) {
        CachedEntity entity = new CachedEntity(type, nextClientEntityId.getAndIncrement());
        entities.put(entity.getProxyEid(), entity);
        return entity;
    }

    public long newBossBar(UUID uuid) {
        long proxyEid = nextClientEntityId.getAndIncrement();
        bossbars.put(uuid, proxyEid);
        return proxyEid;
    }

    public long removeBossBar(UUID uuid) {
        long proxyEid = bossbars.get(uuid);
        bossbars.remove(uuid);
        return proxyEid;
    }

    public CachedPlayer newPlayer(int entityId, GameProfile profile) {
        CachedPlayer entity = new CachedPlayer(nextClientEntityId.getAndIncrement(), entityId, profile);

        entities.put(entity.getProxyEid(), entity);
        clientToRemoteMap.put(entity.getProxyEid(), entity.getRemoteEid());
        remoteToClientMap.put(entity.getRemoteEid(), entity.getProxyEid());
        return entity;
    }

    public void destroyEntity(long proxyEid) {
        entities.remove(proxyEid);
        remoteToClientMap.remove(clientToRemoteMap.get(proxyEid));
        clientToRemoteMap.remove(proxyEid);
    }

    @Override
    public void purge() {
        entities.clear();
        clientToRemoteMap.clear();
        remoteToClientMap.clear();
    }
}
