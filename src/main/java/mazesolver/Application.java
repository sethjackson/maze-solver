package mazesolver;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Application {
    void main() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(MainWindow::new);
    }
}
