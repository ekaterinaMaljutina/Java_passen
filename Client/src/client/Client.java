package client;

import server.Server;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client extends Thread{
    private Server st;
    private Socket cs;
    private UUID uuid;

    private DataInputStream dis;
    private DataOutputStream dos;
    private Player player;


    public  synchronized UUID getUUID()
    {
        return uuid;
    }

    public Client(Server server, Socket socket, UUID uuid) throws IOException {
        this.st = server;
        this.cs = socket;
        this.uuid = uuid;
        // тут еще можно создать потоки для чтения и записи
        dis = new DataInputStream(cs.getInputStream());
        dos = new DataOutputStream(cs.getOutputStream());
        start(); // запускием поток для клиета
    }
    public synchronized Player getPlayer(){return player;}

    private synchronized void AddPlayer() throws IOException{
        // значит к нам добавился игрок и надо постать его сервер
        try {
            while (true) {
                ObjectInputStream objectInputStream = new ObjectInputStream(cs.getInputStream());
                player = (Player) objectInputStream.readObject();

                //uuid = player.getIdPlyer();
                if (player != null) {

                    System.out.println("значит к нам добавился игрок и надо постать его сервер " + uuid);

                    System.out.println(player.getNamePlayer());

                    return;
                }
                else {
                    System.out.println("obj  is null");
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.dis = new DataInputStream(cs.getInputStream());
    }
    private synchronized void addRoom(){
        while (true){
            try {

                String nameRoom =  dis.readUTF();
                System.out.println(" want create room ");
                st.createRoom(nameRoom);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                break;
            }

        }
    }

    public synchronized void getRoomInfo() throws IOException {
        List<RoomInfo> roomInfos=  st.gerRoom();
        System.out.println("  roomInfo size  = " + roomInfos.size());
        dos = new DataOutputStream(cs.getOutputStream());
        dos.writeUTF("SizeRoomInfo");
        dos = new DataOutputStream(cs.getOutputStream());
        dos.writeInt(roomInfos.size());

       for (int i=0;i<roomInfos.size();i++){
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(cs.getOutputStream());
            System.out.println( " отправляем комнату " + roomInfos.get(i).getName() );
            objectOutputStream.writeObject(roomInfos.get(i));
        }

    }

    public synchronized void joinRoom() throws IOException, ClassNotFoundException {
        System.out.println(" want to join in the room");
        while (true) {
            ObjectInputStream objectInputStream = new ObjectInputStream(cs.getInputStream());
            RoomInfo roomInfo = (RoomInfo) objectInputStream.readObject();
            if (roomInfo!=null) {
                System.out.println("хотим присоедениться к комноте " + roomInfo.getName() + "    " +  roomInfo.getId());
                System.out.println("хочет присоедениться игрок  " + player.getNamePlayer() + "  " + uuid);
                if ( st.joinRoom(roomInfo.getId(), uuid)){
                    st.sendToAll("ConnectionPlayer");
                }

                return;
            }
        }
    }

    public synchronized void deletePlayer(){
        st.deleteClient(uuid);
    }

    @Override
    public synchronized void run() {
        // отправили сообщение на сервер
        st.addToLog(" connection user with ID ");
        //st.sendToAll(id.toString());
        Boolean flag = true;
        // принимает сообщения от сервера
        while (flag){
            try {
                //dis = new DataInputStream(cs.getInputStream());
                String s = dis.readUTF();
                System.out.println("read " + s);
                if (s.indexOf("-")!=-1){
                    System.out.println("want send");
                    DataOutputStream dataOutputStream = new DataOutputStream(cs.getOutputStream());
                    dataOutputStream.writeUTF(s);
                }

                if (s.indexOf("Hello")!= -1){
                    // значит к нам добавился игрок и надо постать его сервер
                    AddPlayer();
                    //st.sendToAll(" add new player ");
                }

                if (s.equals("RoomCreate")){
                    // создали новую комнату
                    addRoom();
                    getRoomInfo();
                    //st.sendToAll(" new room has been create");
                }
                if (s.equals("JoinRoom")){
                    joinRoom();

                }
                if (s.equals("GetRoom")){
                    // запрашиваем данные с сервера о количестве комнат
                    System.out.println(" получили сообщение о том что мы хотим получить комнаты ");
                    getRoomInfo();
                }
                if (s.equals("StartGame")){
                    st.sendToAll("Message All players connected");

                }
                if (s.equals("exit")){
                    // удалить клиента из хэш-таблице на сервере надо
                    deletePlayer();
                    flag =false;
                    System.out.println("server stoped");
                    //cs.close();
                }
                else {
                    st.sendToAll(s);
                }
                //System.out.println("readUTF on client  " + s );


            } catch (IOException e) {
                e.printStackTrace();
            }  catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void sendMessage(String s){
        // записываем сообщение в поток отправки
        new Thread() {
            @Override
            public void run() {
                try {
                    dos.writeUTF(s);
                    //System.out.println("send " + s);
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();

    }





}
