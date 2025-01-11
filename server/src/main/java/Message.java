public class Message {
    private String recipientId;
    private String content;
    private Integer senderId;

    // Default constructor required for Jackson deserialization
    public Message() {
    }

    // Constructor for creating response messages
    public Message(Integer senderId, String content) {
        this.senderId = senderId;
        this.content = content;
    }

    // Getters and setters required for Jackson
    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }
}
