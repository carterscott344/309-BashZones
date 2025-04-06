package com.zonezone.backend.gameHandler.mechanicsHandlers;

public class MovementPayloadDTO {

    private String type; // use "movement" to discriminate
    private Long userID;
    private Double playerXPosition;
    private Double playerYPosition;
    private Integer rotationDegrees;

    // Constructors
    public MovementPayloadDTO() {
    }

    public MovementPayloadDTO(String type, Long userId, Double x, Double y, Integer rotation) {
        this.type = type;
        this.userID = userId;
        this.playerXPosition = x;
        this.playerYPosition = y;
        this.rotationDegrees = rotation;
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Double getPlayerXPosition() {
        return playerXPosition;
    }

    public void setPlayerXPosition(Double x) {
        this.playerXPosition = x;
    }

    public Double getPlayerYPosition() {
        return playerYPosition;
    }

    public void setPlayerYPosition(Double y) {
        this.playerYPosition = y;
    }

    public Integer getRotationDegrees() {
        return rotationDegrees;
    }

    public void setRotationDegrees(Integer degrees) {
        this.rotationDegrees = degrees;

    }
}