package gay.bunnie.armorhistory.commands;

import gay.bunnie.armorhistory.Armorhistory;
import gay.bunnie.armorhistory.util.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RemoveClaimCommand implements CommandExecutor {

    private final Armorhistory plugin;

    public RemoveClaimCommand(Armorhistory plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        double cost = plugin.getConfig().getDouble("remove-cost");
        if (Armorhistory.getEconomy().getBalance(player) < cost) {
            player.sendMessage(Component.text("You need " + String.format("%.2f", cost) + " currency to remove a claim.", NamedTextColor.RED));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!ItemUtils.removeClaim(item)) {
            player.sendMessage(Component.text("This item has no claims to remove.", NamedTextColor.RED));
            return true;
        }

        Armorhistory.getEconomy().withdrawPlayer(player, cost);
        player.sendMessage(NamedTextColor.GREEN + "Removed the most recent claim for " + String.format("%.2f", cost) + " currency.");
        return true;
    }
}