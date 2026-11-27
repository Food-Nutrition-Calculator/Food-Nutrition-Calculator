package nutritrack.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Swing GUI Panel for Member 2 – Meal Management & Meal Builder.
 * Allows users to select food items, enter serving quantities,
 * scale nutrients dynamically, and assemble complete meals.
 *
 * Owned and maintained by: Member 2
 */
public class MealCreationPanel extends JPanel {

    private JTextField mealNameField;
    private JComboBox<String> foodDropdown;
    private JTextField quantityField;
    private DefaultTableModel tableModel;
    private JLabel totalCaloriesLabel;
    private JLabel totalMacrosLabel;

    // In-memory food catalog for immediate demo & standalone run
    private final List<CatalogFood> foodCatalog = new ArrayList<>();
    private final List<ActiveMealItem> currentMealItems = new ArrayList<>();

    public MealCreationPanel() {
        initCatalog();
        initUI();
    }

    private void initCatalog() {
        foodCatalog.add(new CatalogFood(1, "Rolled Oats", "grams", 100.0, 389.0, 16.9, 66.3, 6.9));
        foodCatalog.add(new CatalogFood(2, "Whole Milk", "ml", 100.0, 61.0, 3.2, 4.8, 3.3));
        foodCatalog.add(new CatalogFood(3, "Banana", "grams", 100.0, 89.0, 1.1, 22.8, 0.3));
        foodCatalog.add(new CatalogFood(4, "Boiled Egg", "unit", 1.0, 78.0, 6.3, 0.6, 5.3));
        foodCatalog.add(new CatalogFood(5, "Grilled Chicken", "grams", 100.0, 165.0, 31.0, 0.0, 3.6));
        foodCatalog.add(new CatalogFood(6, "White Rice", "grams", 100.0, 130.0, 2.7, 28.2, 0.3));
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 245));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // 1. Top Header Banner
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        header.setBackground(new Color(27, 94, 32));
        JLabel title = new JLabel("🍱  Meal Builder & Portion Scaling (Member 2)");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // 2. Control Bar (Meal Name, Food Dropdown, Quantity Input)
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 230, 201), 2),
            new EmptyBorder(10, 15, 10, 15)
        ));

        // Row 1: Meal Name
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.setBackground(Color.WHITE);
        row1.add(new JLabel("Meal Name:"));
        mealNameField = new JTextField("Breakfast", 12);
        row1.add(mealNameField);

        // Row 2: Food Select & Serving Quantity
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.setBackground(Color.WHITE);
        row2.add(new JLabel("Select Food:"));

        foodDropdown = new JComboBox<>();
        for (CatalogFood f : foodCatalog) {
            foodDropdown.addItem(f.name + " (" + f.defaultServing + f.unit + " = " + f.calories + " kcal)");
        }
        row2.add(foodDropdown);

        row2.add(new JLabel("Quantity (g/unit):"));
        quantityField = new JTextField("100", 6);
        row2.add(quantityField);

        JButton addBtn = new JButton("+ Add to Meal");
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        addBtn.setForeground(new Color(27, 94, 32));
        addBtn.addActionListener(e -> addItemToMeal());
        row2.add(addBtn);

        JButton clearBtn = new JButton("Clear Meal");
        clearBtn.setForeground(new Color(183, 28, 28));
        clearBtn.addActionListener(e -> clearMeal());
        row2.add(clearBtn);

        controlPanel.add(row1);
        controlPanel.add(row2);
        add(controlPanel, BorderLayout.NORTH);

        // 3. Center Table (Active Meal Items)
        String[] columns = {"Food Name", "Portion Qty", "Scaled Calories (kcal)", "Protein (g)", "Carbs (g)", "Fats (g)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 4. Bottom Total Bar
        JPanel footer = new JPanel(new BorderLayout(10, 5));
        footer.setBackground(new Color(232, 245, 233));
        footer.setBorder(new EmptyBorder(10, 15, 10, 15));

        totalCaloriesLabel = new JLabel("Total Calories: 0.0 kcal");
        totalCaloriesLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalCaloriesLabel.setForeground(new Color(27, 94, 32));

        totalMacrosLabel = new JLabel("Protein: 0.0g  |  Carbs: 0.0g  |  Fats: 0.0g");
        totalMacrosLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        totalMacrosLabel.setForeground(new Color(55, 71, 79));

        footer.add(totalCaloriesLabel, BorderLayout.WEST);
        footer.add(totalMacrosLabel, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
    }

    private void addItemToMeal() {
        try {
            int selectedIndex = foodDropdown.getSelectedIndex();
            if (selectedIndex < 0) return;

            CatalogFood food = foodCatalog.get(selectedIndex);
            double qty = Double.parseDouble(quantityField.getText().trim());
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check duplicate and aggregate
            boolean exists = false;
            for (ActiveMealItem item : currentMealItems) {
                if (item.food.id == food.id) {
                    item.quantity += qty;
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                currentMealItems.add(new ActiveMealItem(food, qty));
            }

            refreshTable();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearMeal() {
        currentMealItems.clear();
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        double totalCal = 0.0, totalP = 0.0, totalC = 0.0, totalF = 0.0;

        for (ActiveMealItem item : currentMealItems) {
            double ratio = item.quantity / item.food.defaultServing;
            double cal = item.food.calories * ratio;
            double p   = item.food.protein * ratio;
            double c   = item.food.carbs * ratio;
            double f   = item.food.fats * ratio;

            totalCal += cal;
            totalP   += p;
            totalC   += c;
            totalF   += f;

            tableModel.addRow(new Object[]{
                item.food.name,
                item.quantity + " " + item.food.unit,
                String.format("%.1f", cal),
                String.format("%.1f", p),
                String.format("%.1f", c),
                String.format("%.1f", f)
            });
        }

        totalCaloriesLabel.setText(String.format("Total Calories: %.1f kcal", totalCal));
        totalMacrosLabel.setText(String.format("Protein: %.1fg  |  Carbs: %.1fg  |  Fats: %.1fg", totalP, totalC, totalF));
    }

    // --- Data Holder Classes ---
    static class CatalogFood {
        int id;
        String name, unit;
        double defaultServing, calories, protein, carbs, fats;

        CatalogFood(int id, String name, String unit, double defaultServing, double calories, double protein, double carbs, double fats) {
            this.id = id;
            this.name = name;
            this.unit = unit;
            this.defaultServing = defaultServing;
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fats = fats;
        }
    }

    static class ActiveMealItem {
        CatalogFood food;
        double quantity;

        ActiveMealItem(CatalogFood food, double quantity) {
            this.food = food;
            this.quantity = quantity;
        }
    }

    // Standalone Runner for Member 2
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            JFrame frame = new JFrame("NutriTrack – Meal Creation Panel (Member 2)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(850, 550);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new MealCreationPanel());
            frame.setVisible(true);
        });
    }
}