package gay.bunnie.armorhistory.commands;

import gay.bunnie.armorhistory.util.ItemUtils;
import gay.bunnie.armorhistory.Armorhistory;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class ClaimItemCommand implements CommandExecutor {
    private final Armorhistory plugin;

    public ClaimItemCommand(Armorhistory plugin) {
        this.plugin = plugin;
    }

    @Override   
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!ItemUtils.isClaimable(item)) {
            player.sendMessage(Component.text("You must hold a claimable item (armor, tool, or weapon).", NamedTextColor.RED));
            return true;
        }

        boolean success = ItemUtils.addClaim(item, player.getName());
        if (success) {
            player.sendMessage(Component.text("You have claimed this item.", NamedTextColor.GREEN));
        } else {
            int claimCount = ItemUtils.getClaimCount(item);
            player.sendMessage(Component.text("This item already has the maximum of 3 claims (" + claimCount + "/3).", NamedTextColor.RED));
        }
        return true;
    }
}