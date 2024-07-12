package com.daniel.spawners.cache;

import com.daniel.spawners.Main;
import com.daniel.spawners.db.Database;
import com.daniel.spawners.model.SpawnerInstalled;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class Cache<T> {

    protected final Set<T> cacheSet = ConcurrentHashMap.newKeySet();

    public static void persistData(Cache<?> cache) {
        new BukkitRunnable() {
            @Override
            public void run() {

                cache.persist();

            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 20L * 60 * 5, ((20L * 60) * 60));
    }

    public T find(Predicate<T> predicate) {
        return cacheSet.stream().filter(predicate).findFirst().orElse(null);
    }

    public void add(T element) {
        cacheSet.add(element);
    }

    public void remove(T element) {
        cacheSet.remove(element);
    }

    public void persist() {
        for (T t : cacheSet) {

            if (t instanceof SpawnerInstalled) {

                SpawnerInstalled installed = (SpawnerInstalled) t;
                Database.persist(installed);

            }
        }
    }

}
