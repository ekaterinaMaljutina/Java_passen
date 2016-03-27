package client;

import parser.JSONObject;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class EdgeSender {

    private BufferedReader in;
    private PrintWriter out;


    public EdgeSender(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    public void send(int x, int y, int edge) {
        JSONObject obj = new JSONObject();
        obj.put("x", x);
        obj.put("y", y);
        obj.put("edge", edge);
        String edgeString = obj.toJSONString();
        //out.println(edgeString);
    }
}