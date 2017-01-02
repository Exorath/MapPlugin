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

import com.exorath.plugin.map.*;
import com.exorath.plugin.map.res.CommandInfo;
import com.exorath.plugin.map.res.EnvInfo;
import com.exorath.plugin.map.res.MapInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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

    private class MapInv implements InventoryListener{
        private Inventory inventory;
        private int page = 0;

        public MapInv(){
            inventory = Bukkit.createInventory(null, 36);
            Main.getInventoryRegistry().register(this);
        }
        @Override
        public Inventory getInventory() {
            return inventory;
        }

        @Override
        public void onClick(InventoryClickEvent event) {

        }

        @Override
        public void onClose(Player player) {
            Main.getInventoryRegistry().unregister(this);
        }
    }

    private class MapList implements InventoryListener {
        private Inventory inventory;

        private int page = 0;

        public MapList() {
            inventory = Bukkit.createInventory(null, 36);
            //set buttons
            fill();
            Main.getInventoryRegistry().register(this);
        }

        void fill() {
            int startIndex = page * 27;
            for (int i = 0; i < 36; i++)
                inventory.clear(i);
            for (int i = 0; i < 27; i++) {
                MapInfo mapInfo = mapListProvider.getMap(startIndex + i);
                Bukkit.broadcastMessage("info:" + mapInfo);
                if (mapInfo == null)
                    break;
                ItemStack is = new ItemStack(Material.MAP, 1);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(ChatColor.GOLD + mapInfo.getMapName());
                List<String> lore = new ArrayList<>();
                for (Map.Entry<String, EnvInfo> entry : mapInfo.getEnvInfos().entrySet()) {
                    lore.add(ChatColor.WHITE + "- " + entry.getKey() + ": " + entry.getValue().getLastModified());
                }
                im.setLore(lore);
                is.setItemMeta(im);
                inventory.setItem(i + 9, is);
            }
            //TODO set refresh and paging buttons
            if (page > 0)
                inventory.setItem(PREVIOUS_SLOT, PREVIOUS_PAGE_IS);
            inventory.setItem(REFRESH_SLOT, REFRESH_IS);

            if (inventory.getItem(35) != null && inventory.getItem(35).getType() != Material.AIR)
                inventory.setItem(NEXT_SLOT, NEXT_PAGE_IS);
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        @Override
        public void onClick(InventoryClickEvent event) {
            if (event.getClickedInventory() != event.getView().getTopInventory())
                return;
            event.setCancelled(true);
            if (event.getSlot() == NEXT_SLOT) {
                if (inventory.getItem(35) != null && inventory.getItem(35).getType() != Material.AIR) {
                    page++;
                    fill();
                }
            } else if (event.getSlot() == PREVIOUS_SLOT) {
                if (page > 0) {
                    page--;
                    fill();
                }
            } else if (event.getSlot() == REFRESH_SLOT) {
                mapListProvider.clearCache();
                fill();
            }
        }

        @Override
        public void onClose(Player player) {
            Main.getInventoryRegistry().unregister(this);
        }
    }

    private static final int PREVIOUS_SLOT = 0;
    private static final int NEXT_SLOT = 8;
    private static final int REFRESH_SLOT = 4;

    private static final ItemStack NEXT_PAGE_IS = new ItemStack(Material.ARROW, 1) {{
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Next" + ChatColor.DARK_GRAY + " (Click)");
        setItemMeta(itemMeta);
    }};

    private static final ItemStack PREVIOUS_PAGE_IS = new ItemStack(Material.ARROW, 1) {{
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Previous" + ChatColor.DARK_GRAY + " (Click)");
        setItemMeta(itemMeta);
    }};

    private static final ItemStack REFRESH_IS = new ItemStack(Material.SLIME_BALL, 1) {{
        ItemMeta itemMeta = this.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Refresh entries" + ChatColor.DARK_GRAY + " (Click)");
        setItemMeta(itemMeta);
    }};
}