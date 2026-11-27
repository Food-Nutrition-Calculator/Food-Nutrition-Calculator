package nutritrack.ui;

import nutritrack.model.Nutrition;
import nutritrack.service.NutritionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Swing GUI Panel for Member 3 (Nutrition Dashboard).
 */
public class NutritionDashboardPanel extends JPanel {

    private final NutritionService nutritionService;
    private JTextField dateField;
    private JLabel caloriesValue, proteinValue, carbsValue, fatsValue;
    private JLabel proteinPctLabel, carbsPctLabel, fatsPctLabel;
    private JProgressBar proteinBar, carbsBar, fatsBar;
    private DefaultTableModel tableModel;

    public NutritionDashboardPanel() {
        this.nutritionService = new NutritionService();
        initUI();
        loadDemoData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 245));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        header.setBackground(new Color(27, 94, 32));
        JLabel title = new JLabel("📊  Nutrition Dashboard (Member 3)");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Center Panel
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(new Color(245, 250, 245));

        // Date Bar
        JPanel dateBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        dateBar.setBackground(new Color(245, 250, 245));
        dateBar.add(new JLabel("Date:"));
        dateField = new JTextField(LocalDate.now().toString(), 10);
        dateBar.add(dateField);
        JButton loadBtn = new JButton("Calculate Summary");
        loadBtn.addActionListener(e -> loadDemoData());
        dateBar.add(loadBtn);
        center.add(dateBar);

        // 4 KPI Cards
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 10, 0));
        kpiRow.setBackground(new Color(245, 250, 245));
        caloriesValue = new JLabel("0.0 kcal");
        proteinValue  = new JLabel("0.0 g");
        carbsValue    = new JLabel("0.0 g");
        fatsValue     = new JLabel("0.0 g");

        kpiRow.add(createCard("🔥 Total Calories", caloriesValue, new Color(27, 94, 32)));
        kpiRow.add(createCard("💪 Protein", proteinValue, new Color(21, 101, 192)));
        kpiRow.add(createCard("🌾 Carbohydrates", carbsValue, new Color(230, 81, 0)));
        kpiRow.add(createCard("🥑 Fats", fatsValue, new Color(106, 27, 154)));
        center.add(kpiRow);
        center.add(Box.createVerticalStrut(10));

        // Macro % Distribution Bars
        JPanel macroBox = new JPanel(new GridLayout(3, 1, 5, 5));
        macroBox.setBackground(Color.WHITE);
        macroBox.setBorder(BorderFactory.createTitledBorder("Macronutrient Distribution (4-4-9 Standard)"));

        proteinPctLabel = new JLabel("Protein: 0%");
        proteinBar = new JProgressBar(0, 100);
        proteinBar.setForeground(new Color(21, 101, 192));

        carbsPctLabel = new JLabel("Carbs: 0%");
        carbsBar = new JProgressBar(0, 100);
        carbsBar.setForeground(new Color(230, 81, 0));

        fatsPctLabel = new JLabel("Fats: 0%");
        fatsBar = new JProgressBar(0, 100);
        fatsBar.setForeground(new Color(106, 27, 154));

        macroBox.add(createMacroRow(proteinPctLabel, proteinBar));
        macroBox.add(createMacroRow(carbsPctLabel, carbsBar));
        macroBox.add(createMacroRow(fatsPctLabel, fatsBar));
        center.add(macroBox);
        center.add(Box.createVerticalStrut(10));

        // Meals Table
        String[] cols = {"Meal", "Calories (kcal)", "Protein (g)", "Carbs (g)", "Fats (g)"};
        tableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(tableModel);
        center.add(new JScrollPane(table));

        add(center, BorderLayout.CENTER);
        add(new JLabel("Ready. Live Nutrition Calculation Engine Active."), BorderLayout.SOUTH);
    }

    private JPanel createCard(String title, JLabel val, Color col) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setBackground(Color.WHITE);
        p.setBorder(new LineBorder(new Color(200, 230, 201), 2));
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.PLAIN, 12));
        val.setFont(new Font("SansSerif", Font.BOLD, 18));
        val.setForeground(col);
        p.add(t);
        p.add(val);
        return p;
    }

    private JPanel createMacroRow(JLabel lbl, JProgressBar bar) {
        JPanel r = new JPanel(new BorderLayout(8, 0));
        r.setBackground(Color.WHITE);
        lbl.setPreferredSize(new Dimension(100, 20));
        r.add(lbl, BorderLayout.WEST);
        r.add(bar, BorderLayout.CENTER);
        return r;
    }

    private void loadDemoData() {
        Nutrition b = Nutrition.scale(78.0, 6.3, 0.6, 5.3, 1.0, 2.0); // 2 eggs
        Nutrition l = Nutrition.scale(165.0, 31.0, 0.0, 3.6, 100.0, 200.0); // 200g chicken
        Nutrition tot = nutritionService.calculateDailyTotal(List.of(b, l));
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
        tableModel.addRow(new Object[]{"Breakfast (2 Boiled Eggs)", b.getTotalCalories(), b.getTotalProtein(), b.getTotalCarbs(), b.getTotalFats()});
        tableModel.addRow(new Object[]{"Lunch (200g Grilled Chicken)", l.getTotalCalories(), l.getTotalProtein(), l.getTotalCarbs(), l.getTotalFats()});
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("NutriTrack – Member 3 Nutrition Dashboard");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(750, 550);
            f.setLocationRelativeTo(null);
            f.setContentPane(new NutritionDashboardPanel());
            f.setVisible(true);
        });
    }
}