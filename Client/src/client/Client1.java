package client;

import parser.JSONObject;
import parser.JSONParser;
import parser.ParseExeption;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client1 extends JFrame{

    // UI
    private JPanel panel;
    private JLabel messageLabel = new JLabel("Hello");

    // Game
    private Ground gameGround;
    private EdgeSender edgeSender;
    private GroundGraphics gameGroundGraphics;
    private EdgeState player = EdgeState.Null;
    private EdgeState opponent = EdgeState.Null;

    // Sockets
    private static int PORT = 8901;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client1(String serverAddress) throws Exception {
        super("Koridorchiki");
        initUI();

        // Setup networking
        try {
            socket = new Socket(serverAddress, PORT);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            edgeSender = new EdgeSender(in, out);
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }

    public void initializeGame() {
        gameGround = new Ground(5, 5);
        gameGround.Init();
        gameGroundGraphics = new GroundGraphics(gameGround, edgeSender);
    }

    private void initUI() {
        setSize(540, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel = new GPanel();
        messageLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        panel.add(messageLabel);
        add(panel);
    }

    private void play() throws Exception {
        String response;
        try {
            response = in.readLine();

            System.out.print(response);
            if (response.startsWith("Hello")) {
                String playerName = response.split(" ")[1];
                if (playerName.equals("Player_1")) {
                    player = gameGroundGraphics.player = EdgeState.Player_1;
                    opponent = EdgeState.Player_2;
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Hello Player 1!");
                }
                if (playerName.equals("Player_2")) {
                    player = gameGroundGraphics.player = EdgeState.Player_2;
                    opponent = EdgeState.Player_1;
                    messageLabel.setForeground(Color.BLUE);
                    messageLabel.setText("Hello Player 2!");
                }
            }
            while (true) {
                response = in.readLine();
                System.out.print(response + '\n');
                if (response.startsWith("Successful_move")) {
                    messageLabel.setText("Valid move, please wait");
                    gameGroundGraphics.myTurn = false;
                } else if (response.startsWith("Invalid_move")) {
                    messageLabel.setText("Invalid move, please try again");
                    gameGroundGraphics.myTurn = true;
                } else if (response.startsWith("YOUR_TURN")) {
                    messageLabel.setText("Your turn");
                    gameGroundGraphics.myTurn = true;
                } else if (response.startsWith("Winner")) {
                    messageLabel.setText(response);
                    break;
                } else if (response.startsWith("Tie")) {
                    messageLabel.setText("You tied");
                    break;
                } else if (response.startsWith("Message")) {
                    messageLabel.setText(response.substring(8));
                }
                else {
                    processOpponentTurn(response);
                    messageLabel.setText("Opponent moved, please wait");
                    panel.repaint();
                }
            }
            System.out.print(response);
            out.println("QUIT");
        }
        finally {
            socket.close();
        }
    }

    private void processOpponentTurn(String response) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response);
            JSONObject jsonObject = (JSONObject) obj;
            int x = ((Long) jsonObject.get("x")).intValue();
            int y = ((Long) jsonObject.get("y")).intValue();
            int edge = ((Long) jsonObject.get("edge")).intValue();
            gameGround.InitEdge(x, y, edge, opponent);
            gameGround.putLabel(x, y, gameGround.setLabel(opponent));
            switch (edge) {
                case 0:
                    if (y > 0) {
                        gameGround.InitEdge(x, y - 1, 2, opponent);
                        gameGround.putLabel(x, y - 1, gameGround.setLabel(opponent));
                    }
                    break;
                case 1:
                    if (x < gameGround.width - 1) {
                        gameGround.InitEdge(x + 1, y, 3, opponent);
                        gameGround.putLabel(x + 1, y, gameGround.setLabel(opponent));
                    }
                    break;
                case 2:
                    if (y < gameGround.height - 1) {
                        gameGround.InitEdge(x, y + 1, 0, opponent);
                        gameGround.putLabel(x, y + 1, gameGround.setLabel(opponent));
                    }
                    break;
                case 3:
                    if (x > 0) {
                        gameGround.InitEdge(x - 1, y, 1, opponent);
                        gameGround.putLabel(x - 1, y, gameGround.setLabel(opponent));
                    }
                    break;
            }
        } catch (ParseExeption e) {
            System.out.print(e.getMessage());
        }
    }

    private boolean wantsToPlayAgain() {
        int response = JOptionPane.showConfirmDialog(this,
                "Want to play again?",
                "Koridorchiki",
                JOptionPane.YES_NO_OPTION);
        this.dispose();
        return response == JOptionPane.YES_OPTION;
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            String serverAddress = (args.length == 0) ? "localhost" : args[1];
            Client1 client = new Client1(serverAddress);
            client.initializeGame();
            client.setVisible(true);
            client.play();
            if (!client.wantsToPlayAgain()) {
                break;
            }
        }
    }

    class GPanel extends JPanel {
        public GPanel() {
            setPreferredSize(new Dimension(540, 600));
            MouseAdapter mouseHandler;
            mouseHandler = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Point point = e.getPoint();
                    gameGroundGraphics.clickProcess(8, 48, point.x, point.y);
                    repaint();
                }
            };
            addMouseListener(mouseHandler);
        }

        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            gameGroundGraphics.draw(graphics);
        }
    }
}
