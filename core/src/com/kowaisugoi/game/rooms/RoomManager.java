package com.kowaisugoi.game.rooms;

import java.util.HashMap;
import java.util.Map;

/**
 * Room mappings are final and global.
 */
public final class RoomManager {
    private HashMap<RoomId, Room> _roomMap = new HashMap<RoomId, Room>();

    public RoomManager() {
        _roomMap.put(RoomId.MAIN_HALL, new RoomMainHall());
        _roomMap.put(RoomId.FRONT_DOOR_INTERIOR, new RoomFrontDoorInterior());
        _roomMap.put(RoomId.FRONTYARD, new RoomFrontYard());
        _roomMap.put(RoomId.CAR, new RoomCar());
    }

    public Map<RoomId, Room> getRoomMap() {
        return _roomMap;
    }

    // Clean up all rooms
    public void cleanUp() {
        for (Room room : _roomMap.values()) {
            room.dispose();
        }
    }
}