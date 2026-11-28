package nutritrack.ui;

import nutritrack.model.Nutrition;
import nutritrack.service.NutritionService;
import nutritrack.service.HistoryService;
import nutritrack.model.Meal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import nutritrack.utils.UIConstants;

/**
 * Swing GUI Panel for Member 3 (Nutrition Dashboard).
 */
public class NutritionDashboardPanel extends JPanel {

    private final NutritionService nutritionService;
    private final HistoryService historyService = new HistoryService();
    private JTextField dateField;
    private JLabel caloriesValue, proteinValue, carbsValue, fatsValue;
    private JLabel statusLabel;
    private JLabel proteinPctLabel, carbsPctLabel, fatsPctLabel;
    private JProgressBar proteinBar, carbsBar, fatsBar;
    private DefaultTableModel tableModel;

    public NutritionDashboardPanel() {
        this.nutritionService = new NutritionService();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        header.setBackground(UIConstants.PRIMARY_COLOR);
        JLabel title = new JLabel("📊  Nutrition Dashboard");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Center Panel
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(UIConstants.BACKGROUND_COLOR);

        // Date Bar
        JPanel dateBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        dateBar.setBackground(UIConstants.BACKGROUND_COLOR);
        dateBar.setFont(UIConstants.FONT_BODY);
        dateBar.add(new JLabel("Date:"));
        dateField = new JTextField(LocalDate.now().toString(), 10);
        dateBar.add(dateField);
        JButton loadBtn = new JButton("Load Summary");
        loadBtn.addActionListener(e -> loadData());
        dateBar.add(loadBtn);
        center.add(dateBar);

        // 4 KPI Cards
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 10, 0));
        kpiRow.setBackground(UIConstants.BACKGROUND_COLOR);
        caloriesValue = new JLabel("0.0 kcal");
        proteinValue  = new JLabel("0.0 g");
        carbsValue    = new JLabel("0.0 g");
        fatsValue     = new JLabel("0.0 g");

        kpiRow.add(createCard("Total Calories", caloriesValue));
        kpiRow.add(createCard("Protein", proteinValue));
        kpiRow.add(createCard("Carbohydrates", carbsValue));
        kpiRow.add(createCard("Fats", fatsValue));
        center.add(kpiRow);
        center.add(Box.createVerticalStrut(10));

        // Macro % Distribution Bars
        JPanel macroBox = new JPanel(new GridLayout(3, 1, 5, 5));
        macroBox.setBackground(Color.WHITE);
        macroBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
                BorderFactory.createTitledBorder("Macronutrient Distribution (4-4-9 Standard)")));

        proteinPctLabel = new JLabel("Protein: 0%");
        proteinBar = new JProgressBar(0, 100);
        proteinBar.setForeground(UIConstants.PRIMARY_COLOR);

        carbsPctLabel = new JLabel("Carbs: 0%");
        carbsBar = new JProgressBar(0, 100);
        carbsBar.setForeground(UIConstants.PRIMARY_COLOR);

        fatsPctLabel = new JLabel("Fats: 0%");
        fatsBar = new JProgressBar(0, 100);
        fatsBar.setForeground(UIConstants.PRIMARY_COLOR);

        macroBox.add(createMacroRow(proteinPctLabel, proteinBar));
        macroBox.add(createMacroRow(carbsPctLabel, carbsBar));
        macroBox.add(createMacroRow(fatsPctLabel, fatsBar));
        center.add(macroBox);
        center.add(Box.createVerticalStrut(10));

        // Meals Table
        String[] cols = {"Meal", "Calories (kcal)", "Protein (g)", "Carbs (g)", "Fats (g)"};
        tableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(tableModel);
        table.setFont(UIConstants.FONT_BODY);
        table.setRowHeight(32);
        table.getTableHeader().setFont(UIConstants.FONT_BOLD);
        table.getTableHeader().setBackground(UIConstants.BACKGROUND_COLOR);
        center.add(new JScrollPane(table));

        add(center, BorderLayout.CENTER);
        statusLabel = new JLabel("Ready. Select a date and calculate its saved meals.");
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createCard(String title, JLabel val) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setBackground(Color.WHITE);
        p.setBorder(new LineBorder(UIConstants.BORDER_COLOR, 1));
        JLabel t = new JLabel(title);
        t.setFont(UIConstants.FONT_METRIC_LABEL);
        t.setForeground(UIConstants.TEXT_MUTED);
        val.setFont(UIConstants.FONT_METRIC_VALUE);
        val.setForeground(UIConstants.PRIMARY_DARK);
        p.add(t);
        p.add(val);
        return p;
    }

    private JPanel createMacroRow(JLabel lbl, JProgressBar bar) {
        JPanel r = new JPanel(new BorderLayout(8, 0));
        r.setBackground(Color.WHITE);
        lbl.setFont(UIConstants.FONT_BODY);
        lbl.setPreferredSize(new Dimension(100, 20));
        r.add(lbl, BorderLayout.WEST);
        r.add(bar, BorderLayout.CENTER);
        return r;
    }

    public void refreshDashboard() {
        loadData();
    }

    private void loadData() {
        final LocalDate date;
        try {
            date = LocalDate.parse(dateField.getText().trim());
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Enter a date in YYYY-MM-DD format.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            List<Meal> meals = historyService.getDailyMealHistory(date);
            Nutrition tot = nutritionService.calculateDailyNutrition(meals);
            Map<String, Double> pcts = nutritionService.calculateMacronutrientPercentages(tot);

        caloriesValue.setText(String.format("%.1f kcal", tot.getTotalCalories()));
        proteinValue.setText(String.format("%.1f g", tot.getTotalProtein()));
        carbsValue.setText(String.format("%.1f g", tot.getTotalCarbs()));
        fatsValue.setText(String.format("%.1f g", tot.getTotalFats()));

        proteinBar.setValue(pcts.get("PROTEIN_PCT").intValue());
        proteinPctLabel.setText("Protein: " + pcts.get("PROTEIN_PCT") + "%");

        carbsBar.setValue(pcts.get("CARBS_PCT").intValue());
        carbsPctLabel.setText("Carbs: " + pcts.get("CARBS_PCT") + "%");

        fatsBar.setValue(pcts.get("FATS_PCT").intValue());
        fatsPctLabel.setText("Fats: " + pcts.get("FATS_PCT") + "%");

            tableModel.setRowCount(0);
            for (Meal meal : meals) {
                Nutrition mealNutrition = nutritionService.calculateMealNutrition(meal);
                tableModel.addRow(new Object[]{meal.getMealName(), mealNutrition.getTotalCalories(), mealNutrition.getTotalProtein(), mealNutrition.getTotalCarbs(), mealNutrition.getTotalFats()});
            }
            statusLabel.setText(meals.isEmpty()
                    ? "No meals saved for " + date + ". Add and save a meal in Meal Builder."
                    : "Loaded " + meals.size() + " saved meal(s) for " + date + ".");
        } catch (RuntimeException ex) {
            tableModel.setRowCount(0);
            statusLabel.setText("Dashboard could not load MySQL data: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("NutriTrack - Nutrition Dashboard");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(750, 550);
            f.setLocationRelativeTo(null);
            f.setContentPane(new NutritionDashboardPanel());
            f.setVisible(true);
        });
    }
}
