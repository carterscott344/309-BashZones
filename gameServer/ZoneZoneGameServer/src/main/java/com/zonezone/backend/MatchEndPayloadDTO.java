package com.zonezone.backend;

import java.util.List;

public class MatchEndPayloadDTO {
    public String matchID;
    public List<Long> players; // all 4 userIDs involved in the match
}
