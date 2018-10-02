package club.leaps.presentation.test;

/**
 * Created by Ivan on 11/24/2017.
 */
public class ChatMessages {

    private String message;
    private boolean seenUser1;
    private boolean seenUser2;
    private long time;
    private String type;
    private String sender;
    private String room;


    public ChatMessages(){

    }

    public ChatMessages(String message, long time, String type,String sender,boolean seenUser1,boolean seenUser2) {
        this.message = message;
        this.time = time;
        this.type = type;
        this.sender = sender;
        this.seenUser1 = seenUser1;
        this.seenUser2 = seenUser2;
    }

    public boolean isSeenUser1() {
        return seenUser1;
    }

    public void setSeenUser1(boolean seenUser1) {
        this.seenUser1 = seenUser1;
    }

    public boolean isSeenUser2() {
        return seenUser2;
    }

    public void setSeenUser2(boolean seenUser2) {
        this.seenUser2 = seenUser2;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return sender;
    }

    public void setFrom(String from) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}

