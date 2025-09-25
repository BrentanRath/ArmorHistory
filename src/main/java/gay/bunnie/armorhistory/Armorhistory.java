package gay.bunnie.armorhistory;

import gay.bunnie.armorhistory.commands.ClaimItemCommand;
import gay.bunnie.armorhistory.commands.RemoveClaimCommand;
import gay.bunnie.armorhistory.commands.HelpCommand;
import gay.bunnie.armorhistory.database.DatabaseManager;
import gay.bunnie.armorhistory.util.ItemUtils;
import gay.bunnie.armorhistory.events.PlayerJoinEventListener;


import net.milkbowl.vault.economy.Economy;


import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public final class Armorhistory extends JavaPlugin {

    public static Armorhistory instance;
    private static Economy economy;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        setupEconomy();

        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();

        ItemUtils.loadClaimableItems();

        getCommand("claimitem").setExecutor(new ClaimItemCommand(this));
        getCommand("removeclaim").setExecutor(new RemoveClaimCommand(this));
        getCommand("armorhistory").setExecutor(new HelpCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(), this);

        getLogger().info("ArmorHistory enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("ArmorHistory disabled.");
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }
    public static Armorhistory getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
