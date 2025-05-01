package models.grond;


/** Hoofd tile klasse**/
public abstract class Tile {
    private final String type;  // Type tegel
    private final int x;       // X-coördinaat op het bord
    private final int y;       // Y-coördinaat op het bord
    private final int homebase;// Spelerindex als homebase (0 = geen homebase)


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