package client;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by kate on 26.03.16.
 */
public class RoomInfo implements Serializable {
    private String name;
    private UUID id;
    private Player player_1;
    private Player player_2;

    public RoomInfo(String name, UUID id, Player player_1, Player player_2){
        this.name = name;
        this.id = id;
        this.player_1 = player_1;
        this.player_2 = player_2;
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
}