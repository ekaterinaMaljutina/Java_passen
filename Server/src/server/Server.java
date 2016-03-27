package server;

import client.Client;
import client.Player;
import client.RoomInfo;
import game.CellState;
import game.GroundVisor;
import game.Label;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server extends Thread {
    private int port = 3124;
    private InetAddress ip = null;
    private ServerSocket ss;
    private String log;

    private Hashtable<UUID, Client> playerHashtable
            = new Hashtable<>();
    private Hashtable<UUID, Room> rooms
            = new Hashtable<>();

    private Hashtable<UUID, GroundVisor>  groundVisorHashtable
            =  new Hashtable<>();

    private gameLister gameLister;

    public void addToLog(String s)
    {
        String str= log + s  + "\n";
        System.out.println(str);
    }

    Server(String log){
        this.log = log;
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            ss = new ServerSocket(port, 0, ip);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        addToLog("server start\n");
    }

    public synchronized   void sendToAll(String s)
    {
        System.out.println("size hashTabl " + playerHashtable.size() );
        for (Map.Entry<UUID, Client> entrySet :
                playerHashtable.entrySet()) {
            UUID key = entrySet.getKey();

            //System.out.println(" user  " + key);
            Client value = entrySet.getValue();
            System.out.println(" user  " + value.getPlayer().getNamePlayer() + "  " + key);
            value.sendMessage(s);

        }
    }

    public synchronized boolean joinRoom(UUID roomId, UUID playerId)  {
        System.out.println("pl count " +  playerHashtable.size());
        System.out.println("pl count " + playerId);
        Client client = playerHashtable.get(playerId);
        if (client!=null) {
            System.out.println(client.getName());
            Player player = playerHashtable.get(playerId).getPlayer();
            if (player == null) {
                System.out.println(" player is null");
            }
            rooms.get(roomId).joinRoom(roomId, new Pair<>(roomId, player));

            if ( ! rooms.get(roomId).isFree()){

                System.out.println("room is FULL");
                sendToAll("OpponentIsReady");
                /*Ground ground = new Ground(5,5);
                ground.Init();
                GroundVisor groundVisor = new GroundVisor(ground);
                groundVisorHashtable.put(roomId,groundVisor );*/
            }
            else {
                sendToAll("Player1");
            }

            System.out.println("Player has been joined to the room");
            return true;

        }else {
            System.out.println(" client is null "  );
            return false;
        }
    }

    public synchronized UUID createRoom(String roomName) {

        Room room = new Room(roomName);
        UUID roomId = UUID.randomUUID();
        rooms.put(roomId, room);
        //Player player = playerHashtable.get(playerId).getPlayer();
        //room.joinRoom(roomId, new Pair<>(playerId, player));
        System.out.println("New room has been created");
        System.out.println( "roomInfo size  in server = " + rooms.size());
        return roomId;
    }

    public synchronized List<RoomInfo> gerRoom(){
        List<RoomInfo> roomsInfo = new ArrayList<>() ;
        System.out.println(" мы на сервере и хотим отослать комнаты");
        System.out.println( " размер комнат сейчас  = " +rooms.size());
        for (int i=0; i< rooms.keySet().size(); i++){
            UUID roomId = (UUID) rooms.keySet().toArray()[i];
            System.out.println( roomId.toString() );
            Room room = (Room) rooms.values().toArray()[i];
            System.out.println( room.getRoomName() );
            Player player_1 = null;
            Player player_2 = null;
            if (room.getplayer_1_info() != null){
                System.out.println( " yes player_1" );
                player_1 = new Player(room.getplayer_1_info().getRight().getNamePlayer());//,room.getplayer_1_info().getLeft() );
                player_1.setPlayer(CellState.Player_1);
                player_1.setLabel(Label.X);
                System.out.println( player_1.getNamePlayer() );
            }

            if (room.getplayer_2_info() != null){
                System.out.println( " yes player_2" );
                player_2 = new Player(room.getplayer_2_info().getRight().getNamePlayer());//,room.getplayer_2_info().getLeft() );
                player_2.setPlayer(CellState.Player_2);
                player_2.setLabel(Label.O);
            }

            RoomInfo roomInfo_to_add = new RoomInfo(room.getRoomName(), roomId, player_1, player_2);
            System.out.println( roomInfo_to_add.getName() );
            roomsInfo.add(roomInfo_to_add);
            System.out.println( roomsInfo.size());
        }
        System.out.println("Room list has been sent");
        System.out.println( " we send with roomInfo size = " + roomsInfo.size());
        return roomsInfo;
    }

    private synchronized void findPlayerInRoom(UUID id){
        System.out.println(" want delete player with id =   " + id);
        System.out.println("  удаляем игрока из комнаты  " +rooms.size() );
        for (int i=0; i< rooms.keySet().size(); i++) {
            UUID key = (UUID) rooms.keySet().toArray()[i];
            Room room   = rooms.get(key);
            rooms.remove(key);
            System.out.println(room.getRoomName());
            room.leaveRoom(id);
            room.roomInfo();
        }
    }

    public void deleteClient(UUID id){
        System.out.println("  игрок хочет покинуть игру, удаляем его из таблицы ");
        System.out.println(" было  " + playerHashtable.size());
        playerHashtable.remove(id);
        System.out.println(" стало " + playerHashtable.size());
        findPlayerInRoom(id);

    }

    @Override
    public void run(){
        while(true){
            try {
                Socket cs = ss.accept();
                Client client = new Client(this,cs ,UUID.randomUUID());
                playerHashtable.put( client.getUUID(), client);

                System.out.println("size tablPlayer = " + playerHashtable.size());
                System.out.println(client.getUUID());


                /*if (playerHashtable.size() / 2 ==0){
                    System.out.println("четное количество игроков");
                    // можно добавить предыдущего как соперника
                }*/
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public class gameLister extends Thread{
        private BufferedReader input;
        public PrintWriter output;

        public  gameLister(Socket socket) throws IOException{
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(),true);
        }

        public void run() {
            /*try {
                // The thread is only started after everyone connects.
                output.println("Message All players connected");
                if (player == CellState.Player_1)
                    output.println("YOUR_TURN");

                // Repeatedly get commands from the client and process them.
                while (!checkGroundFill()) {
                    String command = input.readLine();
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(command);
                    JSONObject jsonObject = (JSONObject) obj;
                    int x = ((Long) jsonObject.get("x")).intValue();
                    int y = ((Long) jsonObject.get("y")).intValue();
                    int edge = ((Long) jsonObject.get("edge")).intValue();
                    if (legalMove(x, y, edge, this)) {
                        output.println("Successful_move");
                        opponent.sendEdge(x, y, edge);
                        if (!this.equals(curPlayer)) { // Если последний ход отметил клетку
                            opponent.output.println("YOUR_TURN");
                        }
                        else {
                            output.println("YOUR_TURN");
                        }
                    }
                    else {
                        output.println("Invalid_move");
                    }
                }
            } catch (IOException e) {
                System.out.println("Player died: " + e);
            } catch(ParseException e){
                e.printStackTrace();
            } finally {
                //Вывод победителя
                CellState win = ground.hasWinner();
                if (win != null) {
                    output.println("Winner is " + win);
                    opponent.output.println("Winner is " + win);
                }
                else {
                    output.println("Tie");
                    opponent.output.println("Tie");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }*/
        }
    }

}
