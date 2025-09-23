package gay.bunnie.armorhistory.commands;

import gay.bunnie.armorhistory.Armorhistory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

    private final Armorhistory plugin;

    public HelpCommand(Armorhistory plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(Component.text("=== ArmorHistory Commands ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/claimitem", NamedTextColor.YELLOW).append(Component.text(" - Claim the item in your hand.", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("/removeclaim", NamedTextColor.YELLOW).append(Component.text(" - Remove the most recent claim (costs money).", NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("/armorhistory help", NamedTextColor.YELLOW).append(Component.text(" - Show this help message.", NamedTextColor.WHITE)));
        return true;
    }
}