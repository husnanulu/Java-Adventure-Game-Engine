package minimaceraoyunu;
import minimaceraoyunu.diyalog.ConversationNode;
import minimaceraoyunu.model.Player;
import minimaceraoyunu.model.Room;
import minimaceraoyunu.varliklar.*;
import minimaceraoyunu.diyalog.ConversationChoice;

import java.util.*;
import java.util.stream.Collectors;

public class GameEngine {
    private final Player player;
    private final Map<Integer, Room> map;
    private final Scanner scanner;
    private boolean isRunning;
    private ConversationNode currentDialogueNode; // Diyalog durumunu tutar

    public GameEngine() {
        this.player = new Player(100, 10); // Başlangıç HP: 100, Güç: 10
        this.map = new HashMap<>();
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
    }
    private void initializeGame() {
        Room salon = new Room(1, "Salon", "Evin merkezi olan geniş bir salon. Dört yöne de kapılar açılıyor. Burası başlangıç noktan.", false);
        Room mutfak = new Room(2, "Mutfak", "Mutfak tezgahının üzerinde bir kılıç parlıyor.", false);
        Room yatakOdasi = new Room(3, "Yatak Odası", "Dağınık bir yatak ve eski bir komodin var. Bir şey saklanmış olabilir. Burası tehlikeli görünüyor.", false);
        Room banyo = new Room(4, "Banyo", "Nemli ve loş bir banyo. Köşede küçük bir kutu duruyor.", false);
        Room cikisKoridoru = new Room(5, "Çıkış Koridoru", "Büyük, demir bir kapının olduğu dar bir koridor. Kapıyı geçersen dışarı çıkacaksın.", true);

        map.put(salon.getId(), salon);
        map.put(mutfak.getId(), mutfak);
        map.put(yatakOdasi.getId(), yatakOdasi);
        map.put(banyo.getId(), banyo);
        map.put(cikisKoridoru.getId(), cikisKoridoru);
        
        salon.connect("east", mutfak);
        salon.connect("south", yatakOdasi);
        salon.connect("west", banyo);
        salon.connect("north", cikisKoridoru);
  
        mutfak.connect("west", salon);


        yatakOdasi.connect("north", salon);
        yatakOdasi.connect("east", banyo);

        banyo.connect("west", salon); 
        banyo.connect("east", salon); 
        banyo.connect("south", yatakOdasi); 

        yatakOdasi.connect("west", banyo);

        cikisKoridoru.connect("south", salon);

        Item kilic = new WeaponItem(12, "kilic", "Eski, ama keskin bir kılıç. Saldırı gücünü 15 artırır.", 15);
        mutfak.getItems().add(kilic);

        NPC goblin = new EnemyNPC("Goblin", "Hırıltılı sesler çıkarıyor.", 40, 15);
        yatakOdasi.getNpcs().add(goblin);

        Item buyukIksir = new PotionItem(13, "buyuk_iksir", "Sana 30 HP verecek büyük bir iksir.", 30);

        yatakOdasi.getItems().add(buyukIksir); 

        Item kirmiziAnahtar = new KeyItem(11, "kirmizi_anahtar", "Paslı, kırmızı bir anahtar. Son çıkış kapısının kilidini açar.", 5); // Çıkış Koridoru (ID 5)'i açar
        banyo.getItems().add(kirmiziAnahtar);

        ConversationNode node3 = new ConversationNode("Unutma, anahtarı bulup sonra Goblin'i yenmek sana büyük avantaj sağlar. Haydi yola çık!", Collections.emptyList());
        ConversationNode node2 = new ConversationNode("Anahtarın Banyo'da (WEST) saklı olduğunu duydum. Mutfağa (EAST) gidip kılıcı alırsan Yatak Odası'ndaki (SOUTH) düşmanı daha kolay yenersin!", 
            Arrays.asList(new ConversationChoice("Teşekkürler Muhafız.", node3)));
        ConversationNode node1 = new ConversationNode("Selam! Ben Muhafız. Amacın Kuzeydeki kilitli kapıdan geçmek.", 
            Arrays.asList(new ConversationChoice("Kilitli kapıyı nasıl açabilirim?", node2), new ConversationChoice("Şimdilik bir şeye ihtiyacım yok.", node3)));

        NPC muhafiz = new FriendlyNPC("Muhafız", "Selam! Ben Muhafız. Hazır olduğunda benimle konuş.", node1);
        salon.getNpcs().add(muhafiz);

        player.setCurrentRoom(salon);
    }

