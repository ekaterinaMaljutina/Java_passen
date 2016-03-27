package client;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormInterface {
    private Stage stage;
    private double width = 400;
    private double height = 275;

    private int port = 3124;
    private InetAddress ip = null;
    private Socket cs;
    private DataOutputStream dos;

    private UUID playerId;
    private Vector<java.lang.String> nameRoom =
            new Vector<>();

    private Player player;
    private Player opponent;

    private List<RoomInfo> rooms;
    //private GameBoard gameBoard;
    private myThread myThread;
    private int indexRoom;

    private JPanel panel;

    public FormInterface(Stage stage ){
        this.stage = stage;
        start();
    }

    public void setWindowSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public void start() {
        registration();
    }

    public void registration() {

        setWindowSize(width, height);
        stage.setTitle("Passages");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scene_title = new Text("Welcome");
        scene_title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scene_title, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);
        userTextField.setText("Kate");

        Button btn = new Button("Sign in");
        btn.setDefaultButton(true);
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);

        btn.setOnAction(event -> {

            try {
                ip = InetAddress.getLocalHost();


                cs = new Socket(ip, port);
                System.out.println("socket run");

                player = new Player(userTextField.getText());//,playerId);

                System.out.println("player create " + player.getNamePlayer() );
                dos = new DataOutputStream(cs.getOutputStream());
                dos.writeUTF("Hello " + player.getNamePlayer());

                ObjectOutputStream objectOutputStream= new ObjectOutputStream(cs.getOutputStream());
                objectOutputStream.writeObject(player);

                // тут надо запросить с сервера данные об уже созданных комнатах
                dos = new DataOutputStream(cs.getOutputStream());
                dos.writeUTF("GetRoom");
                System.out.println(" отправили сообщение о том что мы хотим получить комнаты ");

                myThread = new myThread(cs);
                myThread.start();

                rooms = myThread.getRoomInfos();

                if (rooms!=null) {
                    System.out.println(rooms.size());
                }


            } catch (UnknownHostException ex) {
                Logger.getLogger(FormInterface.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                chooseTheRoom();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        Scene scene = new Scene(grid, width, height);
        stage.setScene(scene);
        stage.show();
    }

    public void chooseTheRoom() throws IOException {

        Button joinBtn = new Button("Join room");
        Button createBtn = new Button("Create room");
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 5, 5, 10));
        hBox.getChildren().add(joinBtn);
        hBox.getChildren().add(createBtn);

        GridPane createGrid = new GridPane();
        createGrid.setAlignment(Pos.CENTER);
        createGrid.setHgap(10);
        createGrid.setVgap(10);
        Text createTitle = new Text("Create room");
        createTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        createGrid.add(createTitle, 0, 0, 2, 1);
        Label roomName = new Label("Room name:");
        createGrid.add(roomName, 0, 1);

        TextField roomTextField = new TextField();
        roomTextField.setText("start_room");
        createGrid.add(roomTextField, 1, 1);

        GridPane joinGrid = new GridPane();
        joinGrid.setAlignment(Pos.CENTER);
        joinGrid.setHgap(10);
        joinGrid.setVgap(10);
        joinGrid.setPadding(new Insets(20, 20, 10, 10));

        Text joinTitle = new Text("Join room");
        joinTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        joinGrid.add(joinTitle, 0, 0, 2, 1);

        Button okBtn = new Button("OK");
        okBtn.setPrefSize(50, 20);
        HBox hBoxBtn = new HBox();
        hBoxBtn.setPadding(new Insets(20, 20, 30, 20));
        hBoxBtn.setAlignment(Pos.CENTER);
        hBoxBtn.getChildren().add(okBtn);

        Button exit = new Button("exit");
        exit.setPrefSize(90, 20);
        hBoxBtn.getChildren().add(exit);

        BorderPane borderPane = new BorderPane();

        borderPane.setTop(hBox);
        borderPane.setCenter(joinGrid);
        borderPane.setBottom(hBoxBtn);

        exit.setOnMouseClicked(event1 -> {

            try {

                dos = new DataOutputStream(cs.getOutputStream());
                dos.writeUTF("exit");
                myThread.setRun(false);
                myThread.end();
                //cs.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.close();

        });

        createBtn.setOnMouseClicked(event -> borderPane.setCenter(createGrid));

        joinBtn.setOnMouseClicked( event2 ->  {

            try {
                System.out.println(" хотим получить комнаты с сервера ");

                rooms = myThread.getRoomInfos();
                if (rooms!=null) {
                    System.out.println(rooms.size());
                }

                chooseTheRoom();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        ListView<java.lang.String> list = new ListView<>();
        Vector<java.lang.String> items = new Vector<>();
        //myThread.end();

        if (rooms!=null) {
            for (RoomInfo r : rooms) {
                items.add(r.getName());
            }
        }
        list.setItems(FXCollections.observableArrayList(items));
        /*if (nameRoom!=null) {
            list.setItems(FXCollections.observableArrayList(nameRoom));
        }*/
        joinGrid.add(list, 0, 1);

        okBtn.setOnMouseClicked( event -> {
            if (borderPane.getCenter() == createGrid) {
                nameRoom.add(roomTextField.getText());

                try {
                    dos = new DataOutputStream(cs.getOutputStream());
                    dos.writeUTF("RoomCreate");
                    dos.writeUTF(roomTextField.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                int idx = list.getSelectionModel().getSelectedIndices().get(0);
                indexRoom = idx;
                RoomInfo r = rooms.get(idx);
                try {
                    dos = new DataOutputStream(cs.getOutputStream());
                    dos.writeUTF("JoinRoom");
                    //  должны отправить объект roomInfo

                    ObjectOutputStream objectOutputStream= new ObjectOutputStream(cs.getOutputStream());
                    objectOutputStream.writeObject(r);
                    myThread.setRun(false);
                    stage.hide();

                    // создаем поля для игры
                    System.out.println(" create game board");;
                    /*gameBoard = new GameBoard();
                    gameBoard.initializeGame();
                    gameBoard.setVisible(true);
*/
                    return;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("chose room " + r.getName() );

            }
        });
        Scene scene = new Scene(borderPane, width, height);
        stage.setScene(scene);
        stage.show();
    }



    /*public class GameBoard extends JFrame {
        // UI
        private JPanel panel;
        private JLabel messageLabel = new JLabel("Hello");

        // Game
        private Ground gameGround;
        private EdgeSender edgeSender;
        private GroundGraphics gameGroundGraphics;
        private EdgeState player_1 = EdgeState.Null;
        private EdgeState opponent = EdgeState.Null;

        private ThreadGame threadGame;

        public GameBoard(){

            super("Passeng_Korid");
            initUI();

            threadGame = new ThreadGame();
            threadGame.start();
            // edgeSender = new EdgeSender(in, out);

        }
        private void initUI() {

            setSize(540, 600);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        threadGame.end();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    super.windowOpened(e);
                }
            });


            panel = new GPanel();
            messageLabel.setFont(new java.awt.Font("Serif", java.awt.Font.PLAIN, 24));
            panel.add(messageLabel);
            add(panel);
        }

        public void initializeGame() throws IOException {
            gameGround = new Ground(5, 5);
            gameGround.Init();
            gameGroundGraphics = new GroundGraphics(gameGround, edgeSender);
            //gameBoard.play();
        }

        private void play() throws IOException  {

            *//*while (true){

            }*//*
            *//*String playerName = player.getNamePlayer();
            Player player1 = rooms.get(indexRoom).getplayer_1();
            Player player2 = rooms.get(indexRoom).getplayer_2();
            if (player1!=null && player2!=null){
                System.out.println("GameStart");
                dos = new DataOutputStream(cs.getOutputStream());
                dos.writeUTF("StartGame");
            }
            else{
                messageLabel.setText("Wait your oppenent");
                boolean f = !myThread.flag_opponent;  // true
                System.out.println(" wait with flag " + myThread.flag_opponent);
                *//**//*while (f){
                    //System.out.println("here");
                    // ждем когда придет оповещение что игрок присоеденился
                    f = !myThread.flag_opponent;
                }
                if (!f){
                    messageLabel.setText("Your turn");
                }*//**//*

            }*//*
            //String response;

                //response = in.readLine();

                //System.out.print(response);
                //if (response.startsWith("Hello")) {
                //response.split(" ")[1];
                    *//*if (playerName.equals("Player_1")) {
                        //player = gameGroundGraphics.player = EdgeState.Player_1;
                        //opponent = EdgeState.Player_2;
                        messageLabel.setForeground(Color.RED);
                        messageLabel.setText("Hello Player 1!");
                    }*//*
                    *//*if (playerName.equals("Player_2")) {
                        //player = gameGroundGraphics.player = EdgeState.Player_2;
                        //opponent = EdgeState.Player_1;
                        messageLabel.setForeground(Color.BLUE);
                        messageLabel.setText("Hello Player 2!");
                    }*//*
                //}
                //while (true) {
                    *//*response = in.readLine();
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
                    }*//*
                //}
                //System.out.print(response);
                //out.println("QUIT");

        }


        public class ThreadGame extends Thread{


            private  DataInputStream dataInputStream;
            private boolean run = true;


            public  ThreadGame(){
                super();

                try {

                    dataInputStream = new DataInputStream(cs.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void run() {
                try {
                    messageLabel.setText("Start Game");
                    dataInputStream = new DataInputStream(cs.getInputStream());
                    while (run) {
                        System.out.println("wait .... ");
                        String srt = dataInputStream.readUTF();
                        System.out.println(" input: " + srt);

                        if (srt.equals("Player1")) {
                            System.out.println("I am player_1. I want to play!");
                            messageLabel.setText("Wait your opponent");
                        }

                        if (srt.equals("OpponentIsReady")) {
                            System.out.println("I am player_2. I want to play!");
                    *//*DataOutputStream dataOutputStream = new DataOutputStream(cs.getOutputStream());
                    dataOutputStream.writeUTF("");*//*
                            messageLabel.setText("Your turn");

                        }
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }

            }

            public  void  end() throws IOException{
                System.out.println("close thread .....");
                DataOutputStream dataOutputStream = new DataOutputStream(cs.getOutputStream());
                dataOutputStream.writeUTF("exit");
                run = false;
                super.stop();
            }
        }

        public class GPanel extends JPanel {
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
    }*/
}
