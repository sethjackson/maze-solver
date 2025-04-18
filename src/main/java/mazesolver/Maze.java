package mazesolver;

import java.io.BufferedReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class Maze {
    private final List<List<Tile>> tiles;
    private final Tile start;
    private List<Tile> history;
    private final int width;

    private Maze(List<List<Tile>> tiles, Tile start, int width) {
        this.tiles = tiles;
        this.start = start;
        this.history = new ArrayList<>();
        this.width = width;
    }

    public static Maze parse(Reader maze) throws Exception {
        Objects.requireNonNull(maze);

        var tiles = new ArrayList<List<Tile>>();
        Tile start = null;
        var longestLine = 0;

        try (var reader = new BufferedReader(maze)) {
            var y = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                longestLine = Math.max(line.length(), longestLine);

                var row = new ArrayList<Tile>();

                for (var x = 0; x < line.length(); x++) {
                    var c = line.charAt(x);
                    var tileType = TileType.toTileType(c);

                    if (tileType == null) {
                        throw new ParseException(String.format("Invalid tile type '%s'.", c), x);
                    }

                    var tile = new Tile(x, y, tileType);

                    if (tile.getTileType() == TileType.START) {
                        if (start != null) {
                            throw new ParseException("Only one starting point is allowed.", x);
                        }

                        start = tile;
                    }

                    row.add(tile);
                }

                tiles.add(row);

                y++;
            }
        }

        return new Maze(tiles, start, longestLine);
    }

    public boolean solve() {
        if (start == null) {
            return false;
        }

        history = new ArrayList<>();

        for (var column : tiles) {
            for (var tile : column) {
                tile.setVisited(false);
            }
        }

        var moves = new Stack<Tile>();

        moves.push(start);

        while (!moves.isEmpty()) {
            var tile = moves.pop();

            if (tile.getTileType() == TileType.END) {
                history.add(tile);

                return true;
            } else if (tile.getTileType() == TileType.WALL) {
                continue;
            } else if (tile.isVisited()) {
                continue;
            }

            tile.setVisited(true);

            history.add(tile);

            var x = tile.getX();
            var y = tile.getY();

            if (x < getWidth() - 1) {
                moves.push(tiles.get(y).get(x + 1));
            }

            if (y > 0) {
                moves.push(tiles.get(y - 1).get(x));
            }

            if (x > 0) {
                moves.push(tiles.get(y).get(x - 1));
            }

            if (y < getHeight() - 1) {
                moves.push(tiles.get(y + 1).get(x));
            }
        }

        return false;
    }

    public List<List<Tile>> getTiles() {
        return tiles;
    }

    public Tile getStart() {
        return start;
    }

    public List<Tile> getHistory() {
        return history;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return tiles.size();
    }
}
