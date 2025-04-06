package ZoneZone.com.queueHandler;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class lobbyQueue {

    private final LinkedList<String> queue = new LinkedList<>();

    public synchronized void add(String userID) {
        if (!queue.contains(userID)) {
            queue.add(userID);
        }
    }

    public synchronized void remove(String userID) {
        queue.remove(userID);
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized List<String> popFirstFour() {
        List<String> players = new LinkedList<>();
        for (int i = 0; i < 4 && !queue.isEmpty(); i++) {
            players.add(queue.poll());
        }
        return players;
    }
}
