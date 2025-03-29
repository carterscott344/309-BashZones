package com.zonezone.backend.gameHandler;

import java.util.List;

public class MatchStartPayloadDTO {
    public String matchID;
    public List<String> teamA;
    public List<String> teamB;
    public String gameMode;
}