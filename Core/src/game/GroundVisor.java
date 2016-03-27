package game;

import client.Player;


public class GroundVisor {

    private Ground ground;
    private Player curPlayer;

    public GroundVisor(Ground ground) {
        this.ground = ground;
    }

    public void setCurPlayer(Player curPlayer) {
        this.curPlayer = curPlayer;
    }

    //корректность движения по ребру
    public synchronized boolean legalMove(int x, int y, int edge, Player player) {
        if (player == curPlayer && !ground.hasPlayer(x,y,edge)) {
            boolean newMove = true;
            ground.InitEdge(x, y, edge, player.getPlayer()); //инициализируем ребро соответствующим игроком
            if (ground.putLabel(x, y, player.getLabel())) //Проверка можно ли уже положить в ячейку ноль или крестик
                newMove = false;
            switch (edge) {
                case 0:
                    if (y > 0) {
                        ground.InitEdge(x, y - 1, 2, player.getPlayer());
                        if ((ground.putLabel(x, y - 1, player.getLabel()) && newMove))
                            newMove = false;
                    }
                    break;
                case 1:
                    if (x < ground.getWidth() - 1) {
                        ground.InitEdge(x + 1, y, 3, player.getPlayer());
                        if ((ground.putLabel(x + 1, y, player.getLabel())) && newMove)
                            newMove = false;
                    }
                    break;
                case 2:
                    if (y < ground.getLength() - 1) {
                        ground.InitEdge(x, y + 1, 0, player.getPlayer());
                        if ((ground.putLabel(x, y + 1, player.getLabel())) && newMove)
                            newMove = false;
                    }
                    break;
                case 3:
                    if (x > 0) {
                        ground.InitEdge(x - 1, y, 1, player.getPlayer());
                        if ((ground.putLabel(x - 1, y, player.getLabel())) && newMove)
                            newMove = false;
                    }
                    break;
            }
            if (newMove) {
                curPlayer = curPlayer.getOpponent();
            }
            return true;
        }
        return false;
    }
    //проверка на заполненность всего поля,вызывается каждый раз, когда передается ход новому игроку
    public boolean checkGroundFill(){
        for (int i = 0; i < ground.getLength(); i++)
            for (int j = 0; j < ground.getWidth(); j++)
                for (int k = 0; k < 4; k++)
                    if (!ground.hasPlayer(i,j,k))
                        return false;
        return true;
    }
}
