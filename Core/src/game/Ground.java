package game;


public class Ground {
    private GroundCell[][] landscape;
    private int length;
    private int width;

    public Ground(int length, int width) {
        this.length = length;
        this.width = width;
        this.landscape = new GroundCell[this.length][this.width];
    }

    public GroundCell[][] getLandscape() {
        return landscape;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public void setGroundCell(int x, int y, Label label) {//????
        landscape[x][y].setPlayer(label);
    }

    public void Init(){
        for (int i = 0; i < this.length; i++)
            for (int j = 0; j < this.width; j++) {
                this.landscape[i][j] = new GroundCell(i, j);
                InitEdge(i, j, 0, CellState.Null);
                InitEdge(i, j, 1, CellState.Null);
                InitEdge(i, j, 2, CellState.Null);
                InitEdge(i, j, 3, CellState.Null);
            }
    }
    //инициализация ребра соответствующим игроком
    public void InitEdge(int x, int y, int edge, CellState stateEdge){
        this.landscape[x][y].setStateEdge(stateEdge,edge);
    }
    // проверка ячейки на наличие в ней крестика или нолика
    public boolean hasLabel(int  x, int y){
        if (landscape[x][y].getPlayer() != null &&
                (landscape[x][y].getPlayer().equals(Label.O) || landscape[x][y].getPlayer().equals(Label.X)))
            return true;
        else
            return false;
    }
    //проверка ребра на наличие в ней уже игрока
    public synchronized boolean hasPlayer(int x, int y, int edge){
        if (landscape[x][y].getStateEdge(edge).equals(CellState.Player_1)||
                landscape[x][y].getStateEdge(edge).equals(CellState.Player_2))
            return true;
        else
            return false;
    }
    //ставим метку крестик или нолик соответствующему игроку Player1 - X; Player2 - O;
    public Label setLabel(CellState player) {
        if (player.equals(CellState.Player_1))
            return Label.X;
        else
            return Label.O;
    }
    //сопоставление метки игроку, используется в putLabel;
    public CellState setState(Label label){
        if (label.equals(Label.X))
            return CellState.Player_1;
        else
            return CellState.Player_2;
    }
    public boolean putLabel(int x, int y, Label label){
        int edgeFlag = 0;
        if (!hasLabel(x, y))
            for (int i = 0; i < 4; i++)
                if (hasPlayer(x, y, i))
                    edgeFlag++;
        if (edgeFlag == 4) {
            setGroundCell(x, y, label);
            return true;
        }
        else
            return false;
    }
    //кто выиграл
    public CellState hasWinner() {
        int player1 = 0;
        int player2 = 0;
        for (int i = 0; i < this.length; i++)
            for (int j = 0; j < this.width; j++) {
                if (landscape[i][j].getPlayer().equals(Label.O))
                    player1++;
                else if (landscape[i][j].getPlayer().equals(Label.X))
                    player2++;
            }
        if (player1 > player2) return CellState.Player_1;
        if (player1 < player2) return CellState.Player_2;
        return null;//if player1 = player2
    }
}
