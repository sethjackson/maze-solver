package mazesolver;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Program {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(MainWindow::new);
    }
}
