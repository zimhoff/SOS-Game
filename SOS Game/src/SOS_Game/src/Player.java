package SOS_Game.src;

public class Player {
    private final String name;
    private BoardTile tile;
    private PlayStyle style;
    private Integer points;
    public Player(SOS_Game.src.BoardTile.TileValue value, String name) {
        this.name = name;
        this.tile = new BoardTile(value);
        this.style = PlayStyle.Human;
        this.points = 0;
    }

    public BoardTile getTile() {
        return this.tile;
    }

    public void setTile(SOS_Game.src.BoardTile.TileValue tile) {
        this.tile = new BoardTile(tile);
    }

    public PlayStyle getStyle() {
        return this.style;
    }

    public void setStyle(PlayStyle style) {
        this.style = style;
    }

    public String getName() {
        return this.name;
    }

    public Integer getPoints() {
        return this.points;
    }

    public void incrementPoints() {
        this.points++;
    }

    public void resetPoints() {
        this.points = 0;
    }

    public String toString() {
        return String.format("%s (%s, %s)", this.name, this.tile, this.style);
    }

    public enum PlayStyle {
        Human, Computer
    }
}