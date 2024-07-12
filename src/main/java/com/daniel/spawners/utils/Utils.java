package com.daniel.spawners.utils;

import com.daniel.spawners.enums.Permission;
import com.daniel.spawners.serializer.ItemStackAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javassist.I;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Utils {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(ItemStack.class, new ItemStackAdapter()).create();

    private static final Gson gsons = new Gson();

    public static String serializeMap(Map<UUID, Set<Permission>> map) {
        return gsons.toJson(map);
    }

    public static Map<UUID, Set<Permission>> deserializeMap(String jsonString) {
        Type type = new TypeToken<Map<UUID, Set<Permission>>>() {}.getType();
        return gsons.fromJson(jsonString, type);
    }

    @SuppressWarnings("deprecation")
    public static ItemStack parseItemStack(String materialString, int amount) {
        String[] parts = materialString.split(":");
        int materialId = Integer.parseInt(parts[0]);

        if (parts.length > 1) {
            byte data = Byte.parseByte(parts[1]);
            return new ItemStack(Material.getMaterial(materialId), amount, data);
        } else {
            return new ItemStack(Material.getMaterial(materialId), amount);
        }
    }

    public static Location getDeserializedLocation(String s) {
        String[] parts = s.split(";");
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);
        String name = parts[3];
        World w = Bukkit.getWorld(name);


        return new Location(w, x, y, z);
    }

    public static boolean compare(Location loc1, Location loc2) {
        int loc1X = (int) loc1.getX();
        int loc1Y = (int) loc1.getY();
        int loc1Z = (int) loc1.getZ();

        int loc2X = (int) loc2.getX();
        int loc2Y = (int) loc2.getY();
        int loc2Z = (int) loc2.getZ();

        return (loc1X == loc2X && loc1Y == loc2Y && loc1Z == loc2Z);
    }

    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String itemStackArrayToBase64ByAdapter(ItemStack[] items) {
        Map<String, Pair<Integer, Short>> groupedItems = new HashMap<>();

        for (ItemStack itemStack : items) {
            String itemType = itemStack.getType().toString();
            int quantity = itemStack.getAmount();
            short durability = itemStack.getDurability();

            groupedItems.put(itemType, Pair.of(groupedItems.getOrDefault(itemType, Pair.of(0, (short) 0)).getLeft() + quantity, durability));
        }

        JsonArray jsonArray = new JsonArray();
        for (Map.Entry<String, Pair<Integer, Short>> entry : groupedItems.entrySet()) {
            JsonObject itemObject = new JsonObject();
            itemObject.addProperty("type", entry.getKey());
            itemObject.addProperty("amount", entry.getValue().getLeft());
            itemObject.addProperty("durability", entry.getValue().getRight());
            jsonArray.add(itemObject);
        }
        String json = gsons.toJson(jsonArray);

        return Base64.encodeBase64String(json.getBytes());
    }

    public static ItemStack[] itemStackArrayFromBase64ByAdapter(String data) throws IOException {
        byte[] decodedBytes = Base64.decodeBase64(data);
        String json = new String(decodedBytes);

        JsonArray jsonArray = gsons.fromJson(json, JsonArray.class);
        ItemStack[] items = new ItemStack[jsonArray.size()];

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject itemObject = jsonArray.get(i).getAsJsonObject();
            String itemType = itemObject.get("type").getAsString();
            int amount = itemObject.get("amount").getAsInt();
            short durability = itemObject.get("durability").getAsShort();
            Material material = Material.getMaterial(itemType);
            items[i] = new ItemStack(material, amount, durability);
        }

        return items;
    }


    public static String getSerializedLocation(Location loc) {
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getWorld().getName();
    }
}
