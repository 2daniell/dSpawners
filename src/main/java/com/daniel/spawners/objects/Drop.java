package com.daniel.spawners.objects;

import com.daniel.spawners.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class Drop {

    private final String material;
    private final int amount;
    private final int prob;

    public ItemStack getItem() {
        return Utils.parseItemStack(material, amount);
    }
}
