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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by toonsev on 1/4/2017.
 */
public class LocalMaps {
    private static final String GENERATOR_FILE_NAME = "exo_generator.txt";

    private Map<String, LocalMap> mapsByMapId = new HashMap<>();

    public boolean mapLoaded(String mapId) {
        return mapsByMapId.containsKey(mapId);
    }

    public LocalMap createMap(String mapId, String envId, String generator) {
        if (mapsByMapId.containsKey(mapId))
            return null;
        if (Bukkit.getWorld(mapId) != null)
            return null;
        //if the map already exists there may be a generator saved as a file
        String fileGen = getGenerator(new File(Bukkit.getWorldContainer(), mapId));
        if (fileGen != null) generator = fileGen;

        WorldCreator worldCreator = new WorldCreator(mapId).generator(generator);
        World world = Bukkit.createWorld(worldCreator);

        saveGenerator(world, generator);

        return new LocalMap(mapId, envId, world);
    }

    private boolean saveGenerator(World world, String generator) {
        try {
            File generatorDescription = new File(world.getWorldFolder(), GENERATOR_FILE_NAME);
            generatorDescription.createNewFile();
            try (PrintWriter out = new PrintWriter(generatorDescription)) {
                out.write(generator);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getGenerator(File worldDir) {
        File generatorFile = new File(worldDir, GENERATOR_FILE_NAME);
        if (!generatorFile.isFile())
            return null;
        try (Scanner scanner = new Scanner(generatorFile)) {
            return scanner.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LocalMap addLoadedMap(String mapId, String envId) {
        if (mapsByMapId.containsKey(mapId))
            return null;
        World world = Bukkit.getWorld(mapId);
        if (world == null)
            return null;
        return new LocalMap(mapId, envId, world);
    }

    public boolean unloadMap(String mapId) {
        mapsByMapId.remove(mapId);
        if (Bukkit.getWorld(mapId) != null) {
            Bukkit.getWorld(mapId).getPlayers().forEach(p -> p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation()));
            Bukkit.unloadWorld(mapId, false);
        }

        File file = new File(Bukkit.getWorldContainer(), mapId);
        if (file.isDirectory()) {
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
