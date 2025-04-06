package com.zonezone.backend.gameHandler;

public class PlayerJoinPayloadDTO {

    private String type; // should be "join"
    private String matchID;
    private String userID;

    public PlayerJoinPayloadDTO() {}

    public PlayerJoinPayloadDTO(String type, String matchID, String userID) {
        this.type = type;
        this.matchID = matchID;
        this.userID = userID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMatchID() {
        return matchID;
    }

    public void setMatchID(String matchID) {
        this.matchID = matchID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
