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

import com.exorath.plugin.map.MapDownloadProvider;
import com.exorath.plugin.map.local.LocalMaps;
import com.exorath.plugin.map.res.CommandInfo;
import com.exorath.plugin.map.SubCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Created by toonsev on 12/29/2016.
 */
public class LoadCommand implements SubCommandExecutor {
    private LocalMaps localMaps;
    private MapDownloadProvider mapDownloadProvider;

    public LoadCommand(LocalMaps localMaps, MapDownloadProvider mapDownloadProvider) {
        this.localMaps = localMaps;
        this.mapDownloadProvider = mapDownloadProvider;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage("Insufficient arguments.");
            return false;
        }
        String mapId = args[0];
        String envId = args[1];
        String versionId = args.length >= 3 ? args[2] : null;
        if(localMaps.mapLoaded(mapId) || new File(Bukkit.getWorldContainer(), mapId).isDirectory()){
            commandSender.sendMessage(ChatColor.RED + "A version of this map is already loaded in.");
            commandSender.sendMessage(ChatColor.GRAY + "Please type /map unload " + mapId + " first.");
            return true;
        }
        File worldDir = new File(Bukkit.getWorldContainer(), mapId);
        worldDir.mkdirs();
        boolean success = mapDownloadProvider.downloadTo(mapId, envId, versionId, worldDir);
        if (success == true) {
            commandSender.sendMessage(ChatColor.GREEN + "Successfully downloaded map. Creating it now...");
            if (commandSender instanceof Player)
                ((Player) commandSender).performCommand("exomaps create " + mapId);
            return true;
        } else {
            commandSender.sendMessage(ChatColor.RED + "failed to download map, probably due to the saved zip file being corrupted");
            return false;
        }
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("load", new String[]{"mapId", "envId"}, new String[]{"versionId"}, "Loads a map version in to edit");
    }
}