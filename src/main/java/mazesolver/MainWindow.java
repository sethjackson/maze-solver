package mazesolver;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class MainWindow extends JFrame {
    private Timer timer;

    private JButton animateButton;
    private JButton stepButton;
    private JButton pauseButton;

    private MazePanel mazePanel = new MazePanel();
    private StatusPanel statusPanel = new StatusPanel(this);

    public MainWindow() {
        setTitle("Maze Solver");
        setPreferredSize(new Dimension(640, 480));
        setMinimumSize(getPreferredSize());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        timer = new Timer(150, action -> tick());

        createMenus();
        createButtons();

        add(mazePanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        pack();

        setVisible(true);
    }

    private void createMenus() {
        var menuBar = new JMenuBar();

        var fileMenu = new JMenu("File");

        var openMenuItem = new JMenuItem("Open");
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        openMenuItem.addActionListener(action -> openMaze());
        fileMenu.add(openMenuItem);

        if (!(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.APP_QUIT_STRATEGY))) {
            var quitMenuItem = new JMenuItem("Quit");
            quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
            quitMenuItem.addActionListener(action -> System.exit(0));
            fileMenu.add(quitMenuItem);
        }

        menuBar.add(fileMenu);

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.APP_MENU_BAR)) {
            Desktop.getDesktop().setDefaultMenuBar(menuBar);
        } else {
            setJMenuBar(menuBar);
        }
    }

    private void createButtons() {
        var buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        animateButton = new JButton("Animate");
        animateButton.addActionListener(action -> animate());
        animateButton.setEnabled(false);

        stepButton = new JButton("Step");
        stepButton.addActionListener(action -> step());
        stepButton.setEnabled(false);

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(action -> pause());
        pauseButton.setEnabled(false);

        buttonPanel.add(animateButton);
        buttonPanel.add(stepButton);
        buttonPanel.add(pauseButton);

        add(buttonPanel, BorderLayout.NORTH);
    }

    private void openMaze() {
        var fileChooser = new JFileChooser();

        var result = fileChooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            mazePanel.setMaze(Maze.parse(new FileReader(fileChooser.getSelectedFile())));
        } catch (Exception e) {
            animateButton.setEnabled(false);
            stepButton.setEnabled(false);
            pauseButton.setEnabled(false);

            mazePanel.reset();
            mazePanel.repaint();

            statusPanel.setStatus("Unable to load maze.");
            statusPanel.setStep("");

            return;
        }

        mazePanel.reset();
        mazePanel.repaint();

        var start = Instant.now();
        var solved = mazePanel.getMaze().solve();
        var end = Instant.now();

        if (solved) {
            animateButton.setEnabled(true);
            stepButton.setEnabled(true);
            pauseButton.setEnabled(false);

            statusPanel.setStatus(String.format("Solved. Steps taken: %d. Time taken: %s.", mazePanel.getMaze().getHistory().size(), Duration.between(start, end)));
            statusPanel.setStep("Step: " + mazePanel.getStep());
        } else {
            animateButton.setEnabled(false);
            stepButton.setEnabled(false);
            pauseButton.setEnabled(false);

            statusPanel.setStatus("Unsolvable.");
            statusPanel.setStep("");
        }
    }

    private void animate() {
        stepButton.setEnabled(false);
        pauseButton.setEnabled(true);

        timer.start();
    }

    private void step() {
        pauseButton.setEnabled(false);

        increment();

        if (mazePanel.getStep() == mazePanel.getMaze().getHistory().size() - 1) {
            animateButton.setEnabled(false);
            stepButton.setEnabled(false);
        }
    }

    private void pause() {
        animateButton.setEnabled(true);
        stepButton.setEnabled(true);
        pauseButton.setEnabled(false);

        timer.stop();
    }

    private void tick() {
        increment();

        if (mazePanel.getStep() == mazePanel.getMaze().getHistory().size() - 1) {
            animateButton.setEnabled(false);
            pauseButton.setEnabled(false);

            timer.stop();
        }
    }

    private void increment() {
        if (mazePanel.getStep() < mazePanel.getMaze().getHistory().size() - 1) {
            mazePanel.step();
            mazePanel.repaint();
        }

        statusPanel.setStep("Step: " + mazePanel.getStep());
    }
}
