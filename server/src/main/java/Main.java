import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static List<User> allUsers = new ArrayList<>();
    private static Map<WsContext, User> contextToUserMap = new HashMap<>();

    private static int userIdCounter = 0;

    public static void main(String[] args) {
        Javalin app = Javalin.create(javalinConfig -> {
            // Modifying the WebSocketServletFactory to set the socket timeout to 120 seconds
            javalinConfig.jetty.modifyWebSocketServletFactory(jettyWebSocketServletFactory ->
                    jettyWebSocketServletFactory.setIdleTimeout(Duration.ofSeconds(120))
            );
        });

        // Start the server on port 5001
        app.start(5001);

        // WebSocket endpoints
        app.ws("/", ws -> {
            ws.onConnect(ctx -> {
                int newUserId = userIdCounter++; // increment for ids
                User newUser = new User(newUserId, ctx);
                allUsers.add(newUser);
                contextToUserMap.put(ctx, newUser);  // Map WebSocket context to User

                // Send welcome message with system senderId
                Message welcomeMsg = new Message();
                welcomeMsg.setContent("Welcome to BeeChat! Your ID is: " + newUserId);
                welcomeMsg.setSenderId(0);  // System ID (0 for system messages)
                welcomeMsg.setRecipientId("");  // Empty means broadcast to all
                newUser.receiveMessage(welcomeMsg);

                System.out.println("New connection: User " + newUserId);
            });

            ws.onClose(ctx -> {
                // Clean up when user disconnects
                allUsers.removeIf(user -> user.getContext() == ctx);
                contextToUserMap.remove(ctx);  // Remove the mapping from context to User
                System.out.println("Closed connection");
            });

            ws.onMessage(ctx -> {
                Message message = ctx.messageAsClass(Message.class);
                String recipientId = message.getRecipientId();
                String content = message.getContent();

                // Retrieve the sender's User object using the WebSocket context
                User senderUser = contextToUserMap.get(ctx);
                if (senderUser == null) {
                    System.err.println("Could not find sender ID for context: " + ctx);
                    return;
                }

                int senderId = senderUser.getId(); // Now we have the correct sender ID

                // Create the response message
                Message responseMessage = new Message();
                responseMessage.setSenderId(senderId);
                responseMessage.setContent(content);
                responseMessage.setRecipientId(recipientId);

                // Global message (recipientId is empty)
                if (recipientId == null || recipientId.isEmpty()) {
                    // Send to all users
                    for (User user : allUsers) {
                        user.receiveMessage(responseMessage);
                    }
                }
                // Targeted message
                else {
                    int targetId = Integer.parseInt(recipientId);
                    // Send to recipient
                    for (User user : allUsers) {
                        if (user.getId() == targetId) {
                            user.receiveMessage(responseMessage);
                            break;
                        }
                    }
                    // Send back to sender if they're not the recipient
                    if (senderId != targetId) {
                        for (User user : allUsers) {
                            if (user.getId() == senderId) {
                                user.receiveMessage(responseMessage);
                                break;
                            }
                        }
                    }
                }
            });

            ws.onError(ctx -> {
                System.err.println("Error occurred: " + ctx.error().getMessage());
                allUsers.removeIf(user -> user.getContext() == ctx);
                contextToUserMap.remove(ctx);  // Remove the mapping on error
            });
        });
    }
}


