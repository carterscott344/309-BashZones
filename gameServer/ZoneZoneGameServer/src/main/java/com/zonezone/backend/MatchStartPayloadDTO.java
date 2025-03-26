package com.zonezone.backend;

import java.util.List;

public class MatchStartPayloadDTO {
    public String matchID;
    public List<String> teamA;
    public List<String> teamB;
    public String gameMode;
}