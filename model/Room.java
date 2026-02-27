package minimaceraoyunu.model;

import minimaceraoyunu.varliklar.Item;
import minimaceraoyunu.varliklar.NPC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class Room {
    private final int id;
    private final String name;
    private final String description;
    private final Map<String, Room> exits;
    private final List<Item> items;
    private final List<NPC> npcs;
    private boolean isLocked;

    public Room(int id, String name, String description, boolean isLocked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isLocked = isLocked;
        this.exits = new HashMap<>();
        this.items = new ArrayList<>();
        this.npcs = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public List<Item> getItems() {
        return items;
    }

    public List<NPC> getNpcs() {
        return npcs;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
    public void connect(String direction, Room neighbor) {
        exits.put(direction.toLowerCase(), neighbor);
    }
    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }
    public void describe() {
        System.out.println("\n=== " + name + " ===\n");
        System.out.println(description);
    
        String exitNames = exits.keySet().stream()
                .collect(Collectors.joining(", "));
        System.out.println("Çıkışlar: " + (exitNames.isEmpty() ? "Yok" : exitNames));

        if (!items.isEmpty()) {
            String itemNames = items.stream()
                    .map(Item::getName)
                    .collect(Collectors.joining(", "));
            System.out.println("Eşyalar: " + itemNames);
        }

        if (!npcs.isEmpty()) {
            String npcNames = npcs.stream()
                    .map(NPC::getName)
                    .collect(Collectors.joining(", "));
            System.out.println("Karakterler: " + npcNames);
        }

        if (isLocked) {
            System.out.println(">>> Bu oda şu an KİLİTLİ görünüyor. Açmak için bir anahtar bulmalısın. <<<");
        }
    }
}