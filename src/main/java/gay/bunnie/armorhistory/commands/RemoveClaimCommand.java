package gay.bunnie.armorhistory.commands;

import gay.bunnie.armorhistory.Armorhistory;
import gay.bunnie.armorhistory.util.ItemUtils;
import gay.bunnie.armorhistory.util.MessageUtils;


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
            sender.sendMessage(
                MessageUtils.prefix(
                    Component.text("Only players can use this command.", NamedTextColor.RED)
                )
            );
            return true;
        }

        double cost = plugin.getConfig().getDouble("remove-cost");
        if (Armorhistory.getEconomy().getBalance(player) < cost) {
            sender.sendMessage(
                MessageUtils.prefix(
                    Component.text("You need " + String.format("%.2f", cost) + " currency to remove a claim.", NamedTextColor.RED)
                )
            );
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!ItemUtils.removeClaim(item)) {
            sender.sendMessage(
                MessageUtils.prefix(
                    Component.text("This item has no claims to remove.", NamedTextColor.RED)
                )
            );
            return true;
        }

        Armorhistory.getEconomy().withdrawPlayer(player, cost);
        sender.sendMessage(
                MessageUtils.prefix(
                    Component.text("Removed the most recent claim for " + String.format("%.2f", cost) + " currency.", NamedTextColor.GREEN)
                )
            );
        return true;
    }
}