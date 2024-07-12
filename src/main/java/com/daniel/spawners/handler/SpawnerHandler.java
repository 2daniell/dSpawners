package com.daniel.spawners.handler;

import com.daniel.spawners.enums.SpawnerType;
import com.daniel.spawners.model.Spawner;

import java.util.HashSet;
import java.util.Set;

public class SpawnerHandler {

    private static final Set<Spawner> spawners = new HashSet<>();

    static {
        spawners.add(new Spawner("aranha", SpawnerType.SPIDER));
        spawners.add(new Spawner("blaze", SpawnerType.BLAZE));
        spawners.add(new Spawner("bruxa", SpawnerType.WITCH));
        spawners.add(new Spawner("magma", SpawnerType.MAGMA));
        spawners.add(new Spawner("porco", SpawnerType.PIG));
        spawners.add(new Spawner("vaca", SpawnerType.COW));
        spawners.add(new Spawner("zumbi", SpawnerType.ZOMBIE));
        spawners.add(new Spawner("ovelha", SpawnerType.SHEEP));

    }

    public static void load() {
        getSpawners().forEach(Spawner::load);
    }

    public static Spawner findByName(String name) {
        return spawners.stream().filter(e -> e.getType().getName().equals(name)).findFirst().orElse(null);
    }

    public static Spawner findByType(SpawnerType type) {
        return spawners.stream().filter(e -> e.getType().equals(type)).findFirst().orElse(null);
    }

    public static Set<Spawner> getSpawners() {
        return spawners;
    }
}
