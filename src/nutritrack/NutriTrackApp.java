package nutritrack;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import nutritrack.ui.MainFrame;

public class NutriTrackApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                System.err.println("Could not set system look and feel: "
                        + e.getMessage());
            }

            new MainFrame().setVisible(true);
        });
    }
}
