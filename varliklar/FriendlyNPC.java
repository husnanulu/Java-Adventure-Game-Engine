package minimaceraoyunu.varliklar;

import minimaceraoyunu.diyalog.ConversationNode;

public class FriendlyNPC extends NPC {
    private ConversationNode conversation;

    public FriendlyNPC(String name, String initialDialogue, ConversationNode conversation) {
        super(name, initialDialogue);
        this.conversation = conversation;
    }

    public ConversationNode getConversation() {
        return conversation;
    }
}