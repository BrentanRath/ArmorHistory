package gay.bunnie.armorhistory.events;

import gay.bunnie.armorhistory.Armorhistory;
import gay.bunnie.armorhistory.util.MessageUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class PlayerJoinEventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(
            MessageUtils.prefix(
                Component.text("Welcome to the server, " + player.getName() + "!", NamedTextColor.GREEN)
            )
        );
    }
}
