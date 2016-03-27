package client;

import game.CellState;
import game.Label;
import parser.JSONObject;

import java.io.Serializable;


public class Player implements Serializable {
    //private Socket socket;
    //private DataOutputStream dos;

    private Player opponent;
    private CellState player;  // какой игрок по номеру 1 или 2
    private Label label;    // чем закрашивается клетка (крестиком или кружочком)
    private String name;


    public Player( String name) {
        this.name = name;
    }
    public void setPlayer( CellState cellState){ this.player = cellState;}

    public CellState getPlayer(){return  player;}
    public String getNamePlayer(){
        return name;
    }

    public void setLabel( Label label){this.label = label;}
    public Label getLabel (){return label;}

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
        //this.label = ground.setLabel(player);
    }
    public  Player getOpponent(){return opponent;}

    public void sendEdge(int x, int y, int edge) {
        JSONObject obj = new JSONObject();
        obj.put("x", x);
        obj.put("y", y);
        obj.put("edge", edge);
        String edgeString = obj.toJSONString();

        // хотели отправить на сервер
        //output.println(edgeString);
    }


    public void run(){
       /* try {
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
        } catch(ParseExeption e){
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
