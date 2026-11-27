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
        FoodManagementPanel foodPanel = new FoodManagementPanel();
        MealCreationPanel mealPanel = new MealCreationPanel();
        NutritionDashboardPanel nutritionPanel = new NutritionDashboardPanel();
        MealHistoryPanel historyPanel = new MealHistoryPanel();

        mealPanel.addPropertyChangeListener("mealSaved", event -> {
            nutritionPanel.refreshDashboard();
            historyPanel.refreshHistory();
        });
        tabs.addTab("Food Catalog", foodPanel);
        tabs.addTab("Meal Builder", mealPanel);
        tabs.addTab("Nutrition Dashboard", nutritionPanel);
        tabs.addTab("Meal History", historyPanel);
        tabs.addChangeListener(event -> {
            if (tabs.getSelectedComponent() == mealPanel) mealPanel.refreshCatalog();
        });

        add(tabs, BorderLayout.CENTER);
    }
}
