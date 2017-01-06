/*
 * Copyright 2016 Exorath
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

package com.exorath.plugin.map;

import com.exorath.plugin.map.local.LocalMaps;
import com.exorath.plugin.map.worldgen.VoidGenerator;
import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by toonsev on 12/29/2016.
 */
public class Main extends JavaPlugin {
    private static InventoryRegistry inventoryRegistry;
    private LocalMaps localMaps;

    @Override
    public void onEnable() {
        localMaps = new LocalMaps();
        inventoryRegistry = new InventoryRegistry();
        Bukkit.getPluginManager().registerEvents(inventoryRegistry, this);
        CommandRegistration.register(this, localMaps);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static InventoryRegistry getInventoryRegistry() {
        return inventoryRegistry;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new VoidGenerator();
    }
}
