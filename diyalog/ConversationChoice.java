package minimaceraoyunu.diyalog;

public class ConversationChoice {
    private final String choiceText;
    private final ConversationNode nextNode;

    public ConversationChoice(String choiceText, ConversationNode nextNode) {
        this.choiceText = choiceText;
        this.nextNode = nextNode;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public ConversationNode getNextNode() {
        return nextNode;
    }
}