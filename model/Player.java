package minimaceraoyunu.model;

import minimaceraoyunu.varliklar.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Player {
    private Room currentRoom;
    private final List<Item> inventory;
    private int hp;
    private int attackPower;

    public Player(int initialHp, int initialAttackPower) {
        this.hp = initialHp;
        this.attackPower = initialAttackPower;
        this.inventory = new ArrayList<>();
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }
    public void increaseHp(int amount) {
        this.hp += amount;
    }
    public void decreaseHp(int amount) {
        this.hp -= amount;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }
    public String inventoryText() {
        if (inventory.isEmpty()) {
            return "Envanterin boş.";
        }
        return "Envanterin: " + inventory.stream()
                .map(Item::getName)
                .collect(Collectors.joining(", "));
    }
}