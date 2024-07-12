package com.daniel.spawners.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.EntityType;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum SpawnerType {

    PIG("porco", EntityType.PIG, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzhlOTg0NTBhNGMwMTdhMjA3NGM5OGJiYzk2MjMxNDkwNDNiNjBhZTVkYTRiNWEwYzA5ZjkyYjg2MjgzMWFiMCJ9fX0="),
    COW("vaca", EntityType.COW, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY2N2MwZTEwN2JlNzlkNzY3OWJmZTg5YmJjNTdjNmJmMTk4ZWNiNTI5YTMyOTVmY2ZkZmQyZjI0NDA4ZGNhMyJ9fX0="),
    SPIDER("aranha", EntityType.SPIDER, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWVjNTU3NDYwM2YzMDQ4ZjIxYWQ1YTNjOTRkOTcxMTU3MDYwMTFmZTZiYTY3NzgxMDkxYjhhOWFjMTBhZjU0ZiJ9fX0="),
    ZOMBIE("zumbi", EntityType.ZOMBIE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2MxZjI0NTFkZjdiYjZkZDNjMzE2NjA2YmQ5ZDEzMWQyZTM5OTZmZGNkMzk5ZjEyYTQ0ODc5ZTJiMzhmMThkNyJ9fX0="),
    BLAZE("blaze", EntityType.BLAZE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmViYjRkOTNmZWI0OWQ5MDRjNjFhOGZhMGVhZWFjNDFmZTM0NDQyY2FmZWE3ODAxMDM4ZWJmN2MzODFjODM5NyJ9fX0="),
    MAGMA("magma", EntityType.MAGMA_CUBE, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTFjOTdhMDZlZmRlMDRkMDAyODdiZjIwNDE2NDA0YWIyMTAzZTEwZjA4NjIzMDg3ZTFiMGMxMjY0YTFjMGYwYyJ9fX0="),
    WITCH("bruxa", EntityType.WITCH, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGFhOTg2YTZlMWMyZDg4ZmYxOThhYjJjMzI1OWU4ZDI2NzRjYjgzYTZkMjA2Zjg4M2JhZDJjOGFkYTgxOSJ9fX0="),
    SHEEP("ovelha", EntityType.SHEEP, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzBmNTAzOTRjNmQ3ZGJjMDNlYTU5ZmRmNTA0MDIwZGM1ZDY1NDhmOWQzYmM5ZGNhYzg5NmJiNWNhMDg1ODdhIn19fQ==");

    private String name;
    private EntityType entityType;
    private String url;

    public static SpawnerType findByName(String name) {
        return Arrays.stream(values()).filter($ -> $.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static SpawnerType findByType(SpawnerType type) {
        return Arrays.stream(values()).filter($ -> $ == type).findFirst().orElse(null);
    }

}
