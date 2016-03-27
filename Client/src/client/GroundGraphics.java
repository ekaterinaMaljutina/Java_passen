package client;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class GroundGraphics {
    private Ground ground;
    private EdgeSender edgeSender;
    private int startX = 10;
    private int startY = 40;
    private int finishX = 510;
    private int finishY = 540;
    private int width = finishX - startX;
    private int height = finishY - startY;

    public EdgeState player = EdgeState.Null;
    public boolean myTurn = false;
    public GroundGraphics(Ground ground, EdgeSender edgeSender) {
        this.ground = ground;
        this.edgeSender = edgeSender;
    }

    public void clickProcess(int seekX, int seekY, int x, int y) {
        if ((x < seekX) || (y < seekY) || (x > seekX + width) || (y > seekY + height))
            return;
        if (!myTurn) return;
        for (int i = 0; i < ground.width; i++) {
            for (int j = 0; j < ground.height; j++) {
                int cellWidth = width / ground.width;
                int cellHeight = height / ground.height;
                if ((x > startX + i * cellWidth) && (x <= startX + (i + 1) * cellWidth)) {
                    if ((y > startY + j * cellHeight) && (y <= startY + (j + 1) * cellHeight)) {
                        int localX = x - seekX - i * cellWidth;
                        int localY = y - seekY - j * cellHeight;
                        int edgeNum;
                        if (localX < localY) {
                            if (cellWidth - localX < localY) {
                                edgeNum = 2;
                            }
                            else {
                                edgeNum = 3;
                            }
                        }
                        else {
                            if (cellWidth - localX < localY) {
                                edgeNum = 1;
                            }
                            else {
                                edgeNum = 0;
                            }
                        }
                        myTurn = false;
                        switch (edgeNum) {
                            case 0:
                                ground.InitEdge(i, j, 0, player);
                                ground.putLabel(i, j, ground.setLabel(player));
                                edgeSender.send(i, j, 0);
                                if (j > 0)
                                    if (!ground.hasPlayer(i, j - 1, 2)) {
                                        ground.InitEdge(i, j - 1, 2, player);
                                        ground.putLabel(i, j - 1, ground.setLabel(player));
                                    }
                                break;
                            case 1:
                                ground.InitEdge(i, j, 1, player);
                                ground.putLabel(i, j, ground.setLabel(player));
                                edgeSender.send(i, j, 1);
                                if (i < ground.width - 1)
                                    if (!ground.hasPlayer(i + 1, j, 2)) {
                                        ground.InitEdge(i + 1, j, 3, player);
                                        ground.putLabel(i + 1, j, ground.setLabel(player));
                                    }
                                break;
                            case 2:
                                ground.InitEdge(i, j, 2, player);
                                ground.putLabel(i, j, ground.setLabel(player));
                                edgeSender.send(i, j, 2);
                                if (j < ground.height - 1)
                                    if (!ground.hasPlayer(i, j + 1, 2)) {
                                        ground.InitEdge(i, j + 1, 0, player);
                                        ground.putLabel(i, j + 1, ground.setLabel(player));
                                    }
                                break;
                            case 3:
                                ground.InitEdge(i, j, 3, player);
                                ground.putLabel(i, j, ground.setLabel(player));
                                edgeSender.send(i, j, 3);
                                if (i > 0)
                                    if (!ground.hasPlayer(i - 1, j, 2)) {
                                        ground.InitEdge(i - 1, j, 1, player);
                                        ground.putLabel(i - 1, j, ground.setLabel(player));
                                    }
                                break;
                        }
                        break;
                    }
                }
            }
        }
    }

    public void draw(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setStroke(new BasicStroke(4));

        drawGameField(startX, startY, width, height, graphics2D);
    }

    private void drawGameField(int startX, int startY, int width, int height, Graphics2D graphics2D) {
        // Horisontal edges
        for (int i = 0; i < ground.width; i++) {
            for (int j = 0; j < ground.height; j++) {
                if (j == 0) {
                    switch (ground.landscape[i][j].getStateEdge(0)) {
                        case Player_1:
                            graphics2D.setColor(Color.RED);
                            break;
                        case Player_2:
                            graphics2D.setColor(Color.BLUE);
                            break;
                        case Null:
                        default:
                            graphics2D.setColor(Color.BLACK);
                            break;
                    }
                    graphics2D.drawLine(
                            startX + i * width / ground.width,
                            startY + j * height / ground.height,
                            startX + (i + 1) * width / ground.width,
                            startY + j * height / ground.height);
                }
                switch (ground.landscape[i][j].getStateEdge(2)) {
                    case Player_1:
                        graphics2D.setColor(Color.RED);
                        break;
                    case Player_2:
                        graphics2D.setColor(Color.BLUE);
                        break;
                    case Null:
                    default:
                        graphics2D.setColor(Color.BLACK);
                        break;
                }

                graphics2D.drawLine(
                        startX + i * width / ground.width,
                        startY + (j + 1) * height / ground.height,
                        startX + (i + 1) * width / ground.width,
                        startY + (j + 1) * height / ground.height);
            }
        }
        // Vertical edges
        for (int i = 0; i < ground.width; i++) {
            for (int j = 0; j < ground.height; j++) {
                if (i == 0) {
                    switch (ground.landscape[i][j].getStateEdge(3)) {
                        case Player_1:
                            graphics2D.setColor(Color.RED);
                            break;
                        case Player_2:
                            graphics2D.setColor(Color.BLUE);
                            break;
                        case Null:
                        default:
                            graphics2D.setColor(Color.BLACK);
                            break;
                    }
                    graphics2D.drawLine(
                            startX + i * width / ground.width,
                            startY + j * height / ground.height,
                            startX + i * width / ground.width,
                            startY + (j + 1) * height / ground.height);
                }
                switch (ground.landscape[i][j].getStateEdge(1)) {
                    case Player_1:
                        graphics2D.setColor(Color.RED);
                        break;
                    case Player_2:
                        graphics2D.setColor(Color.BLUE);
                        break;
                    case Null:
                    default:
                        graphics2D.setColor(Color.BLACK);
                        break;
                }

                graphics2D.drawLine(
                        startX + (i + 1) * width / ground.width,
                        startY + j * height / ground.height,
                        startX + (i + 1) * width / ground.width,
                        startY + (j + 1) * height / ground.height);
            }
        }

        // O and X
        for (int i = 0; i < ground.width; i++) {
            for (int j = 0; j < ground.height; j++) {
                if (ground.hasLabel(i, j)) {
                    switch (ground.landscape[i][j].getPlayer()) {
                        case X:
                            graphics2D.setColor(Color.RED);
                            graphics2D.drawLine(
                                    startX + i * width / ground.width + 10,
                                    startY + j * height / ground.height + 10,
                                    startX + (i + 1) * width / ground.width - 10,
                                    startY + (j + 1) * height / ground.height - 10);
                            graphics2D.drawLine(
                                    startX + (i + 1) * width / ground.width - 10,
                                    startY + j * height / ground.height + 10,
                                    startX + i * width / ground.width + 10,
                                    startY + (j + 1) * height / ground.height - 10);
                            break;
                        case O:
                            graphics2D.setColor(Color.BLUE);
                            Shape theCircle = new Ellipse2D.Double(
                                    startX + i * width / ground.width + 10,
                                    startY + j * height / ground.height + 10,
                                    width / ground.width - 20, width / ground.width - 20);
                            graphics2D.draw(theCircle);
                            break;
                    }
                }
            }
        }
    }
}

