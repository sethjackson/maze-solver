package mazesolver;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class MazePanel extends JPanel {
    private Maze maze;
    private int step;

    public MazePanel() {
    }

    public Maze getMaze() {
        return maze;
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public int getStep() {
        return step;
    }

    public void step() {
        step++;
    }

    public void reset() {
        step = 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (maze == null || maze.getTiles().isEmpty()) {
            super.paintComponent(g);

            return;
        }

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        var tileWidth = getWidth() / maze.getWidth();
        var tileHeight = getHeight() / maze.getHeight();

        for (var column : maze.getTiles()) {
            for (var tile : column) {
                g.setColor(getColorForTile(tile));
                g.fillRect(tile.getX() * tileWidth, tile.getY() * tileHeight, tileWidth, tileHeight);
            }
        }

        for (var i = 0; i <= step; i++) {
            var tile = maze.getHistory().get(i);

            g.setColor(Color.GREEN);
            g.fillRect(tile.getX() * tileWidth, tile.getY() * tileHeight, tileWidth, tileHeight);
        }
    }

    private Color getColorForTile(Tile tile) {
        switch (tile.getTileType()) {
            case START:
                return Color.YELLOW;
            case END:
                return Color.RED;
            case SPACE:
                return Color.GRAY;
            case WALL:
                return Color.BLACK;
        }

        return Color.BLACK;
    }
}
