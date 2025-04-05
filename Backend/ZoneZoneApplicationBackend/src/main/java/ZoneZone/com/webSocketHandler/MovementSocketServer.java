package ZoneZone.com.webSocketHandler;

import java.io.IOException;

@ServerEndpoint("/playerMovement/{accountID}")
@Component)
public class MovementSocketServer {
	@OnOpen
	public voic onOpen(Session session) throws IOException {
		
	}
	
	@OnMessage
	public void onMessage(Session session, Message message) throws IOException {
		
	}
	
	@OnClose
	public void onError(Session session) {
		
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		
	}
}