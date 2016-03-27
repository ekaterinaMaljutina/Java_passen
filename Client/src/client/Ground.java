package client;

public class Ground {
    public GroundCell[][] landscape;
    public int height;
    public int width;

    public Ground(int height, int width) {
        this.height = height;
        this.width = width;
        this.landscape = new GroundCell[this.height][this.width];
    }

    public void setGroundCell(int x, int y, Label label) {//????
        landscape[x][y].setPlayer(label);
    }

    public void Init(){
        for (int i = 0; i < this.height; i++)
            for (int j = 0; j < this.width; j++){
                landscape[i][j] = new GroundCell(i,j);
                InitEdge(i, j, 0, EdgeState.Null);
                InitEdge(i, j, 1, EdgeState.Null);
                InitEdge(i, j, 2, EdgeState.Null);
                InitEdge(i, j, 3, EdgeState.Null);
            }
        //System.out.println("Successful init the ground");
    }

    public void InitEdge(int x, int y, int edge, EdgeState stateEdge){
        this.landscape[x][y].setStateEdge(stateEdge, edge);
    }

    public boolean hasPlayer(int x, int y, int edge){
        if (landscape[x][y].getStateEdge(edge).equals(EdgeState.Player_1)||
                landscape[x][y].getStateEdge(edge).equals(EdgeState.Player_2))
            return true;
        else
            return false;
    }
    public boolean hasLabel(int  x, int y){
        if (landscape[x][y].getPlayer().equals(Label.X) || landscape[x][y].getPlayer().equals(Label.O))
            return true;
        else
            return false;
    }

    public Label setLabel(EdgeState player) {
        if (player.equals(EdgeState.Player_1))
            return Label.X;
        else
            return Label.O;
    }
    public void putLabel(int x, int y, Label label){
        int edgeFlag = 0;
        if (!hasLabel(x, y))
            for (int i = 0; i < 4; i++)
                if (hasPlayer(x, y, i))
                    edgeFlag++;
        if (edgeFlag == 4)
            setGroundCell(x,y, label);
    }
}
