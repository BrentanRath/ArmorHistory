package gay.bunnie.armorhistory.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;

public class MessageUtils {

    public static final Component PLUGIN_PREFIX = 
        Component.text("[", NamedTextColor.DARK_GRAY)
        .append(Component.text("ArmorHistory", NamedTextColor.GOLD))
        .append(Component.text("] ", NamedTextColor.DARK_GRAY));

    /**
     * Prepends the plugin prefix to a single message Component.
     * @param message The raw message Component.
     * @return The final, prefixed Component.
     */
    public static Component prefix(ComponentLike message) {
        // AI YAPPIN
        // ComponentLike is used for flexibility (accepts Component, TextComponent, etc.)
        return PLUGIN_PREFIX.append(message);
    }

    /**
     * Prepends the plugin prefix to a series of message Components.
     * This is useful when building the message piecewise at the call site.
     * @param components A variable number of Components to append.
     * @return The final, prefixed and combined Component.
     */
    public static Component prefix(ComponentLike... components) {
        Component finalMessage = PLUGIN_PREFIX;
        
        for (ComponentLike component : components) {
            finalMessage = finalMessage.append(component);
        }
        return finalMessage;
    }
}