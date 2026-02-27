package minimaceraoyunu.varliklar;

import minimaceraoyunu.GameEngine;
import minimaceraoyunu.model.Player;

public class WeaponItem extends Item {
    private final int attackBonus;

    public WeaponItem(int id, String name, String description, int attackBonus) {
        super(id, name, description);
        this.attackBonus = attackBonus;
    }

    @Override
    public void onUse(Player p, GameEngine ctx) {

        p.setAttackPower(p.getAttackPower() + attackBonus);
        System.out.println("\n*** " + this.name + " kuşanıldı. Saldırı gücün " + attackBonus + " puan arttı! Yeni Güç: " + p.getAttackPower() + " ***");
    }
}