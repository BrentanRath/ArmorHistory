package gay.bunnie.armorhistory.database;

import gay.bunnie.armorhistory.Armorhistory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private Connection connection;
    private final Armorhistory plugin;

    public DatabaseManager(Armorhistory plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        try {
            String path = plugin.getDataFolder().getAbsolutePath() + "/claims.db";
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);

            String createTable = """
                CREATE TABLE IF NOT EXISTS claims (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    item_uuid TEXT NOT NULL,
                    player_name TEXT NOT NULL,
                    claim_timestamp TEXT NOT NULL,
                    UNIQUE(item_uuid, player_name, claim_timestamp)
                )
                """;

            try (PreparedStatement stmt = connection.prepareStatement(createTable)) {
                stmt.execute();
            }

            plugin.getLogger().info("Database initialized successfully!");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to close database connection: " + e.getMessage());
        }
    }

    public boolean addClaim(String itemUUID, String playerName, String timestamp) {
        if (getClaimCount(itemUUID) >= 3) {
            return false;
        }

        String sql = "INSERT INTO claims (item_uuid, player_name, claim_timestamp) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, itemUUID);
            stmt.setString(2, playerName);
            stmt.setString(3, timestamp);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to add claim: " + e.getMessage());
            return false;
        }
    }

    public boolean removeClaim(String itemUUID) {
        String getMostRecentSql = """
            SELECT id FROM claims
            WHERE item_uuid = ?
            ORDER BY id DESC
            LIMIT 1
            """;

        try (PreparedStatement stmt = connection.prepareStatement(getMostRecentSql)) {
            stmt.setString(1, itemUUID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int claimId = rs.getInt("id");

                String deleteSql = "DELETE FROM claims WHERE id = ?";
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, claimId);
                    deleteStmt.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to remove claim: " + e.getMessage());
        }

        return false;
    }

    public int getClaimCount(String itemUUID) {
        String sql = "SELECT COUNT(*) as count FROM claims WHERE item_uuid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, itemUUID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to get claim count: " + e.getMessage());
        }
        return 0;
    }

    public List<ClaimRecord> getClaims(String itemUUID) {
        List<ClaimRecord> claims = new ArrayList<>();
        String sql = "SELECT player_name, claim_timestamp FROM claims WHERE item_uuid = ? ORDER BY id ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, itemUUID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                claims.add(new ClaimRecord(
                    rs.getString("player_name"),
                    rs.getString("claim_timestamp")
                ));
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to get claims: " + e.getMessage());
        }

        return claims;
    }

    public boolean hasClaims(String itemUUID) {
        return getClaimCount(itemUUID) > 0;
    }
    public ClaimRecord getMostRecentClaim(String itemUUID) {
        String sql = """
            SELECT player_name, claim_timestamp FROM claims
            WHERE item_uuid = ?
            ORDER BY id DESC
            LIMIT 1
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, itemUUID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ClaimRecord(rs.getString("player_name"), rs.getString("claim_timestamp"));
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to get most recent claim: " + e.getMessage());
        }
        return null;
    }

    public static class ClaimRecord {
        private final String playerName;
        private final String timestamp;

        public ClaimRecord(String playerName, String timestamp) {
            this.playerName = playerName;
            this.timestamp = timestamp;
        }

        public String getPlayerName() {
            return playerName;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}
