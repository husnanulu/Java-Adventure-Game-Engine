package minimaceraoyunu.varliklar;

import minimaceraoyunu.GameEngine;
import minimaceraoyunu.model.Player;

public class PotionItem extends Item {
    private final int healingAmount;

    public PotionItem(int id, String name, String description, int healingAmount) {
        super(id, name, description);
        this.healingAmount = healingAmount;
    }
    @Override
    public void onUse(Player p, GameEngine ctx) {
        p.increaseHp(healingAmount);
        p.getInventory().remove(this);
        System.out.println("\n*** " + this.name + " kullanıldı. " + healingAmount + " HP kazandın! Güncel HP: " + p.getHp() + " ***");
    }
}