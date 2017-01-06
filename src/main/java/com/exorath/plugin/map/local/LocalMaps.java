/*
 * Copyright 2017 Exorath
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.exorath.plugin.map.local;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by toonsev on 1/4/2017.
 */
public class LocalMaps {

    private Map<String, LocalMap> mapsByMapId = new HashMap<>();

    public boolean mapLoaded(String mapId) {
        return mapsByMapId.containsKey(mapId);
    }

    public LocalMap createMap(String mapId, String envId, String generator) {
        if (mapsByMapId.containsKey(mapId))
            return null;
        if (Bukkit.getWorld(mapId) != null)
            return null;
        WorldCreator worldCreator = new WorldCreator(mapId).generator(generator);
        return new LocalMap(mapId, envId, Bukkit.createWorld(worldCreator));
    }

    public LocalMap addLoadedMap(String mapId, String envId) {
        if (mapsByMapId.containsKey(mapId))
            return null;
        World world = Bukkit.getWorld(mapId);
        if (world == null)
            return null;
        return new LocalMap(mapId, envId, world);
    }

    public boolean unloadMap(String mapId){
        mapsByMapId.remove(mapId);
        if(Bukkit.getWorld(mapId) != null){
            Bukkit.getWorld(mapId).getPlayers().forEach(p -> p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()));
            Bukkit.unloadWorld(mapId, false);
        }

        File file = new File(Bukkit.getWorldContainer(), mapId);
        if(file.isDirectory()){
            deleteDir(file);
            return true;
        }
        return false;
    }

    void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

}
