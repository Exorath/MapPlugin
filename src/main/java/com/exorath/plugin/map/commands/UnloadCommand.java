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

import com.exorath.plugin.map.local.LocalMaps;
import com.exorath.plugin.map.res.CommandInfo;
import com.exorath.plugin.map.SubCommandExecutor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * Created by toonsev on 12/29/2016.
 */
public class UnloadCommand implements SubCommandExecutor {
    private LocalMaps localMaps;

    public UnloadCommand(LocalMaps localMaps) {
        this.localMaps = localMaps;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1) {
            commandSender.sendMessage(ChatColor.RED + "Not enough arguments.");
            return false;
        }
        String mapId = args[0];
        boolean unloaded = localMaps.unloadMap(mapId);
        if (unloaded)
            commandSender.sendMessage(ChatColor.GREEN + "The map is unloaded (if you didn't save it it's gone).");
        else
            commandSender.sendMessage(ChatColor.RED + "The map failed to unload.");
        return true;
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("unload", new String[]{"mapId"}, new String[0], "unloads the map locally");
    }
}