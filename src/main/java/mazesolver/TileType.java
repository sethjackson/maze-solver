package mazesolver;

public enum TileType {
    START('S'),
    END('E'),
    SPACE('.'),
    WALL('#');

    private final char value;

    TileType(char value) {
        this.value = value;
    }

    public static TileType toTileType(char tile) {
        for (var type : TileType.values()) {
            if (type.value == tile) {
                return type;
            }
        }

        return null;
    }
}
