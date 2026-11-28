package nutritrack;

import java.io.IOException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import nutritrack.ui.MainFrame;
import nutritrack.utils.UIConstants;

public class NutriTrackApp {

    public static void main(String[] args) {
        try {
            NutriTrackServer.main(args);
        } catch (IOException e) {
            System.err.println("Could not start the NutriTrack web server: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                System.err.println("Could not set system look and feel: "
                        + e.getMessage());
            }

            UIManager.put("defaultFont", UIConstants.FONT_BODY);
            UIManager.put("Label.font", UIConstants.FONT_BODY);
            UIManager.put("Button.font", UIConstants.FONT_BOLD);
            UIManager.put("TextField.font", UIConstants.FONT_BODY);
            UIManager.put("ComboBox.font", UIConstants.FONT_BODY);
            UIManager.put("CheckBox.font", UIConstants.FONT_BODY);
            UIManager.put("Table.font", UIConstants.FONT_BODY);
            UIManager.put("TableHeader.font", UIConstants.FONT_BOLD);
            UIManager.put("TabbedPane.font", UIConstants.FONT_BODY);

            new MainFrame().setVisible(true);
        });
    }
}
