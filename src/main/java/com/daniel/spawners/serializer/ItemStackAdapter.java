package com.daniel.spawners.serializer;

import com.google.gson.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack>  {


    @Override
    public JsonElement serialize(ItemStack stack, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("type", stack.getType().toString());
        object.addProperty("amount", stack.getAmount());
        return object;
    }

    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = new JsonObject();
        Material t = Material.getMaterial(object.get("type").getAsString());
        int amount = object.get("amount").getAsInt();
        return new ItemStack(t, amount);
    }
}
