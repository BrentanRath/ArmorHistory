package gay.bunnie.armorhistory.util;

import gay.bunnie.armorhistory.Armorhistory;
import gay.bunnie.armorhistory.database.DatabaseManager;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.Component;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.text.SimpleDateFormat;
import java.util.*;

public class ItemUtils {

    private static Set<Material> claimableMaterials;

    private static final NamespacedKey ITEM_UUID_KEY = new NamespacedKey(Armorhistory.getInstance(), "item_uuid");

    public static void loadClaimableItems() {
        claimableMaterials = EnumSet.noneOf(Material.class);
        List<String> configItems = Armorhistory.getInstance().getConfig().getStringList("claimable-items");

        for (String itemName : configItems) {
            try {
                Material material = Material.valueOf(itemName.toUpperCase());
                claimableMaterials.add(material);
            } catch (IllegalArgumentException e) {
                Armorhistory.getInstance().getLogger().warning("Invalid material in claimable-items config: " + itemName);
            }
        }
    }

    public static boolean isClaimable(ItemStack item) {
        if (claimableMaterials == null) {
            loadClaimableItems();
        }
        return item != null && claimableMaterials.contains(item.getType());
    }

    public static ClaimResult addClaim(ItemStack item, String playerName) {
        if (!isClaimable(item)) return ClaimResult.NOT_CLAIMABLE;

        String itemUUID = getOrCreateItemUUID(item);
        DatabaseManager dbManager = Armorhistory.getInstance().getDatabaseManager();

        if (dbManager.getClaimCount(itemUUID) >= 3) {
            return ClaimResult.TOO_MANY_CLAIMS;
        }

        DatabaseManager.ClaimRecord mostRecent = dbManager.getMostRecentClaim(itemUUID);
        if (mostRecent != null && mostRecent.getPlayerName().equalsIgnoreCase(playerName)) {
            return ClaimResult.CONSECUTIVE_NOT_ALLOWED;
        }

        String dateFormat = Armorhistory.getInstance().getConfig().getString("date-format", "yyyy-MM-dd HH:mm:ss");
        String timestamp = new SimpleDateFormat(dateFormat).format(new Date());

        boolean success = dbManager.addClaim(itemUUID, playerName, timestamp);
        if (success) {
            updateItemLore(item);
            return ClaimResult.SUCCESS;
        }
        return ClaimResult.FAILURE;
    }

    public static boolean removeClaim(ItemStack item) {
        if (!isClaimable(item)) return false;

        String itemUUID = getItemUUID(item);
        if (itemUUID == null) return false;

        DatabaseManager dbManager = Armorhistory.getInstance().getDatabaseManager();
        if (!dbManager.hasClaims(itemUUID)) return false;

        boolean success = dbManager.removeClaim(itemUUID);
        if (success) {
            updateItemLore(item);
        }
        return success;
    }

    public static int getClaimCount(ItemStack item) {
        if (!isClaimable(item)) return 0;

        String itemUUID = getItemUUID(item);
        if (itemUUID == null) return 0;

        return Armorhistory.getInstance().getDatabaseManager().getClaimCount(itemUUID);
    }

    private static String getOrCreateItemUUID(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String uuid = meta.getPersistentDataContainer().get(ITEM_UUID_KEY, PersistentDataType.STRING);

        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            meta.getPersistentDataContainer().set(ITEM_UUID_KEY, PersistentDataType.STRING, uuid);
            item.setItemMeta(meta);
        }

        return uuid;
    }

    private static String getItemUUID(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(ITEM_UUID_KEY, PersistentDataType.STRING);
    }

    private static void updateItemLore(ItemStack item) {
        String itemUUID = getItemUUID(item);
        if (itemUUID == null) return;

        DatabaseManager dbManager = Armorhistory.getInstance().getDatabaseManager();
        List<DatabaseManager.ClaimRecord> claims = dbManager.getClaims(itemUUID);

        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();

        Collections.reverse(claims);
        for (int i = 0; i < claims.size(); i++) {
            DatabaseManager.ClaimRecord claim = claims.get(i);
            Component claimComponent = Component.text(claim.getPlayerName() + " claimed on " + claim.getTimestamp());
            if (i == 0) {
                claimComponent = claimComponent.color(NamedTextColor.GREEN);
            } else {
                claimComponent = claimComponent.color(NamedTextColor.GRAY);
            }

            lore.add(claimComponent);
        }

        meta.lore(lore);
        item.setItemMeta(meta);
    }

    public enum ClaimResult {
        SUCCESS,
        TOO_MANY_CLAIMS,
        CONSECUTIVE_NOT_ALLOWED,
        NOT_CLAIMABLE,
        FAILURE
    }
}