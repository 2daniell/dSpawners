package com.daniel.spawners.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum Permission {

    MODIFY("Modificar Spawner", Arrays.asList(
            "",
            "§7Permitir adicionar ou remover spawners",
            "§7do conjunto de spawners."
    )),

    ADMINISTER("Administrar Spawner", Arrays.asList(
            "",
            "§7Permitir administrar os spawners",
            "§7incluindo a configuração e controle."
    )),

    ATTACK("Atacar Mobs", Arrays.asList(
            "",
            "§7Permitir atacar os mobs gerados pelo spawner."
    )),

    COLLECT("Coletar Drops", Arrays.asList(
            "",
            "§7Permitir coletar os drops gerados pelo spawner."
    ));

    private String name;
    private List<String> description;

    public static Permission findByName(String name) {
        return Arrays.stream(values()).filter($ -> $.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
