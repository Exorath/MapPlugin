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

import com.exorath.plugin.map.local.LocalMap;
import com.exorath.plugin.map.local.LocalMaps;
import com.exorath.plugin.map.res.CommandInfo;
import com.exorath.plugin.map.SubCommandExecutor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Created by toonsev on 12/29/2016.
 */
public class CreateCommand implements SubCommandExecutor {
    private LocalMaps localMaps;

    public CreateCommand(LocalMaps localMaps){
        this.localMaps = localMaps;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        if(args.length == 0)
            return false;
        String mapId = args[0];
        String generator = null;
        if(args.length > 1)
            generator = args[1];
        if(generator == null)
            generator = "ExoMaps";
        World world = Bukkit.getWorld(mapId);
        LocalMap map = null;
        if(world == null)
            map = localMaps.createMap(mapId, null, generator);
        else
            map = localMaps.addLoadedMap(mapId, null);
        if(map == null){
            commandSender.sendMessage(ChatColor.RED + "Failed to create world, maybe it is already created/loaded?");
        }else{
            commandSender.sendMessage(ChatColor.GREEN + "World created, teleporting you now.");
            commandSender.sendMessage(ChatColor.GRAY + "If you wish to save the world use the '/maps save " + mapId + " <envId>' command.");
            if(commandSender instanceof Player)
                ((Player) commandSender).teleport(new Location(map.getWorld(), 0 , 70, 0));
        }
        return true;
    }

    @Override
    public CommandInfo getCommandInfo() {
        return new CommandInfo("create", new String[]{"mapId"}, new String[]{"generator"}, "Creates new map instance locally");
    }
}
