package com.daniel.spawners.db;

import com.daniel.spawners.Main;
import com.daniel.spawners.enums.Permission;
import com.daniel.spawners.enums.SpawnerType;
import com.daniel.spawners.handler.SpawnerHandler;
import com.daniel.spawners.model.Spawner;
import com.daniel.spawners.model.SpawnerInstalled;
import com.daniel.spawners.objects.SpawnerStacker;
import com.daniel.spawners.utils.Utils;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class Database {

    private static Connection open() {
        final String host = Main.config().getString("MySQL.Host");
        final String user = Main.config().getString("MySQL.Username");
        final String database = Main.config().getString("MySQL.Database");
        final String port = Main.config().getString("MySQL.Port");
        final String pass = Main.config().getString("MySQL.Password");

        String url = "jdbc:mysql://"+host+":"+port+"/"+database+"?useUnicode=true&characterEncoding=utf8";

        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            Main.getInstance().getLogger().warning("Conexão com o banco de dados falhou.");
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
        }
        return null;
    }

    public static void table() {
        try(Connection con = open()) {

            String query = "CREATE TABLE IF NOT EXISTS polus_spawners (" +
                    "location VARCHAR(255) PRIMARY KEY, " +
                    "owner VARCHAR(36), " +
                    "type VARCHAR(255), " +
                    "stack INT, " +
                    "permission TEXT, " +
                    "storage TEXT)";

            try (PreparedStatement statement = con.prepareStatement(query)) {
                statement.execute();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void persist(SpawnerInstalled installed) {
        try(Connection con = open()) {

            String query = "INSERT INTO polus_spawners (location, owner, type, stack, permission, storage) " +
                    "VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "owner = VALUES(owner), " +
                    "type = VALUES(type), " +
                    "stack = VALUES(stack), " +
                    "permission = VALUES(permission), " +
                    "storage = VALUES(storage)";

            try(PreparedStatement stm = con.prepareStatement(query)) {

                stm.setString(1, Utils.getSerializedLocation(installed.getLocation()));
                stm.setString(2, installed.getOwner().toString());
                stm.setString(3, installed.getSpawner().getType().toString());
                stm.setInt(4, installed.getSpawnerStacker().getAmount());
                stm.setString(5, Utils.serializeMap(installed.getTargetPermissions()));
                stm.setString(6, Utils.itemStackArrayToBase64ByAdapter(installed.getStorage().toArray(new ItemStack[0])));

                stm.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(SpawnerInstalled installed) {
        try(Connection con = open()) {

            String query = "DELETE FROM polus_spawners WHERE location = ?";

            try(PreparedStatement stm = con.prepareStatement(query)) {
                stm.setString(1, Utils.getSerializedLocation(installed.getLocation()));
                stm.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static Set<SpawnerInstalled> load() {

        Set<SpawnerInstalled> set = new HashSet<>();

        try(Connection con = open()) {

            String query = "SELECT * FROM polus_spawners";

            try(PreparedStatement stm = con.prepareStatement(query)) {

                ResultSet rs = stm.executeQuery();

                while(rs.next()) {

                    Location location = Utils.getDeserializedLocation(rs.getString("location"));
                    UUID owner = UUID.fromString(rs.getString("owner"));
                    Spawner spawner = SpawnerHandler.findByType(SpawnerType.valueOf(rs.getString("type")));
                    int stack = rs.getInt("stack");
                    Map<UUID, Set<Permission>> permissions = Utils.deserializeMap(rs.getString("permission"));
                    List<ItemStack> storage = Arrays.stream(Utils.itemStackArrayFromBase64ByAdapter(rs.getString("storage"))).collect(Collectors.toList());

                    set.add(new SpawnerInstalled(owner, spawner, location, storage, permissions, stack));

                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return set;
    }

}
