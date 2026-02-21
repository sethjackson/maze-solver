package mazesolver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TileTypeTests {
    @Test
    public void shouldNotConvertInvalidTileType() {
        assertNull(TileType.toTileType('*'));
    }

    @Test
    public void shouldConvertStartTile() {
        assertEquals(TileType.START, TileType.toTileType('S'));
    }

    @Test
    public void shouldConvertEndTile() {
        assertEquals(TileType.END, TileType.toTileType('E'));
    }

    @Test
    public void shouldConvertSpaceTile() {
        assertEquals(TileType.SPACE, TileType.toTileType('.'));
    }

    @Test
    public void shouldConvertWallTile() {
        assertEquals(TileType.WALL, TileType.toTileType('#'));
    }
}
