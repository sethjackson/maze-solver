package mazesolver;

import java.io.StringReader;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MazeTest {
    @Test
    public void shouldThrowNullPointerExceptionWhenParsingNullMaze() {
        assertThrows(NullPointerException.class, () -> Maze.parse(null));
    }

    @Test
    public void shouldNotParseMazeWithInvalidCharacters() {
        assertThrows(ParseException.class, () -> Maze.parse(new StringReader("#****#")));
    }

    @Test
    public void shouldHaveTilesForValidMaze() throws Exception {
        var maze = Maze.parse(new StringReader("S..E"));

        assertNotNull(maze.getTiles());
        assertEquals(1, maze.getTiles().size());
    }

    @Test
    public void shouldHaveStartForValidMaze() throws Exception {
        var maze = Maze.parse(new StringReader("S..E"));

        assertNotNull(maze.getStart());
        assertEquals(0, maze.getStart().getX());
        assertEquals(0, maze.getStart().getX());
    }

    @Test
    public void shouldHaveWidthAndHeightForValidMaze() throws Exception {
        var maze = Maze.parse(new StringReader("S..E"));

        assertEquals(4, maze.getWidth());
        assertEquals(1, maze.getHeight());
    }

    @Test
    public void shouldParseMazeWithMultipleLines() throws Exception {
        var maze = Maze.parse(new StringReader("####\nS..E\n####"));

        assertNotNull(maze.getStart());
        assertEquals(0, maze.getStart().getX());
        assertEquals(1, maze.getStart().getY());
        assertEquals(4, maze.getWidth());
        assertEquals(3, maze.getHeight());
    }

    @Test
    public void shouldNotHaveStartWhenThereIsNoStartingPoint() throws Exception {
        var maze = Maze.parse(new StringReader("#...E"));

        assertNull(maze.getStart());
    }

    @Test
    public void shouldThrowParseExceptionWhenThereAreMultipleStartingPoints() {
        assertThrows(ParseException.class, () -> Maze.parse(new StringReader("S..SE")));
    }

    @Test
    public void shouldSolveSolvableMaze() throws Exception {
        var maze = Maze.parse(new StringReader("S..E"));

        assertTrue(maze.solve());
    }

    @Test
    public void shouldNotSolveMazeWithoutStart() throws Exception {
        var maze = Maze.parse(new StringReader("####"));

        assertFalse(maze.solve());
    }

    @Test
    public void shouldNotSolveUnsolvableMaze() throws Exception {
        var maze = Maze.parse(new StringReader("#S#E"));

        assertFalse(maze.solve());
    }

    @Test
    public void shouldHaveHistoryForSolvableMaze() throws Exception {
        var maze = Maze.parse(new StringReader("S..E"));

        maze.solve();

        assertEquals(4, maze.getHistory().size());
    }

    @Test
    public void shouldNotDuplicateHistoryWhenSolvingSolvableMazeMoreThanOnce() throws Exception {
        var maze = Maze.parse(new StringReader("S..E"));

        maze.solve();
        maze.solve();

        assertEquals(4, maze.getHistory().size());
    }
}
