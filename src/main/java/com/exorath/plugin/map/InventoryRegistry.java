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

package com.exorath.plugin.map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by toonsev on 1/2/2017.
 */
public class InventoryRegistry implements Listener {
    private Map<Inventory, InventoryListener> inventories = new HashMap<>();

    public InventoryRegistry() {
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryListener listener = inventories.get(event.getInventory());
        if (listener != null)
            listener.onClick(event);
    }

    @EventHandler
    public void onCose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        InventoryListener listener = inventories.get(event.getInventory());
        if (listener != null)
            listener.onClose((Player) event.getPlayer());
    }

    public void register(InventoryListener listener){
        inventories.put(listener.getInventory(), listener);
    }

    public void unregister(InventoryListener listener){
        inventories.remove(listener.getInventory());
    }
}
