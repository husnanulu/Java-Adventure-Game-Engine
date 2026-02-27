/*
package minimaceraoyunu.ana;

public class App {
    public static void main(String[] args) {
        GameEngine engine = new GameEngine();
        engine.start();
    }
}
package minimaceraoyunu.ana;

import minimaceraoyunu.model.Player;
import minimaceraoyunu.model.Room;
import minimaceraoyunu.varliklar.*;
import minimaceraoyunu.diyalog.*;

import java.util.*;

public class GameEngine {

    private Player player;
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("=== Mini Macera ===");

        setupWorld();

        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();
            if (!processCommand(input)) break;
        }
    }

    private void setupWorld() {

        // Odalar
        Room salon = new Room("salon", "Salon", "Geniş bir giriş salonundasın.");
        Room silahOdasi = new Room("silah", "Silah Odası", "Duvarlarda kılıçlar asılı.");
        Room koridor = new Room("koridor", "Koridor", "Dar bir koridordasın.");
        Room depo = new Room("depo", "Depo", "Eski sandıklarla dolu bir depo.");
        Room bahce = new Room("bahce", "Bahçe", "Açık bir bahçeye çıktın.");

        // Bağlantılar
        salon.connect("east", silahOdasi);
        salon.connect("south", koridor);
        koridor.connect("south", depo);
        depo.connect("east", bahce);

        // Itemlar
        salon.getItems().add(new KeyItem("key_red", "Kırmızı Anahtar", "Kırmızı bir kapıyı açabilir."));
        depo.getItems().add(new PotionItem("potion", "İksir", "Canını artırır."));
        silahOdasi.getItems().add(new WeaponItem("sword", "Kılıç", "Saldırı gücünü artırır."));

        // NPC
        FriendlyNPC muhafiz = new FriendlyNPC("Muhafız", createGuardConversation());
        salon.getNpcs().add(muhafiz);

        EnemyNPC kurt = new EnemyNPC("Kurt", 15);
        bahce.getNpcs().add(kurt);

        // Player
        player = new Player(salon);
    }

    private ConversationNode createGuardConversation() {
        ConversationNode start = new ConversationNode("Dur yolcu! Buralar tehlikelidir. Ne ararsın?");
        start.addChoice(new ConversationChoice("Sadece geçiyorum.", null));
        start.addChoice(new ConversationChoice("Tehlike nerede?", 
            new ConversationNode("İlerde yabani bir kurt var dikkat et."))
        );
        return start;
    }

    private boolean processCommand(String input) {
        try {
            if (input.equals("quit")) {
                System.out.println("Oyun sonlandırıldı.");
                return false;
            }

            if (input.equals("look")) {
                player.getCurrentRoom().describe();
                return true;
            }

            if (input.startsWith("go ")) {
                String dir = input.substring(3);
                player.move(dir);
                return true;
            }

            if (input.startsWith("take ")) {
                String itemName = input.substring(5);
                player.take(itemName);
                return true;
            }

            if (input.startsWith("use ")) {
                String itemName = input.substring(4);
                player.use(itemName, this);
                return true;
            }

            if (input.startsWith("talk ")) {
                String npcName = input.substring(5);
                player.talk(npcName);
                return true;
            }

            if (input.equals("inv")) {
                player.inventoryText();
                return true;
            }

            if (input.equals("help")) {
                System.out.println("Komutlar: look, go, take, use, talk, inv, help, quit");
                return true;
            }

            System.out.println("Bilinmeyen komut.");
            return true;

        } catch (Exception e) {
            System.out.println("Bir hata oluştu: " + e.getMessage());
            return true;
        }
    }
}

package minimaceraoyunu.model;

import minimaceraoyunu.ana.GameEngine;
import minimaceraoyunu.varliklar.*;
import java.util.*;

public class Player {

    private Room currentRoom;
    private List<Item> inventory = new ArrayList<>();
    private int hp = 20;

    public Player(Room room) {
        this.currentRoom = room;
        room.describe();
    }

    public Room getCurrentRoom() { return currentRoom; }

    public void move(String direction) {
        Room next = currentRoom.getExit(direction);
        if (next == null) {
            System.out.println("Bu yönde çıkış yok.");
            return;
        }
        currentRoom = next;
        currentRoom.describe();
    }

    public void take(String itemId) {
        Item found = currentRoom.findItem(itemId);
        if (found == null) {
            System.out.println("Böyle bir eşya yok.");
            return;
        }
        inventory.add(found);
        currentRoom.getItems().remove(found);
        System.out.println(found.getName() + " alındı.");
    }

    public void use(String itemId, GameEngine ctx) {
        Item found = inventory.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst().orElse(null);

        if (found == null) {
            System.out.println("Bu eşya sende yok.");
            return;
        }

        found.onUse(this, ctx);
    }

    public void talk(String npcName) {
        NPC npc = currentRoom.findNPC(npcName);
        if (npc == null) {
            System.out.println("Böyle bir karakter yok.");
            return;
        }
        npc.talk();
    }

    public void inventoryText() {
        if (inventory.isEmpty()) {
            System.out.println("Envanter boş.");
            return;
        }
        System.out.println("Envanter:");
        for (Item i : inventory)
            System.out.println("- " + i.getName());
    }

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
}

package minimaceraoyunu.model;

import minimaceraoyunu.varliklar.Item;
import minimaceraoyunu.varliklar.NPC;

import java.util.*;

public class Room {

    private String id, name, description;
    private Map<String, Room> exits = new HashMap<>();
    private List<Item> items = new ArrayList<>();
    private List<NPC> npcs = new ArrayList<>();

    public Room(String id, String name, String description) {
        this.id = id; this.name = name; this.description = description;
    }

    public void connect(String direction, Room room) {
        exits.put(direction, room);
    }

    public Room getExit(String direction) {
        return exits.get(direction);
    }

    public List<Item> getItems() { return items; }
    public List<NPC> getNpcs() { return npcs; }

    public Item findItem(String id) {
        return items.stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
    }

    public NPC findNPC(String name) {
        return npcs.stream().filter(n -> n.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void describe() {
        System.out.println("\nBulunduğun yer: " + name);
        System.out.println(description);

        System.out.print("Çıkışlar: ");
        if (exits.isEmpty()) System.out.print("yok");
        else for (String k : exits.keySet()) System.out.print(k + " ");
        System.out.println();

        System.out.print("Eşyalar: ");
        if (items.isEmpty()) System.out.print("yok");
        else items.forEach(i -> System.out.print(i.getId() + " "));
        System.out.println();

        System.out.print("Karakterler: ");
        if (npcs.isEmpty()) System.out.print("yok");
        else npcs.forEach(n -> System.out.print(n.getName() + " "));
        System.out.println();
    }
}

package minimaceraoyunu.varliklar;

import minimaceraoyunu.model.Player;
import minimaceraoyunu.ana.GameEngine;

public abstract class Item {

    protected String id, name, description;

    public Item(String id, String name, String description) {
        this.id = id; this.name = name; this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public abstract void onUse(Player p, GameEngine ctx);
}

package minimaceraoyunu.varliklar;

import minimaceraoyunu.model.Player;
import minimaceraoyunu.ana.GameEngine;

public class KeyItem extends Item {

    public KeyItem(String id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public void onUse(Player p, GameEngine ctx) {
        System.out.println("Kapıyı açtın!");
    }
}
package minimaceraoyunu.varliklar;

import minimaceraoyunu.model.Player;
import minimaceraoyunu.ana.GameEngine;

public class PotionItem extends Item {

    public PotionItem(String id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public void onUse(Player p, GameEngine ctx) {
        p.setHp(p.getHp() + 10);
        System.out.println("Canın arttı! HP = " + p.getHp());
    }
}
package minimaceraoyunu.varliklar;

import minimaceraoyunu.model.Player;
import minimaceraoyunu.ana.GameEngine;

public class WeaponItem extends Item {

    public WeaponItem(String id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public void onUse(Player p, GameEngine ctx) {
        System.out.println("Silahı kuşandın. Saldırı gücün arttı.");
    }
}
package minimaceraoyunu.varliklar;

public class NPC {
    protected String name;

    public NPC(String name) { this.name = name; }

    public String getName() { return name; }

    public void talk() {
        System.out.println(name + " ile konuşulamaz.");
    }
}
package minimaceraoyunu.varliklar;

import minimaceraoyunu.diyalog.*;

public class FriendlyNPC extends NPC {

    private ConversationNode start;

    public FriendlyNPC(String name, ConversationNode start) {
        super(name);
        this.start = start;
    }

    @Override
    public void talk() {
        start.display();
    }
}
package minimaceraoyunu.varliklar;

public class EnemyNPC extends NPC {

    private int attackDamage;

    public EnemyNPC(String name, int attackDamage) {
        super(name);
        this.attackDamage = attackDamage;
    }

    @Override
    public void talk() {
        System.out.println(name + " saldırdı! Sana " + attackDamage + " hasar verdi!");
    }
}
package minimaceraoyunu.diyalog;

import java.util.*;

public class ConversationNode {

    private String text;
    private List<ConversationChoice> choices = new ArrayList<>();

    public ConversationNode(String text) {
        this.text = text;
    }

    public void addChoice(ConversationChoice ch) {
        choices.add(ch);
    }

    public void display() {
        System.out.println(text);

        if (choices.isEmpty()) return;

        for (int i = 0; i < choices.size(); i++)
            System.out.println((i+1) + ") " + choices.get(i).getText());

        Scanner sc = new Scanner(System.in);
        System.out.print("Seçim: ");
        int c = sc.nextInt();
        if (c >= 1 && c <= choices.size()) {
            ConversationNode next = choices.get(c-1).getNext();
            if (next != null) next.display();
        }
    }
}
package minimaceraoyunu.diyalog;

public class ConversationChoice {

    private String text;
    private ConversationNode next;

    public ConversationChoice(String text, ConversationNode next) {
        this.text = text;
        this.next = next;
    }

    public String getText() { return text; }
    public ConversationNode getNext() { return next; }
}
*/
package minimaceraoyunu;

