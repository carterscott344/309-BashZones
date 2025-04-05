package com.zonezone.backend.gameHandler;

import java.util.List;

public class MatchEndPayloadDTO {
    public String matchID;
    public List<Long> players; // all 4 userIDs involved in the match
}
