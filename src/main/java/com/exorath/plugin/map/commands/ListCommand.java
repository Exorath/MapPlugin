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
import com.exorath.plugin.map.res.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Created by toonsev on 12/29/2016.
 */
public class ListCommand implements SubCommandExecutor {
    private MapListProvider mapListProvider;
    private EnvDetailProvider envDetailProvider;

    public ListCommand(MapListProvider mapListProvider, EnvDetailProvider envDetailProvider) {
        this.mapListProvider = mapListProvider;
        this.envDetailProvider = envDetailProvider;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {//this command does not need arguments
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command is for players");
            return false;
        }
        Player player = (Player) commandSender;
        MapListInv mapList = new MapListInv(player);
        return true;
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("list", new String[0], new String[0], "Opens the map list menu");
    }

    private class EnvDetailInv implements InventoryListener {
        private EnvDetail envDetail;
        private Inventory inventory;
        private int page = 0;
        private String mapId;
        private String envId;
        private Map<Integer, String> lastVersionByPage = new HashMap<>();
        private Map<Integer, String> versionBySlot = new HashMap<>();
        private Player player;

        public EnvDetailInv(Player player, String mapId, String envId, EnvDetail envDetail) {
            this.player = player;
            this.mapId = mapId;
            this.envId = envId;
            this.envDetail = envDetail;
            inventory = Bukkit.createInventory(null, 36, mapId + "/" + envId);
            Main.getInventoryRegistry().register(this);
            fill();
            player.openInventory(inventory);
        }

        void fill() {
            clearInventory(inventory, 36);
            String lastVersion = lastVersionByPage.get(page);
            if (page != 0 && lastVersion == null)
                return;//No cached lastVersion
            List<Version> versions = envDetail.getVersions(lastVersion, 26);
            if (versions == null)
                return;
            int i = 0;
            String startId;
            for (Version version : versions) {
                ItemStack is = new ItemStack(Material.PAPER, 1);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(ChatColor.GOLD + "Version: " + version.getId());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.WHITE + "- Date: " + new Date(version.getLastUpdated()).toString());
                lore.add(ChatColor.WHITE + "- Size: " + version.getSize() + " bytes");
                lore.add("");
                lore.add(ChatColor.DARK_GREEN + "Load in " + ChatColor.DARK_GRAY + "(Click)");
                im.setLore(lore);
                is.setItemMeta(im);
                inventory.setItem(i + 9, is);
                versionBySlot.put(i + 9, version.getId());
                i++;
                if (i > 26 && versions.size() > 26) {
                    if (lastVersionByPage.containsKey(page))
                        lastVersionByPage.put(page + 1, version.getId());
                    break;
                }
            }
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
                if (envDetail.isTruncated()) {
                    page++;
                    fill();
                }
            } else if (event.getSlot() == PREVIOUS_SLOT) {
                if (page > 0) {
                    page--;
                    fill();
                }
            } else if (versionBySlot.containsKey(event.getSlot())) {//Loads map
                Player player = (Player) event.getWhoClicked();
                player.performCommand("exomaps load " + mapId + " " + envId + " " + versionBySlot.get(event.getSlot()));
                player.closeInventory();
            }
        }

        @Override
        public void onClose(Player player) {
            Main.getInventoryRegistry().unregister(this);
        }
    }

    private class EnvsListInv implements InventoryListener {
        private Inventory inventory;
        private MapInfo mapInfo;
        private Player player;

        private Map<Integer, String> envIdBySlot = new HashMap<>();

        public EnvsListInv(Player player, MapInfo mapInfo) {
            this.player = player;
            this.mapInfo = mapInfo;

            inventory = Bukkit.createInventory(null, 36);
            fill();
            Main.getInventoryRegistry().register(this);
            player.openInventory(inventory);
        }

        private void fill() {
            clearInventory(inventory, 36);
            //set buttons
            int i = 0;
            envIdBySlot.clear();
            for (Map.Entry<String, EnvInfo> entry : mapInfo.getEnvInfos().entrySet()) {

                ItemStack is = new ItemStack(Material.PAPER, 1);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName(ChatColor.GOLD + "Environment: " + entry.getKey());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.WHITE + "- Date: " + entry.getValue().getLastModified());
                lore.add(ChatColor.WHITE + "- Size: " + entry.getValue().getSize() + " bytes");
                lore.add(" ");
                lore.add(ChatColor.DARK_GREEN + "View versions " + ChatColor.DARK_GRAY + "(Left Click)");
                lore.add(ChatColor.DARK_GREEN + "Load in " + ChatColor.DARK_GRAY + "(Right Click)");
                im.setLore(lore);
                is.setItemMeta(im);
                inventory.setItem(i + 9, is);
                envIdBySlot.put(i + 9, entry.getKey());
                i++;
                if (i >= 27)
                    return;
            }
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
            if (envIdBySlot.containsKey(event.getSlot())) {
                String envId = envIdBySlot.get(event.getSlot());
                if (event.getClick() == ClickType.RIGHT) {
                    player.performCommand("exomaps load " + mapInfo.getMapName() + " " + envId);
                    player.closeInventory();
                } else
                    new EnvDetailInv(player, mapInfo.getMapName(), envId, envDetailProvider.getEnvDetail(mapInfo.getMapName(), envIdBySlot.get(event.getSlot())));
            }
        }

        @Override
        public void onClose(Player player) {
            Main.getInventoryRegistry().unregister(this);
        }
    }

    private class MapListInv implements InventoryListener {
        private Inventory inventory;
        private int page = 0;
        private Map<Integer, MapInfo> mapInfoBySlot = new HashMap<>();
        private Player player;

        public MapListInv(Player player) {
            this.player = player;
            inventory = Bukkit.createInventory(null, 36);
            fill();
            Main.getInventoryRegistry().register(this);
            player.openInventory(inventory);
        }

        private void fill() {
            clearInventory(inventory, 36);
            //set buttons
            int startIndex = page * 27;
            mapInfoBySlot.clear();
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
                mapInfoBySlot.put(i + 9, mapInfo);
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
            } else if (mapInfoBySlot.containsKey(event.getSlot())) {
                EnvsListInv envListInv = new EnvsListInv(player, mapInfoBySlot.get(event.getSlot()));
            }
        }

        @Override
        public void onClose(Player player) {
            Main.getInventoryRegistry().unregister(this);
        }
    }

    private static void clearInventory(Inventory inventory, int slots) {
        for (int i = 0; i < slots; i++)
            inventory.clear(i);
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