/**
 * MiniMaceraOyunu sınıfı.
 * Oyunun genel sabitlerini ve başlangıç metinlerini tutar.
 */
public class MiniMaceraOyunu {
    // Kilitli oda ID'si
    public static final int LOCKED_ROOM_ID = 3;

    // Komutlar
    public static final String HELP_TEXT = 
        "\n--- Komutlar ---\n" +
        "look    : Bulunduğun odayı tekrar açıklar.\n" +
        "go [yön]: Belirtilen yöne git (örnek: go north).\n" +
        "take [eşya]: Odadaki eşyayı envantere al (örnek: take anahtar).\n" +
        "use [eşya]: Envanterdeki eşyayı kullan (örnek: use iksir).\n" +
        "talk [karakter]: Odadaki karakterle konuş (örnek: talk muhafız).\n" +
        "inv     : Envanterini listeler.\n" +
        "help    : Bu yardım metnini gösterir.\n" +
        "quit    : Oyunu sonlandırır.\n";

    public static final String WELCOME_MESSAGE = 
        "\n================================\n" +
        "=== Metin Tabanlı Mini Macera ===\n" +
        "================================\n" +
        "Amacın son odaya ulaşmak. 'help' yazarak komutları görebilirsin.";
        
    public static final String UNKNOWN_COMMAND = "Bilinmeyen komut. 'help' yazarak komutları görebilirsin.";
    public static final String NO_EXIT = "Bu yönde çıkış yok.";
    public static final String ROOM_LOCKED = "Kapı kilitli görünüyor. Belki bir anahtar işe yarar.";
    public static final String NO_ITEM_OR_NPC = "Böyle bir eşya/karakter bulunmuyor.";
    public static final String NOT_IN_INVENTORY = "Bu eşya sende yok.";

    private MiniMaceraOyunu() {
        // Yardımcı sınıf, örneklenemez
    }
}