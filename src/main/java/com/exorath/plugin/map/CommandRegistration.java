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

import com.exorath.plugin.map.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created by toonsev on 12/29/2016.
 */
public class CommandRegistration implements CommandExecutor {
    private Map<String, SubCommandExecutor> subCommands = new HashMap<>();


    public CommandRegistration(){
        register(new HelpCommand());
        register(new CreateCommand());
        register(new ListCommand());
        register(new LoadCommand());
        register(new SaveCommand());
        register(new UnloadCommand());
    }
    private void register(SubCommandExecutor cmd){
        subCommands.put(cmd.getCommandInfo().getLabel(), cmd);
    }

    public boolean onCommand(CommandSender commandSender, Command cmd, String alias, String[] args) {
        if (args.length > 10)//Why not.
            return false;
        if (args.length == 0)
            args = new String[]{"help"};//default to help command

        if (subCommands.containsKey(args[0])) {
            List<String> argsList =  new ArrayList<>(Arrays.asList(args));
            argsList.remove(0);
            return subCommands.get(args[0].toLowerCase()).onCommand(commandSender, argsList.toArray(new String[argsList.size()]));
        }
        return false;
    }


    public static void register(JavaPlugin plugin) {
        PluginCommand command = plugin.getCommand("exomaps");
        command.setExecutor(new CommandRegistration());
        command.setPermission("exorath.maps.cmd");
    }


    private class HelpCommand implements SubCommandExecutor {
        @Override
        public boolean onCommand(CommandSender cmdSender, String[] args) {
            cmdSender.sendMessage(ChatColor.DARK_GRAY + "--------- " + ChatColor.DARK_GREEN + "Help: ExoMaps" + ChatColor.DARK_GRAY + " ------------");
            cmdSender.sendMessage(ChatColor.GOLD + "/exomaps");
            for(SubCommandExecutor subCommandExecutor : subCommands.values()){
                CommandInfo commandInfo = subCommandExecutor.getCommandInfo();
                String line = ChatColor.GOLD + " " + commandInfo.getLabel();
                if(commandInfo.getRequiredArgs().length > 0) {
                    line += ChatColor.GRAY;
                    for (String rArg : commandInfo.getRequiredArgs())
                        line += " <" + rArg + ">";
                }
                if(commandInfo.getOptionalArgs().length > 0) {
                    line += ChatColor.DARK_GRAY;
                    for (String oArgs : commandInfo.getOptionalArgs())
                        line += " (" + oArgs + ")";
                }
                line += ChatColor.DARK_GREEN + ": " + commandInfo.getDescription();
                cmdSender.sendMessage(line);
            }
            cmdSender.sendMessage(ChatColor.DARK_GRAY + "----------------------------------");
            return true;
        }

        @Override
        public CommandInfo getCommandInfo() {
            return new CommandInfo("help", new String[0], new String[0], "Returns list of all cmds");
        }
    }
}
