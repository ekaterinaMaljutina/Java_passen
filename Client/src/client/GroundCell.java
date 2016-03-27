package client;

public class GroundCell {

    private EdgeState[] stateEdge = new EdgeState[4];
    private Label player;
    private int x;
    private int y;

    public GroundCell(int x, int y) {
        this.x = x;
        this.y = y;
        this.player = Label.Null;
    }

    public void setPlayer(Label player) {
        this.player = player;
    }

    public Label getPlayer() {
        return player;
    }

    public void setStateEdge(EdgeState stateEdge, int edge) {
        this.stateEdge[edge] = stateEdge;
    }

    public EdgeState getStateEdge(int edge) {
        return stateEdge[edge];
    }
}