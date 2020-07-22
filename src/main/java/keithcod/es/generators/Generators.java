package keithcod.es.generators;


import com.google.common.util.concurrent.AtomicDouble;
import java.util.ArrayList;
import java.util.List;
import keithcod.es.gui.GUIListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.logging.Logger;
import javafx.scene.paint.Color;
import keithcod.es.commands.GeneratorCmd;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class Generators extends JavaPlugin {

    public static Generators INSTANCE;

    private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    private ConfigurationSection generators;
    //TODO: temp prefix untill put in config
    private String prefix = Color.LIGHTGRAY + "[" + Color.GOLD + "Gen" + Color.LIGHTGRAY + "] " + Color.CHOCOLATE;

    //TODO: despawn timer
    //TODO: place blocks diffrentblocks (wool colors)
    //TODO: fix items falling off the block
    @Override
    public void onEnable()
    {
        INSTANCE = this;
        getCommand("gen").setExecutor(new GeneratorCmd());

        if (!getConfig().isConfigurationSection("generators"))
            getConfig().createSection("generators");
        if (!getConfig().isConfigurationSection("locations"))
            getConfig().createSection("locations");
        saveConfig();
        generators = getConfig().getConfigurationSection("generators");

        if (!setupEconomy())
        {
            log.severe(String.format("[%s] - No Vault dependency found! Economy related features will be disabled. ;)", getDescription().getName()));
        }
        getServer().getPluginManager().registerEvents(new Events(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);

        AtomicDouble t = new AtomicDouble(0);

        // Run every .25s
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable()
        {
            @Override
            public void run()
            {
                
                for (String k : generators.getKeys(false))
                {
                    ConfigurationSection gen = generators.getConfigurationSection(k);
                    double time = gen.getDouble("time");
                    boolean generate = t.doubleValue() % time == 0;
                    if (generate)
                    {
                        int amount = gen.getInt("amount");
                        Material mat = Material.getMaterial(gen.getString("item"));
                        ItemStack is = new ItemStack(mat, amount);
                        
                        //Adding name and lore to the item
                        ItemMeta im = is.getItemMeta();
                        setName(gen, im);
                        setLore(gen, im);
                        is.setItemMeta(im);
                        
                        //Spawn item on all locations
                        if (gen.getStringList("locations") != null)
                        {
                            for (String pos : gen.getStringList("locations"))
                            {
                                String[] loc = pos.split(",");
                                if(loc.length == 4)
                                {
                                    World world = Bukkit.getServer().getWorld(loc[0]);
                                    float x = Integer.parseInt(loc[1])+0.5f;
                                    float y = Integer.parseInt(loc[2])+1;
                                    float z = Integer.parseInt(loc[3])+0.5f;
                                    world.dropItem(new Location(world, x, y, z), is);
                                }
                            }
                        }
                    }
                }
                t.addAndGet(0.25);
            }
        }, 0L, 5L);
        System.out.print("ItemGenerators is enabled.");
    }

    @Override
    public void onDisable()
    {
        System.out.print("ItemGenerators is disabled.");
    }
    
    private boolean setupEconomy()
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    private void setLore(ConfigurationSection gen, ItemMeta im)
    {
        if (gen.contains("lore") && gen.getStringList("lore") != null)
        {
            List<String> loreList = new ArrayList<>();
            for (String lore : gen.getStringList("lore"))
            {
                loreList.add(lore.replace('&', '�'));
            }
            im.setLore(loreList);
        }
    }
    
    private void setName(ConfigurationSection gen, ItemMeta im)
    {
        if(gen.contains("name"))
        {
            im.setDisplayName(gen.getString("name").replace('&', '�'));
        }
    }
    
    /***
     * Send a player a message with prefix in colour.
     * @param player Player to receive the message.
     * @param message Message to send the player.
     */
    public void sendMessage(Player player, String message)
    {
        player.sendMessage(prefix + message);
    }
}
