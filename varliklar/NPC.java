package minimaceraoyunu.varliklar;

public class NPC {
    protected String name;
    protected String initialDialogue;

    public NPC(String name, String initialDialogue) {
        this.name = name;
        this.initialDialogue = initialDialogue;
    }

    public String getName() {
        return name;
    }

    public String talk() {
        return name + ": " + initialDialogue;
    }
}