package ZoneZone.com.webSocketHandler;

import ZoneZone.com.accountHandler.AccountModel;
import ZoneZone.com.accountHandler.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.List;

@Service
public class GameServerHealthCheckService {

    private static final String TEST_URI = "ws://localhost/ws/connectToServer";

    @Autowired
    private AccountRepository accountRepository;

    @Scheduled(fixedRate = 10000) // every 10 seconds
    public void checkGameServerStatus() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            Session session = container.connectToServer(DummyEndpoint.class, new URI(TEST_URI));
            session.close(); // Success means server is alive
        }
        catch (Exception connectionError) {
            System.err.println("❌ Game server unreachable. Resetting active players...");

            List<AccountModel> players = accountRepository.findAll();
            for (AccountModel user : players) {
                if (Boolean.TRUE.equals(user.getIsPlaying())) {
                    user.setIsPlaying(false);
                    accountRepository.save(user);
                    System.out.println("🧹 Reset isPlaying for user: " + user.getAccountUsername());
                }
            }
        }
    }
}
