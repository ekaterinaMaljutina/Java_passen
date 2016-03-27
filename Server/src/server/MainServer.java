package server;

import java.io.IOException;

public class MainServer {

    private Server server = null;
    public static void main(String[] args) throws IOException {
        Server server = new Server("");
        server.start();
    }

}
