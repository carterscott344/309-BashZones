package ZoneZone.com.webSocketHandler;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/startMatch")
public class GameSocketServer {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("🔌 Connection opened: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("📩 Match payload received on Game Server:");
        System.out.println(message);
    }
}
