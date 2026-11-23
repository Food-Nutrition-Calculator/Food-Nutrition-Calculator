package nutritrack.ui;

import nutritrack.model.FoodItem;
import nutritrack.model.Meal;
import nutritrack.model.MealItem;
import nutritrack.model.Nutrition;
import nutritrack.service.MealService;
import nutritrack.utils.DateUtils;
import nutritrack.utils.UIConstants;
import nutritrack.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Swing Graphical User Interface for Meal Creation & Management.
 * Allows users to compose meals with portion scaling, dynamic item addition,
 * live nutrient calculation, and transactional database saving.
 *
 * Owned and maintained by: Member 2 (Meal Management)
 */
public class MealCreationPanel extends JPanel {

    private final MealService mealService;
    private Meal activeMeal;

    // Form Components
    private JTextField txtMealName;
    private JTextField txtMealDate;
    private JComboBox<FoodItem> cmbFoods;
    private JTextField txtQuantity;
    private JLabel lblServingUnit;
    private JLabel lblBaseNutrition;
    private JLabel lblScaledPreview;

    // Buttons
    private JButton btnAddItem;
    private JButton btnRemoveItem;
    private JButton btnClearMeal;
    private JButton btnSaveMeal;
    private JButton btnRefreshFoods;

    // Table
    private JTable tblMealItems;
    private DefaultTableModel tableModel;

    // Live Summary Labels
    private JLabel lblTotalCalories;
    private JLabel lblTotalMacros;

    private static final String[] COLUMN_NAMES = {
        "Food Name", "Quantity", "Unit", "Calories (kcal)", "Protein (g)", "Carbs (g)", "Fats (g)"
    };

    /**
     * Default constructor initializing services and layout.
     */
    public MealCreationPanel() {
        this(new MealService());
    }

