package ZoneZone.com.webSocketHandler;

import org.springframework.beans.factory.annotation.Autowired;
import ZoneZone.com.accountHandler.AccountRepository;
import javax.websocket.OnMessage;
//import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ServerEndpoint("/playerMovement/{accountID}")
@Component)
public class MovementSocketServer {
	@Autowired
    private AccountRepository accountRepository;
	
	
	private Session session;
    private static Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();
	
//	@OnOpen
//	public voic onOpen(Session session) throws IOException {
//		
//	}
	
	@OnMessage
	public void onMessage(Session session, String message) throws IOException {
		this.session = session;
		
		
		/*
		receive:
		"ClientPlayerInformation",playerid,username,x,y
		
		update stored info
		
		send back:
		"ServerUserInfo",playerid,username,x,y,playerid,username,x,y,playerid,username,x,y,playerid,username,x,y
		 */
		String[] info = message.split(",");
		if (info[0].equals("ClientPlayerInformation")) {
			ResponseEntity<String> movingPlayer = accountRepository.findById(accountID).map(account -> {
	            if (!Boolean.TRUE.equals(account.getIsInQueue())) {
	                return ResponseEntity.badRequest().body("❌ User " + userID + " is not currently in queue.");
	            }
			}
		}
	}
	
//	@OnClose
//	public void onClose(Session session) {
//		
//	}
	
//	@OnError
//	public void onError(Session session, Throwable throwable) {
//		
//	}
}