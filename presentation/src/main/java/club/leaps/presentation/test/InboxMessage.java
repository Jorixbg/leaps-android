package club.leaps.presentation.test;

/**
 * Created by Ivan on 1/19/2018.
 */

public class InboxMessage {

    private InboxUser inboxUser;
    private ChatMessages chatMessages;

    public InboxMessage(InboxUser inboxUser, ChatMessages chatMessages) {
        this.inboxUser = inboxUser;
        this.chatMessages = chatMessages;
    }

    public InboxUser getInboxUser() {
        return inboxUser;
    }

    public void setInboxUser(InboxUser inboxUser) {
        this.inboxUser = inboxUser;
    }

    public ChatMessages getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(ChatMessages chatMessages) {
        this.chatMessages = chatMessages;
    }
}
