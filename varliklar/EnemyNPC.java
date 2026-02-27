package minimaceraoyunu.varliklar;

import minimaceraoyunu.model.Player;

public class EnemyNPC extends NPC {
    private int hp;
    private final int damage;
    private final int initialHp;

    public EnemyNPC(String name, String initialDialogue, int hp, int damage) {
        super(name, initialDialogue);
        this.hp = hp;
        this.initialHp = hp;
        this.damage = damage;
    }

    public int getHp() {
        return hp;
    }

    public int getDamage() {
        return damage;
    }
    
    public int getInitialHp() {
        return initialHp;
    }

    public String takeDamage(int damage) {
        this.hp -= damage;
        if (this.hp <= 0) {
            this.hp = 0;
            return name + " yenildi!";
        } else {
            return name + " isabet aldı! Kalan HP: " + this.hp;
        }
    }

    public String attack(Player p) {
        p.decreaseHp(damage);
        return name + " sana " + damage + " hasar verdi! Kalan HP: " + p.getHp();
    }
}