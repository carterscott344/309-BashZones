package ZoneZone.com.webSocketHandler;

import com.google.gson.Gson;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import java.net.URI;

public class GameServerWebSocketClient {

    private static final String SERVER_URI = "ws://localhost:8025/ws/startMatch";
    private static final Gson gson = new Gson();

    public static void sendMatchPayload(WebSocketMessageDTO payload) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = new URI(SERVER_URI);
            Session session = container.connectToServer(SimpleClientEndpoint.class, uri);

            String json = gson.toJson(payload);
            session.getBasicRemote().sendText(json);
            session.close();
        }
        catch (Exception e) {
            System.err.println("Failed to send match payload to game server: " + e.getMessage());
        }
    }
}
