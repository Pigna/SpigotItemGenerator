/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keithcod.es.commands;

import keithcod.es.generators.GUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author myron
 */
public class GeneratorCmd implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }
        if (!sender.hasPermission("generator.use"))
        {
            return false;
        }
        GUI.ShowGUI((Player) sender);
        return true;
    }
}