    /**
     * Constructor allowing dependency injection of MealService.
     */
    public MealCreationPanel(MealService mealService) {
        this.mealService = mealService;
        this.activeMeal = new Meal("Breakfast", LocalDate.now(), LocalTime.now().withNano(0));
        initializeUI();
        reloadAvailableFoods();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. Top Header Banner
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Center Content: Left Config Form + Right Active Meal Table
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(createFormPanel(), BorderLayout.WEST);
        centerPanel.add(createTableAndSummaryPanel(), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(39, 174, 96)); // Fresh Emerald Green
        header.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel title = new JLabel("Meal Builder & Nutrition Calculator");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Combine food items, customize portion sizes, and calculate live nutrient totals");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(new Color(230, 255, 240));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createFormPanel() {
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setPreferredSize(new Dimension(380, 0));
        formContainer.setBackground(UIConstants.PANEL_BACKGROUND);
        formContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));

        // Subtitle
        JLabel formTitle = new JLabel("Meal Details & Portion Input");
        formTitle.setFont(UIConstants.FONT_SUBTITLE);
        formTitle.setForeground(UIConstants.TEXT_PRIMARY);
        formContainer.add(formTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtMealName = new JTextField("Breakfast", 15);
        txtMealDate = new JTextField(LocalDate.now().toString(), 15);

        cmbFoods = new JComboBox<>();
        cmbFoods.setFont(UIConstants.FONT_BODY);
        cmbFoods.addActionListener(e -> updateSelectedFoodPreview());

        btnRefreshFoods = new JButton("↻");
        btnRefreshFoods.setToolTipText("Reload food items from database");
        btnRefreshFoods.addActionListener(e -> reloadAvailableFoods());

        JPanel foodComboPanel = new JPanel(new BorderLayout(5, 0));
        foodComboPanel.setOpaque(false);
        foodComboPanel.add(cmbFoods, BorderLayout.CENTER);
        foodComboPanel.add(btnRefreshFoods, BorderLayout.EAST);

        txtQuantity = new JTextField("100.0", 8);
        txtQuantity.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { updateSelectedFoodPreview(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { updateSelectedFoodPreview(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { updateSelectedFoodPreview(); }
        });

        lblServingUnit = new JLabel("grams");
        lblServingUnit.setFont(UIConstants.FONT_BOLD);
        lblServingUnit.setForeground(UIConstants.TEXT_MUTED);

        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        qtyPanel.setOpaque(false);
        qtyPanel.add(txtQuantity);
        qtyPanel.add(lblServingUnit);

        lblBaseNutrition = new JLabel("Base: Select a food item");
        lblBaseNutrition.setFont(UIConstants.FONT_METRIC_LABEL);
        lblBaseNutrition.setForeground(UIConstants.TEXT_MUTED);

        lblScaledPreview = new JLabel("Scaled: 0.0 kcal");
        lblScaledPreview.setFont(UIConstants.FONT_BOLD);
        lblScaledPreview.setForeground(new Color(39, 174, 96));

        int row = 0;
        addFormField(fieldsPanel, gbc, row++, "Meal Name*:", txtMealName);
        addFormField(fieldsPanel, gbc, row++, "Meal Date (YYYY-MM-DD):", txtMealDate);
        addFormField(fieldsPanel, gbc, row++, "Select Food*:", foodComboPanel);
        addFormField(fieldsPanel, gbc, row++, "Portion Quantity*:", qtyPanel);
        addFormField(fieldsPanel, gbc, row++, "Baseline:", lblBaseNutrition);
        addFormField(fieldsPanel, gbc, row++, "Live Portion Preview:", lblScaledPreview);

        formContainer.add(fieldsPanel, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(3, 1, 6, 6));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnAddItem = new JButton("+ Add Item to Meal");
        btnAddItem.setFont(UIConstants.FONT_BOLD);
        btnAddItem.setForeground(new Color(25, 111, 61));
        btnAddItem.addActionListener(e -> onAddMealItem());

        btnRemoveItem = new JButton("- Remove Selected Item");
        btnRemoveItem.setFont(UIConstants.FONT_BOLD);
        btnRemoveItem.setForeground(new Color(176, 58, 46));
        btnRemoveItem.setEnabled(false);
        btnRemoveItem.addActionListener(e -> onRemoveMealItem());

        btnClearMeal = new JButton("Clear Active Meal");
        btnClearMeal.setFont(UIConstants.FONT_BODY);
        btnClearMeal.addActionListener(e -> onClearMeal());

        actionPanel.add(btnAddItem);
        actionPanel.add(btnRemoveItem);
        actionPanel.add(btnClearMeal);

        formContainer.add(actionPanel, BorderLayout.SOUTH);
        return formContainer;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.FONT_BODY);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        field.setFont(UIConstants.FONT_BODY);
        panel.add(field, gbc);
    }

    private JPanel createTableAndSummaryPanel() {
        JPanel rightContainer = new JPanel(new BorderLayout(10, 10));
        rightContainer.setBackground(UIConstants.PANEL_BACKGROUND);
        rightContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));

        // Title
        JLabel tableTitle = new JLabel("Items in Current Meal");
        tableTitle.setFont(UIConstants.FONT_SUBTITLE);
        tableTitle.setForeground(UIConstants.TEXT_PRIMARY);
        rightContainer.add(tableTitle, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tblMealItems = new JTable(tableModel);
        tblMealItems.setFont(UIConstants.FONT_BODY);
        tblMealItems.setRowHeight(24);
        tblMealItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMealItems.getTableHeader().setFont(UIConstants.FONT_BOLD);
        tblMealItems.getTableHeader().setBackground(new Color(235, 240, 245));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < tblMealItems.getColumnCount(); i++) {
            tblMealItems.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        tblMealItems.getSelectionModel().addListSelectionListener(e -> {
            btnRemoveItem.setEnabled(tblMealItems.getSelectedRow() != -1);
        });

        JScrollPane scrollPane = new JScrollPane(tblMealItems);
        rightContainer.add(scrollPane, BorderLayout.CENTER);

        // Bottom Totals & Save Card
        JPanel bottomCard = new JPanel(new BorderLayout(15, 10));
        bottomCard.setBackground(new Color(245, 250, 245));
        bottomCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 230, 210), 1),
            new EmptyBorder(12, 15, 12, 15)
        ));

        JPanel totalsPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        totalsPanel.setOpaque(false);

        lblTotalCalories = new JLabel("Total Calories: 0.0 kcal");
        lblTotalCalories.setFont(UIConstants.FONT_METRIC_VALUE);
        lblTotalCalories.setForeground(new Color(39, 174, 96));

        lblTotalMacros = new JLabel("Protein: 0.0g  |  Carbohydrates: 0.0g  |  Fats: 0.0g");
        lblTotalMacros.setFont(UIConstants.FONT_BOLD);
        lblTotalMacros.setForeground(UIConstants.TEXT_PRIMARY);

        totalsPanel.add(lblTotalCalories);
        totalsPanel.add(lblTotalMacros);

        btnSaveMeal = new JButton("Save Complete Meal to Database");
        btnSaveMeal.setFont(UIConstants.FONT_TITLE);
        btnSaveMeal.setForeground(new Color(25, 111, 61));
        btnSaveMeal.setBackground(Color.WHITE);
        btnSaveMeal.setPreferredSize(new Dimension(280, 50));
        btnSaveMeal.addActionListener(e -> onSaveMeal());

        bottomCard.add(totalsPanel, BorderLayout.CENTER);
        bottomCard.add(btnSaveMeal, BorderLayout.EAST);

        rightContainer.add(bottomCard, BorderLayout.SOUTH);
        return rightContainer;
    }

    // --- Interactive Action Handlers ---

    private void onAddMealItem() {
        try {
            FoodItem selectedFood = (FoodItem) cmbFoods.getSelectedItem();
            if (selectedFood == null) {
                JOptionPane.showMessageDialog(this, "No food item selected. Select or add a food first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double quantity = ValidationUtils.parsePositiveDouble(txtQuantity.getText(), "Portion Quantity");
            mealService.addFoodToMeal(activeMeal, selectedFood, quantity);

            refreshMealTable();
            txtQuantity.setText("100.0");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onRemoveMealItem() {
        int row = tblMealItems.getSelectedRow();
        if (row != -1) {
            MealItem item = activeMeal.getMealItems().get(row);
            mealService.removeFoodFromMeal(activeMeal, item.getFoodItem().getFoodId());
            refreshMealTable();
            btnRemoveItem.setEnabled(false);
        }
    }

    private void onClearMeal() {
        activeMeal.clearItems();
        refreshMealTable();
    }

    private void onSaveMeal() {
        try {
            String name = txtMealName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a meal name (e.g., Breakfast, Lunch).", "Meal Name Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            activeMeal.setMealName(name);

            // Parse Date
            try {
                LocalDate date = LocalDate.parse(txtMealDate.getText().trim());
                activeMeal.setMealDate(date);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD (e.g. " + LocalDate.now() + ")", "Date Format Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            activeMeal.setMealTime(LocalTime.now().withNano(0));

            boolean success = mealService.commitMeal(activeMeal);
            if (success) {
                Nutrition total = activeMeal.calculateTotalNutrition();
                JOptionPane.showMessageDialog(this,
                    "Meal '" + activeMeal.getMealName() + "' saved successfully!\n" +
                    "Total Calories: " + total.getTotalCalories() + " kcal\n" +
                    "Protein: " + total.getTotalProtein() + "g | Carbs: " + total.getTotalCarbs() + "g | Fats: " + total.getTotalFats() + "g",
                    "Meal Saved", JOptionPane.INFORMATION_MESSAGE);

                // Reset for next meal
                activeMeal = new Meal("Lunch", LocalDate.now(), LocalTime.now().withNano(0));
                txtMealName.setText("Lunch");
                refreshMealTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save meal. Check MySQL connection or verify food items.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateSelectedFoodPreview() {
        FoodItem food = (FoodItem) cmbFoods.getSelectedItem();
        if (food != null) {
            lblServingUnit.setText(food.getServingUnit());
            lblBaseNutrition.setText(String.format("Base: %.0f %s = %.1f kcal (P: %.1fg, C: %.1fg, F: %.1fg)",
                food.getDefaultServingSize(), food.getServingUnit(), food.getCalories(),
                food.getProtein(), food.getCarbohydrates(), food.getFats()));

            try {
                double qty = Double.parseDouble(txtQuantity.getText().trim());
                if (qty > 0) {
                    Nutrition scaled = Nutrition.scale(food, qty);
                    lblScaledPreview.setText(String.format("Scaled: %.1f kcal (P: %.1fg, C: %.1fg, F: %.1fg)",
                        scaled.getTotalCalories(), scaled.getTotalProtein(), scaled.getTotalCarbs(), scaled.getTotalFats()));
                }
            } catch (Exception ignored) {
                lblScaledPreview.setText("Scaled: --");
            }
        }
    }

    public void reloadAvailableFoods() {
        cmbFoods.removeAllItems();
        List<FoodItem> foods = mealService.getAvailableFoods();
        for (FoodItem food : foods) {
            cmbFoods.addItem(food);
        }
        updateSelectedFoodPreview();
    }

    private void refreshMealTable() {
        tableModel.setRowCount(0);
        for (MealItem item : activeMeal.getMealItems()) {
            tableModel.addRow(new Object[]{
                item.getFoodItem().getFoodName(),
                item.getQuantity(),
                item.getFoodItem().getServingUnit(),
                item.getCalculatedCalories(),
                item.getCalculatedProtein(),
                item.getCalculatedCarbs(),
                item.getCalculatedFats()
            });
        }

        Nutrition total = activeMeal.calculateTotalNutrition();
        lblTotalCalories.setText(String.format("Total Calories: %.1f kcal", total.getTotalCalories()));
        lblTotalMacros.setText(String.format("Protein: %.1fg  |  Carbohydrates: %.1fg  |  Fats: %.1fg",
            total.getTotalProtein(), total.getTotalCarbs(), total.getTotalFats()));
    }

    /**
     * Standalone runner allowing Member 2 to test this panel in an independent window.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }

            JFrame frame = new JFrame("NutriTrack - Meal Management Module (Member 2 Test)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 650);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new MealCreationPanel());
            frame.setVisible(true);
        });
    }
}
