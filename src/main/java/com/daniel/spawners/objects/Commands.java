package com.daniel.spawners.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Commands {

    private List<String> commands;
    private double prob;
}
