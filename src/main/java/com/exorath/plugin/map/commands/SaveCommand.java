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

import com.exorath.plugin.map.MapUploadProvider;
import com.exorath.plugin.map.res.CommandInfo;
import com.exorath.plugin.map.SubCommandExecutor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

/**
 * Created by toonsev on 12/29/2016.
 */
public class SaveCommand implements SubCommandExecutor {
    MapUploadProvider mapUploadProvider;

    public SaveCommand(MapUploadProvider mapUploadProvider) {
        this.mapUploadProvider = mapUploadProvider;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage(ChatColor.RED + "Not enough arguments.");
            return false;
        }
        String mapId = args[0];
        String envId = args[1];

        World world = Bukkit.getWorld(mapId);
        if (world == null) {
            commandSender.sendMessage(ChatColor.RED + "No world loaded with the mapId.");
            return true;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");//for now we do this to ensure save success
        
        String versionId = mapUploadProvider.upload(world.getWorldFolder(), mapId, envId);
        if(versionId != null)
            commandSender.sendMessage(ChatColor.GREEN + "Successfully uploaded the map under the version: " + versionId);
        else
            commandSender.sendMessage(ChatColor.RED + "It appears like the upload failed, check the console.");
        return true;
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("save", new String[]{"mapId", "envId"}, new String[0], "saves an edited map");
    }
}