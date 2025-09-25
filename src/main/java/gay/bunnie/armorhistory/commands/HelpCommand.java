package gay.bunnie.armorhistory.commands;

import gay.bunnie.armorhistory.Armorhistory;
import gay.bunnie.armorhistory.util.MessageUtils;

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
        sender.sendMessage(
                MessageUtils.prefix(
                    Component.text("=== ArmorHistory Commands ===", NamedTextColor.GOLD)
                )
            );
        Component helpMessageLineOne = Component.text("/claimitem ", NamedTextColor.YELLOW)
            .append(Component.text("- Claim the item in your hand (max 3 claims, no consecutive self-claims).", NamedTextColor.WHITE));
        Component helpMessageLineTwo = Component.text("/removeclaim ", NamedTextColor.YELLOW)
            .append(Component.text("- Remove the most recent claim (costs money).", NamedTextColor.WHITE));
        Component helpMessageLineThree = Component.text("/armorhistory help ", NamedTextColor.YELLOW)
            .append(Component.text("- Show this help message.", NamedTextColor.WHITE));
            
        sender.sendMessage(MessageUtils.prefix(helpMessageLineOne));
        sender.sendMessage(MessageUtils.prefix(helpMessageLineTwo));
        sender.sendMessage(MessageUtils.prefix(helpMessageLineThree));


        return true;
    }
}