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

package com.exorath.plugin.map.commands;

import com.exorath.plugin.map.CommandInfo;
import com.exorath.plugin.map.MapListProvider;
import com.exorath.plugin.map.SubCommandExecutor;
import com.exorath.plugin.map.impl.MapServiceListProvider;
import com.exorath.plugin.map.res.EnvInfo;
import com.exorath.plugin.map.res.MapInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by toonsev on 12/29/2016.
 */
public class ListCommand implements SubCommandExecutor {
    private MapListProvider mapListProvider;

    public ListCommand(MapListProvider mapListProvider) {
        this.mapListProvider = mapListProvider;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {//this command does not need arguments
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command is for players");
            return false;
        }
        Player player = (Player) commandSender;
        MapList mapList = new MapList();
        player.openInventory(mapList.getInventory());
        return true;
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("list", new String[0], new String[0], "Opens the map list menu");
    }

    private class MapList {
        private Inventory inventory;

        public MapList() {
            inventory = Bukkit.createInventory(null, 36);
            fill(0);
        }

        void fill(int startIndex) {
            for (int i = 0; i < 27; i++)
                inventory.clear(i);
            for (int i = 0; i < 27; i++) {
                MapInfo mapInfo = mapListProvider.getMap(startIndex + i);
                if (mapInfo == null)
                    break;
                ItemStack is = new ItemStack(Material.MAP, 1);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(ChatColor.GOLD + mapInfo.getMapName());
                List<String> lore = new ArrayList<>();
                for (Map.Entry<String, EnvInfo> entry : mapInfo.getEnvInfos().entrySet()) {
                    lore.add("- " + entry.getKey() + ": " + entry.getValue().getLastModified());
                }
                im.setLore(lore);
                is.setItemMeta(im);
                inventory.setItem(i, is);
            }
            //TODO set refresh and paging buttons
        }

        public Inventory getInventory() {
            return inventory;
        }
    }
}