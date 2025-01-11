import io.javalin.Javalin;

import java.time.Duration;

public class Main {

    public static void main(String[] args) {
        Javalin app = Javalin.create( javalinConfig -> {
            javalinConfig.jetty.modifyWebSocketServletFactory(jettyWebSocketServletFactory ->
                jettyWebSocketServletFactory.setIdleTimeout(Duration.ofSeconds(120)));
        });

        app.ws("/", wsConfig -> {
            wsConfig.onConnect((wsConnectContext -> {
                System.out.println("Connected: " + wsConnectContext.sessionId());
            }));
        });



        app.start(5001);
    }
}
