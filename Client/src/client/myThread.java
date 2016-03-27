package client;

/**
 * Created by kate on 27.03.16.
 */

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class myThread extends Thread {

    Socket cs;
    DataInputStream dis;
    String str;
    List<RoomInfo> roomInfos;
    Thread thread;
    boolean flag_game = false;
    boolean flag_opponent = false;
    boolean run = true;

    public myThread(Socket cs) throws IOException {
        this.cs = cs;
        this.dis = new DataInputStream(cs.getInputStream());
    }

    public synchronized List<RoomInfo> getRoomInfos() {
        return roomInfos;
    }

    public synchronized void start() {
        roomInfos = new ArrayList<>();
        thread = new Thread();
        thread.start();
    }

    public void end() throws IOException {
        thread.end();
    }

    public void setRun(boolean run) {
        this.run = run;
    }


    public class Thread extends java.lang.Thread {
        @Override
        public synchronized void run() {
            while (run) {
                System.out.println("wait ");
                try {
                    str = dis.readUTF();
                    if (str.equals("SizeRoomInfo")) {
                        int size = dis.readInt();
                        System.out.println("size  " + Integer.toString(size));

                        for (int i = 0; i < size; i++) {
                            ObjectInputStream objectInputStream = new ObjectInputStream(cs.getInputStream());
                            RoomInfo roomInfo = (RoomInfo) objectInputStream.readObject();
                            if (roomInfo != null) {
                                roomInfos.add(roomInfo);
                                System.out.println(" add room in thread " + roomInfos.size());
                            }
                        }
                    }
                    if (str.equals("ConnectionPlayer")) {
                        flag_game = true;
                        System.out.println("flag is " + flag_game);
                    }

                    if (str.equals("OpponentIsReady")) {
                        flag_opponent = true;
                        System.out.println("flag is opponent " + flag_opponent);
                    }

                    System.out.println("input " + str);

                } catch (Exception ex) {
                    System.out.println("oops");
                    Logger.getLogger(myThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }


        public synchronized void end() throws IOException {
            //cs.close();
            System.out.println(" thread close ");
            super.stop();
        }
    }
}
