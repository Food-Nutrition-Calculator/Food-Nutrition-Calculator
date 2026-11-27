package nutritrack.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import nutritrack.utils.UIConstants;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("NutriTrack - Food & Nutrition Calculator");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        getContentPane().setBackground(UIConstants.BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Food Catalog", new FoodManagementPanel());
        tabs.addTab("Meal History", new MealHistoryPanel());

        add(tabs, BorderLayout.CENTER);
    }
}