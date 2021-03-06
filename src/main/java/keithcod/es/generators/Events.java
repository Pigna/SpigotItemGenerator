package keithcod.es.generators;


import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Events implements Listener
{

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {

    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent event)
    {
        Block block = event.getBlockPlaced();
        ItemStack blockItem = event.getItemInHand();
        if (checkItemName(blockItem))
        {
            String[] meta1 = blockItem.getItemMeta().getLore().get(0).split(" ");
            if (meta1[0].startsWith("Gen:"))
            {
                String uuid = meta1[1];
                if(Generators.INSTANCE.getConfig().getConfigurationSection("generators").contains(uuid))
                {
                    System.out.println("Placed generator [" + uuid + "]");
                    ConfigurationSection config = Generators.INSTANCE.getConfig().getConfigurationSection("generators").getConfigurationSection(uuid);
                    String key = key(block.getLocation());
                    if (!config.contains("locations"))
                    {
                        config.set("locations", new ArrayList<>());
                    }
                    List<String> locations = config.getStringList("locations");
                    locations.add(key);
                    config.set("locations", locations);
                    Generators.INSTANCE.getConfig().getConfigurationSection("locations").set(key, uuid);
                    Generators.INSTANCE.saveConfig();
                }
                else
                {
                    Generators.INSTANCE.sendMessage(event.getPlayer(), "Generator does no longer exist.");
                }
            }
        }
    }

    @EventHandler
    public void onBlockBroken(BlockBreakEvent event)
    {
        String key = key(event.getBlock().getLocation());
        if (Generators.INSTANCE.getConfig().getConfigurationSection("locations").contains(key))
        {
            String uuid = Generators.INSTANCE.getConfig().getConfigurationSection("locations").getString(key);
            Generators.INSTANCE.getConfig().getConfigurationSection("locations").set(key, null);
            List<String> locations = Generators.INSTANCE.getConfig().getConfigurationSection("generators").getConfigurationSection(uuid).getStringList("locations");
            locations.remove(key);
            Generators.INSTANCE.getConfig().getConfigurationSection("generators").getConfigurationSection(uuid).set("locations", locations);
            Generators.INSTANCE.saveConfig();
            System.out.println("Removed generator location.");
        }
    }

    public static String key(Location l)
    {
        return l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
    }

    public boolean checkItemName(ItemStack is)
    {
        if(is.hasItemMeta() && is.getItemMeta().hasDisplayName())
        {
            return is.getItemMeta().getDisplayName().toLowerCase().contains("generator");
        }
        return false;
    }
}
    
