
package minimaceraoyunu.diyalog;

import java.util.List;

public class ConversationNode {
    private final String text;
    private final List<ConversationChoice> choices;

    public ConversationNode(String text, List<ConversationChoice> choices) {
        this.text = text;
        this.choices = choices;
    }

    public String getText() {
        return text;
    }

    public List<ConversationChoice> getChoices() {
        return choices;
    }
    
    public boolean isEndNode() {
        return choices.isEmpty();
    }
}