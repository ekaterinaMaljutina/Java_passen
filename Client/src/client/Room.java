package client;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by kate on 26.03.16.
 */
public class Room implements Serializable {
    private String name;
    private UUID id;

    private Player player_1;
    private Player player_2;

    public Room(RoomInfo roomInfo){
        name = roomInfo.getName();
        player_1 = roomInfo.getplayer_1();
        player_2 = roomInfo.getplayer_2();
        id = roomInfo.getId();
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public Player getplayer_1() {
        return player_1;
    }

    public Player getplayer_2() {
        return player_2;
    }

    public void unbindPlayer(Player player) {
        if (player_1 == player) {
            player_1 = null;
        } else if (player_2 == player) {
            player_2 = null;
        }
    }

    public void setplayer_1(Player player_1) {
        this.player_1 = player_1;
    }

    public void setplayer_2(Player player_2) {
        this.player_2 = player_2;
    }
    public boolean isFree() {
        return player_1 == null || player_2 == null;
    }
}

