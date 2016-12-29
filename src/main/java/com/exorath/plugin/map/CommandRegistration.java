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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by toonsev on 12/29/2016.
 */
public class CommandRegistration implements CommandExecutor{
    private Map<String, SubCommandExecutor> subCommands = new HashMap<>();
    public static void register(JavaPlugin plugin){
        PluginCommand command = plugin.getCommand("maps");
        command.setExecutor(new CommandRegistration());
        command.setPermission("exorath.maps.cmd");
    }

    public CommandRegistration(){
        //TODO add commands to subCommands map
    }

    public boolean onCommand(CommandSender commandSender, Command cmd, String alias, String[] args) {
        if(args.length > 10)//Why not.
            return false;
        if(args.length == 0)
            args = new String[]{"help"};//default to help command

        if(subCommands.containsKey(args[0])){
            List<String> argsList = Arrays.asList(args);
            argsList.remove(0);
            return subCommands.get(args[0]).onCommand(commandSender, argsList.toArray(new String[argsList.size()]));
        }
        return false;
    }
}
