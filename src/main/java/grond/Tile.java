package grond;

public abstract class Tile {
    protected final String type;
    protected final int x;
    protected final int y;
    protected final int homebase;

    public Tile(String type, int x, int y, int homebase) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.homebase = homebase;
    }

    public String getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHomebase() {
        return homebase;
    }
}