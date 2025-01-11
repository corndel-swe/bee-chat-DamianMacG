import io.javalin.websocket.WsContext;

public class User {
    private int id;
    private WsContext context;

    public User(int id, WsContext context) {
        this.id = id;
        this.context = context;
    }

    public int getId() {
        return id;
    }

    public WsContext getContext() {
        return context;
    }

    public void receiveMessage(Message message) {
        context.send(message);
    }
}