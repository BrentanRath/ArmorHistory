package gay.bunnie.armorhistory.commands;

import gay.bunnie.armorhistory.Armorhistory;
import gay.bunnie.armorhistory.util.ItemUtils;
import gay.bunnie.armorhistory.util.ItemUtils.ClaimResult;
import gay.bunnie.armorhistory.util.MessageUtils; 

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

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
            sender.sendMessage(
                MessageUtils.prefix(
                    Component.text("Only players can use this command.", NamedTextColor.RED)
                )
            );
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!ItemUtils.isClaimable(item)) {
            player.sendMessage(
                MessageUtils.prefix(
                    Component.text("You must hold a claimable item (armor, tool, or weapon).", NamedTextColor.RED)
                )
            );
            return true;
        }

        ClaimResult result = ItemUtils.addClaim(item, player.getName());
        
        switch (result) {
            case SUCCESS -> player.sendMessage(
                MessageUtils.prefix(
                    Component.text("You have successfully claimed this item.", NamedTextColor.GREEN)
                )
            );
            case TOO_MANY_CLAIMS -> {
                int claimCount = ItemUtils.getClaimCount(item);
                
                Component message = Component.text("This item already has the maximum of ", NamedTextColor.RED)
                    .append(Component.text("3", NamedTextColor.YELLOW, TextDecoration.BOLD))
                    .append(Component.text(" claims (", NamedTextColor.RED))
                    .append(Component.text(claimCount, NamedTextColor.YELLOW))
                    .append(Component.text("/3).", NamedTextColor.RED));
                    
                player.sendMessage(MessageUtils.prefix(message));
            }
            case CONSECUTIVE_NOT_ALLOWED -> player.sendMessage(
                MessageUtils.prefix(
                    Component.text("You cannot claim this item twice in a row. Another player must claim it before you can claim again.", NamedTextColor.RED)
                )
            );
            case NOT_CLAIMABLE -> player.sendMessage(
                MessageUtils.prefix(
                    Component.text("This item is not claimable.", NamedTextColor.RED)
                )
            );
            default -> player.sendMessage(
                MessageUtils.prefix(
                    Component.text("Failed to claim the item due to an internal error. Please check the console.", NamedTextColor.DARK_RED)
                )
            );
        }
        return true;
    }
}