    public void run() {
        initializeGame();
        System.out.println(MiniMaceraOyunu.WELCOME_MESSAGE);
        player.getCurrentRoom().describe();

        while (isRunning) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (currentDialogueNode != null) {
                handleDialogueInput(input);
            } else {
                processCommand(input);
            }

            if (player.getHp() <= 0) {
                System.out.println("\n*** Can puanın bitti! Oyunu kaybettin. ***");
                isRunning = false;
            } else if (player.getCurrentRoom().getId() == 5 && !player.getCurrentRoom().isLocked()) {
                System.out.println("\n*** Tebrikler! Çıkış Koridoru'ndan geçtin ve oyunu kazandın! ***");
                isRunning = false;
            }
        }
        System.out.println("\n--- OYUN SONA ERDİ ---");
        System.out.println("Kalan HP: " + player.getHp());
        System.out.println("Saldırı Gücü: " + player.getAttackPower());
        System.out.println(player.inventoryText());
    }

    private void processCommand(String input) {
        if (input.isEmpty()) return;

        String[] parts = input.split(" ", 2);
        String command = parts[0];
        String argument = parts.length > 1 ? parts[1] : "";

        try {
            switch (command) {
                case "look":
                    player.getCurrentRoom().describe();
                    break;
                case "go":
                    handleGo(argument);
                    break;
                case "take":
                    handleTake(argument);
                    break;
                case "use":
                    handleUse(argument);
                    break;
                case "talk":
                    handleTalk(argument);
                    break;
                case "say":
                    System.out.println("Kendi kendine fısıldıyorsun: " + argument);
                    break;
                case "inv":
                    System.out.println(player.inventoryText());
                    break;
                case "help":
                    System.out.println(MiniMaceraOyunu.HELP_TEXT);
                    break;
                case "quit":
                    isRunning = false;
                    break;
                case "attack": // Bonus komut: Savaşmak için
                    handleAttack(argument);
                    break;
                default:
                    System.out.println(MiniMaceraOyunu.UNKNOWN_COMMAND);
                    break;
            }
        } catch (Exception e) {
            System.err.println("\n*** Bir hata oluştu! Program sonlanmadı, ancak bu durum geliştiriciye bildirilmeli: " + e.getMessage() + " ***");
        }
    }

    private void handleGo(String direction) {
        Room current = player.getCurrentRoom();
        Room nextRoom = current.getExit(direction);

        if (nextRoom == null) {
            System.out.println(MiniMaceraOyunu.NO_EXIT);
            return;
        }

        if (nextRoom.isLocked()) {
            System.out.println(MiniMaceraOyunu.ROOM_LOCKED);
            return;
        }

        player.setCurrentRoom(nextRoom);
        nextRoom.describe();
    }

    private void handleTake(String itemName) {
        Room current = player.getCurrentRoom();
        Item itemToTake = current.getItems().stream()
                .filter(i -> i.getName().equalsIgnoreCase(itemName))
                .findFirst()
                .orElse(null);

        if (itemToTake != null) {
            current.getItems().remove(itemToTake);
            player.getInventory().add(itemToTake);
            System.out.println("\n*** " + itemToTake.getName() + " adlı eşyayı aldın. ***");
        } else {
            System.out.println(MiniMaceraOyunu.NO_ITEM_OR_NPC);
        }
    }

    private void handleUse(String itemName) {
        Item itemToUse = player.getInventory().stream()
                .filter(i -> i.getName().equalsIgnoreCase(itemName))
                .findFirst()
                .orElse(null);

        if (itemToUse != null) {
            itemToUse.onUse(player, this); 
        } else {
            System.out.println(MiniMaceraOyunu.NOT_IN_INVENTORY);
        }
    }

    private void handleTalk(String npcName) {
        NPC npcToTalk = player.getCurrentRoom().getNpcs().stream()
                .filter(n -> n.getName().equalsIgnoreCase(npcName))
                .findFirst()
                .orElse(null);

        if (npcToTalk != null) {
            if (npcToTalk instanceof FriendlyNPC) {
                FriendlyNPC friendlyNpc = (FriendlyNPC) npcToTalk;
                currentDialogueNode = friendlyNpc.getConversation();
                displayDialogue();
            } else if (npcToTalk instanceof EnemyNPC) {
                System.out.println(npcToTalk.getName() + " sana konuşmak yerine saldırmak istiyor. 'attack " + npcToTalk.getName().toLowerCase() + "' yazmalısın!");
            } else {
                System.out.println(npcToTalk.talk());
            }
        } else {
            System.out.println(MiniMaceraOyunu.NO_ITEM_OR_NPC);
        }
    }

    private void handleDialogueInput(String input) {
        try {
            int choiceIndex = Integer.parseInt(input) - 1;
            List<ConversationChoice> choices = currentDialogueNode.getChoices();

            if (choiceIndex >= 0 && choiceIndex < choices.size()) {
                currentDialogueNode = choices.get(choiceIndex).getNextNode();
                displayDialogue();
            } else {
                System.out.println("Geçersiz seçim. Lütfen seçenek numarasını girin.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Geçersiz giriş. Lütfen geçerli bir sayı girin.");
        }
    }

    private void displayDialogue() {
        if (currentDialogueNode == null) return;
        
        System.out.println("\n*** KONUŞMA BAŞLANGICI ***");
        System.out.println("Muhafız: " + currentDialogueNode.getText());

        if (currentDialogueNode.isEndNode()) {
            System.out.println("Konuşma sona erdi.");
            currentDialogueNode = null;
        } else {
            System.out.println("\nSeçenekler:");
            for (int i = 0; i < currentDialogueNode.getChoices().size(); i++) {
                System.out.println((i + 1) + ". " + currentDialogueNode.getChoices().get(i).getChoiceText());
            }
            System.out.print("Seçiminiz: ");
        }
    }

    private void handleAttack(String npcName) {
        NPC npcToAttack = player.getCurrentRoom().getNpcs().stream()
                .filter(n -> n.getName().equalsIgnoreCase(npcName))
                .findFirst()
                .orElse(null);

        if (!(npcToAttack instanceof EnemyNPC)) {
            System.out.println(MiniMaceraOyunu.NO_ITEM_OR_NPC);
            return;
        }
        
        EnemyNPC enemy = (EnemyNPC) npcToAttack;

        int playerDamage = player.getAttackPower();
        System.out.println("\n*** Saldırıyorsun! " + playerDamage + " hasar verdin. ***");
        String enemyStatus = enemy.takeDamage(playerDamage);
        System.out.println(enemyStatus);
        
        if (enemy.getHp() <= 0) {
            player.getCurrentRoom().getNpcs().remove(enemy);
            System.out.println("*** " + enemy.getName() + " yenildi! Artık oda güvende. ***");

            Room current = player.getCurrentRoom();
            Item droppedItem = current.getItems().stream()
                .filter(i -> i.getName().equalsIgnoreCase("buyuk_iksir"))
                .findFirst()
                .orElse(null);
            
            if (droppedItem != null) {
                current.getItems().remove(droppedItem);
                player.getInventory().add(droppedItem);
                System.out.println("*** Ganimet: " + droppedItem.getName() + " adlı eşyayı odadan aldın. ***");
            }

            return;
        }

        String playerStatus = enemy.attack(player);
        System.out.println(playerStatus);
    }

    public boolean unlockRoom(int roomId) {
        Room room = map.get(roomId);
        if (room != null && room.isLocked()) {
            room.setLocked(false);
            return true;
        }
        return false;
    }
}