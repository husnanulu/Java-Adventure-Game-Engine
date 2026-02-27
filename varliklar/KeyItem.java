package minimaceraoyunu.varliklar;

import minimaceraoyunu.GameEngine;
import minimaceraoyunu.model.Player;

public class KeyItem extends Item {

    private final int unlocksRoomId;
    public KeyItem(int id, String name, String description, int unlocksRoomId) {
        super(id, name, description);
        this.unlocksRoomId = unlocksRoomId;
    }

    @Override
    public void onUse(Player p, GameEngine ctx) {
        p.getInventory().remove(this);

        if (ctx.unlockRoom(unlocksRoomId)) {
            System.out.println("\n*** " + this.name + " kullanıldı. Kapının kilidi açıldı! Artık geçiş serbest. ***");
        } else {
            System.out.println("\n*** " + this.name + " kullanıldı, ancak açılacak kilit bulunamadı. ***");
        }
    }
}