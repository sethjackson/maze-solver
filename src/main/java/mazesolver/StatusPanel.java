package mazesolver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatusPanel extends JPanel {
    private final JLabel status = new JLabel();
    private final JLabel step = new JLabel();

    public StatusPanel(Component parent) {
        setPreferredSize(new Dimension(parent.getWidth(), 26));
        setBorder(BorderFactory.createEtchedBorder());
        setLayout(new BorderLayout());

        status.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        step.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        add(status, BorderLayout.WEST);
        add(step, BorderLayout.EAST);
    }

    public void setStatus(String text) {
        status.setText(text);
    }

    public void setStep(String text) {
        step.setText(text);
    }
}
