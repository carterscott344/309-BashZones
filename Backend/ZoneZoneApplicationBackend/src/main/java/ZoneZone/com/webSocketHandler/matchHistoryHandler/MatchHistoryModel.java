package ZoneZone.com.webSocketHandler.matchHistoryHandler;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class MatchHistoryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String matchID;
    private String winningTeam;
    private int winningScore;
    private String losingTeam;
    private int losingScore;
    private int totalKills;
    private int totalDeaths;

    public MatchHistoryModel() {
        // No-args constructor required by JPA
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatchID() {
        return matchID;
    }

    public void setMatchID(String matchID) {
        this.matchID = matchID;
    }

    public String getWinningTeam() {
        return winningTeam;
    }

    public void setWinningTeam(String winningTeam) {
        this.winningTeam = winningTeam;
    }

    public int getWinningScore() {
        return winningScore;
    }

    public void setWinningScore(int winningScore) {
        this.winningScore = winningScore;
    }

    public String getLosingTeam() {
        return losingTeam;
    }

    public void setLosingTeam(String losingTeam) {
        this.losingTeam = losingTeam;
    }

    public int getLosingScore() {
        return losingScore;
    }

    public void setLosingScore(int losingScore) {
        this.losingScore = losingScore;
    }

    public int getTotalKills() {
        return totalKills;
    }

    public void setTotalKills(int totalKills) {
        this.totalKills = totalKills;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(int totalDeaths) {
        this.totalDeaths = totalDeaths;
    }
}
