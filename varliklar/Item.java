package minimaceraoyunu.varliklar;

import minimaceraoyunu.GameEngine;
import minimaceraoyunu.model.Player;

public abstract class Item {
    protected int id;
    protected String name;
    protected String description;

    public Item(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract void onUse(Player p, GameEngine ctx);

    @Override
    public String toString() {
        return name + " (" + description + ")";
    }
}