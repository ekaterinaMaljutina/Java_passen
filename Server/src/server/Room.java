package server;

import client.Player;
import client.RoomInfo;
import game.CellState;
import game.Label;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by kate on 26.03.16.
 */
public class Room {
    Logger logger = Logger.getLogger(Room.class.getName());

    private String name;

    //private Board board;

    private Pair<UUID, Player> player_1_info = null;
    private Pair<UUID, Player> player_2_info = null;

    public Room(String name) {
        this.name = name;
        //this.board = new Board(boardSize);
    }


    public synchronized void joinRoom(UUID roomId, Pair<UUID, Player> playerInfo) {
        if (player_1_info == null) {
            player_1_info = playerInfo;
        } else if (player_2_info == null) {
            player_2_info = playerInfo;
        } 
        if (player_1_info != null && player_2_info != null) {

            player_1_info.getRight().setOpponent(player_2_info.getRight());
            player_2_info.getRight().setOpponent(player_1_info.getRight());
            RoomInfo roomInfo = getRoomInfo(roomId);

            System.out.println("player : " + player_1_info.getRight().getNamePlayer()
            + "  with opponent : " + player_1_info.getRight().getOpponent().getNamePlayer());

            System.out.println("player : " + player_2_info.getRight().getNamePlayer()
                    + "  with opponent : " + player_2_info.getRight().getOpponent().getNamePlayer());


            // тут еще поле надо добавить
            player_1_info.getRight().setLabel(Label.X);
            player_2_info.getRight().setLabel(Label.O);

            player_1_info.getRight().setPlayer(CellState.Player_1);
            player_2_info.getRight().setPlayer(CellState.Player_2);

            // и запустить играков



            logger.log(Level.INFO, "Game started");
        }
    }


    public synchronized void leaveRoom(UUID playerId) {

        if (player_1_info != null) {
            if (player_1_info.getLeft().equals(playerId)) {

                if (player_2_info != null) {
                    player_1_info = player_2_info;
                    player_2_info = null;
                } else{
                    player_1_info = null;
                }

                System.out.println("delete player_1");

            }
        } else if (player_2_info != null) {
            if (player_2_info.getLeft().equals(playerId)) {
                player_2_info = null;
                System.out.println("delete player_2");

            }
        }
        /*if (player_1_info != null) {
            //player_1_info.getRight().setColor(Color.NONE);
            //player_1_info.getRight().gameOver();
        }
        if (player_2_info != null) {
            //player_2_info.getRight().setColor(Color.NONE);
            //player_2_info.getRight().gameOver();
        }*/
    }
    public synchronized void roomInfo(){
        String str = " player 1 is ";
        if (player_1_info!=null) {
            if (player_1_info.getRight() != null) {
                str+=player_1_info.getRight().getNamePlayer();
                str+="\n";
                str+=player_1_info.getLeft();
            }
        }else{
            str+="null";
        }
        str+="\n";
        str += "  player 2 is ";
        if (player_2_info!=null) {
            if (player_2_info.getRight() != null) {
                str+=player_2_info.getRight().getNamePlayer();
                str+="\n";
                str+=player_1_info.getLeft();
            }
        }else{
            str+="null";
        }
        System.out.println(str);

    }

    /*public synchronized void takeEdge(BoardChange boardChange)  {
        if (player_1_info != null && player_2_info != null) {
            try {
                boolean myStep = board.apply(boardChange);
                player_1_info.getRight().updateBoard(boardChange);
                player_2_info.getRight().updateBoard(boardChange);
                if (boardChange.getReservedBy() == Edge.WHO.BLUE) {
                    player_1_info.getRight().isYourTurn(myStep);
                    player_2_info.getRight().isYourTurn(!myStep);
                } else {
                    player_1_info.getRight().isYourTurn(!myStep);
                    player_2_info.getRight().isYourTurn(myStep);
                }

                boolean isFinish = board.isFinish();
                if (isFinish) {
                    player_1_info.getRight().gameOver();
                    player_2_info.getRight().gameOver();
                }
            } catch (IllegalStateException e) {

            }
        } else {
            throw new ServerRemoteException(ServerRemoteException.Code.GAME_NOT_STARTED);
        }
    }*/



    public synchronized boolean isFree() {
        if (player_1_info == null || player_2_info == null) {
            return true;
        } else {
            return false;
        }
    }

    private RoomInfo getRoomInfo(UUID roomId) {
        Player bluePlayer = null;
        Player redPlayer = null;
        if (player_1_info != null) {
            bluePlayer = new Player(player_1_info.getRight().getNamePlayer());//,player_1_info.getLeft());
        }
        if (player_2_info != null) {
            redPlayer = new Player(player_2_info.getRight().getNamePlayer());//, player_2_info.getLeft());
        }
        RoomInfo roomInfo = new RoomInfo(name, roomId, bluePlayer, redPlayer);
        return roomInfo;
    }

    public String getRoomName() {
        return name;
    }

   /* public Board getBoard() {
        return board;
    }*/

    public Pair<UUID, Player> getplayer_1_info() {
        return player_1_info;
    }

    public Pair<UUID, Player> getplayer_2_info() {
        return player_2_info;
    }


}
