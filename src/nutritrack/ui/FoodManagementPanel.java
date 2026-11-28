package nutritrack.ui;

import nutritrack.model.FoodItem;
import nutritrack.service.FoodService;
import nutritrack.utils.UIConstants;
import nutritrack.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Swing Graphical User Interface for Food Management.
 * Provides food item registration, updates, deletion, catalog display,
 * real-time search, and multi-attribute sorting.
 *
 * Owned and maintained by: Member 1 (Food Management)
 */
public class FoodManagementPanel extends JPanel {

    private final FoodService foodService;

    // Form Input Fields
    private JTextField txtFoodId;
    private JTextField txtFoodName;
    private JComboBox<String> cmbServingUnit;
    private JTextField txtDefaultServingSize;
    private JTextField txtCalories;
    private JTextField txtProtein;
    private JTextField txtCarbs;
    private JTextField txtFats;

    // Action Buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    // Search and Sort Controls
    private JTextField txtSearch;
    private JComboBox<String> cmbSortCriterion;
    private JCheckBox chkAscending;

    // Catalog Table
    private JTable tblFoods;
    private DefaultTableModel tableModel;

    // Table Column Headers
    private static final String[] COLUMN_NAMES = {
        "ID", "Food Name", "Unit", "Serving Size", "Calories (kcal)", "Protein (g)", "Carbs (g)", "Fats (g)"
    };

    /**
     * Default constructor initializing services and UI layout.
     */
    public FoodManagementPanel() {
        this(new FoodService());
    }

    /**
     * Constructor allowing dependency injection of FoodService.
     */
    public FoodManagementPanel(FoodService foodService) {
        this.foodService = foodService;
        initializeUI();
        loadFoodTableData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. Top Header Banner
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Center Content Split: Form Panel (West) + Table & Search (Center)
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setOpaque(false);
        contentPanel.add(createFormPanel(), BorderLayout.WEST);
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.PRIMARY_COLOR);
        header.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel title = new JLabel("Food Catalog Management");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Manage nutritional food profiles, portions, and baseline macros");
        subtitle.setFont(UIConstants.FONT_BODY);
        subtitle.setForeground(new Color(232, 237, 242));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createFormPanel() {
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setPreferredSize(new Dimension(340, 0));
        formContainer.setBackground(UIConstants.PANEL_BACKGROUND);
        formContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel formTitle = new JLabel("Food Item Details");
        formTitle.setFont(UIConstants.FONT_SUBTITLE);
        formTitle.setForeground(UIConstants.TEXT_PRIMARY);
        formContainer.add(formTitle, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Hidden / Read-only ID field
        txtFoodId = new JTextField();
        txtFoodId.setEditable(false);
        txtFoodId.setBackground(new Color(245, 245, 245));
        txtFoodId.setVisible(false); // keep invisible or for internal tracking

        txtFoodName = new JTextField(15);
        cmbServingUnit = new JComboBox<>(new String[]{"grams", "ml", "cup", "pieces", "tablespoon"});
        cmbServingUnit.setEditable(true);
        txtDefaultServingSize = new JTextField("100.0", 15);
        txtCalories = new JTextField(15);
        txtProtein = new JTextField(15);
        txtCarbs = new JTextField(15);
        txtFats = new JTextField(15);

        int row = 0;
        addFormField(fieldsPanel, gbc, row++, "Food Name*:", txtFoodName);
        addFormField(fieldsPanel, gbc, row++, "Serving Unit:", cmbServingUnit);
        addFormField(fieldsPanel, gbc, row++, "Default Serving Size*:", txtDefaultServingSize);
        addFormField(fieldsPanel, gbc, row++, "Calories (kcal)*:", txtCalories);
        addFormField(fieldsPanel, gbc, row++, "Protein (g)*:", txtProtein);
        addFormField(fieldsPanel, gbc, row++, "Carbohydrates (g)*:", txtCarbs);
        addFormField(fieldsPanel, gbc, row++, "Fats (g)*:", txtFats);

        formContainer.add(fieldsPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnAdd = new JButton("Add Food");
        btnAdd.setFont(UIConstants.FONT_BOLD);
        btnAdd.setForeground(UIConstants.PRIMARY_DARK);
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(e -> onAddFood());

        btnUpdate = new JButton("Update Food");
        btnUpdate.setFont(UIConstants.FONT_BOLD);
        btnUpdate.setForeground(UIConstants.PRIMARY_DARK);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e -> onUpdateFood());

        btnDelete = new JButton("Delete Food");
        btnDelete.setFont(UIConstants.FONT_BOLD);
        btnDelete.setForeground(UIConstants.PRIMARY_DARK);
        btnDelete.setFocusPainted(false);
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> onDeleteFood());

        btnClear = new JButton("Clear Form");
        btnClear.setFont(UIConstants.FONT_BOLD);
        btnClear.setForeground(UIConstants.PRIMARY_DARK);
        btnClear.setFocusPainted(false);
        btnClear.addActionListener(e -> clearFormFields());

        buttonsPanel.add(btnAdd);
        buttonsPanel.add(btnUpdate);
        buttonsPanel.add(btnDelete);
        buttonsPanel.add(btnClear);

        formContainer.add(buttonsPanel, BorderLayout.SOUTH);
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

    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel(new BorderLayout(10, 10));
        tableContainer.setBackground(UIConstants.PANEL_BACKGROUND);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // Search & Filter Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setOpaque(false);

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(UIConstants.FONT_BOLD);
        txtSearch = new JTextField(12);
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { onFilterOrSort(); }
            @Override public void removeUpdate(DocumentEvent e) { onFilterOrSort(); }
            @Override public void changedUpdate(DocumentEvent e) { onFilterOrSort(); }
        });

        JLabel lblSort = new JLabel("Sort By:");
        lblSort.setFont(UIConstants.FONT_BOLD);
        cmbSortCriterion = new JComboBox<>(new String[]{"Name", "Calories", "Protein", "Carbs", "Fats"});
        cmbSortCriterion.addActionListener(e -> onFilterOrSort());

        chkAscending = new JCheckBox("Ascending", true);
        chkAscending.setOpaque(false);
        chkAscending.setFont(UIConstants.FONT_BODY);
        chkAscending.addActionListener(e -> onFilterOrSort());

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(UIConstants.FONT_BODY);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadFoodTableData();
        });

        toolbar.add(lblSearch);
        toolbar.add(txtSearch);
        toolbar.add(lblSort);
        toolbar.add(cmbSortCriterion);
        toolbar.add(chkAscending);
        toolbar.add(btnRefresh);

        tableContainer.add(toolbar, BorderLayout.NORTH);

        // Catalog Table
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return false; // Prevent direct editing inside cells
            }
        };

        tblFoods = new JTable(tableModel);
        tblFoods.setFont(UIConstants.FONT_BODY);
        tblFoods.setRowHeight(32);
        tblFoods.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblFoods.getTableHeader().setFont(UIConstants.FONT_BOLD);
        tblFoods.getTableHeader().setBackground(UIConstants.BACKGROUND_COLOR);

        // Center align numerical columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 2; i < tblFoods.getColumnCount(); i++) {
            tblFoods.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Selection Listener: clicking row populates form
        tblFoods.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblFoods.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblFoods);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        return tableContainer;
    }

    // --- Action Handler Methods ---

    private void onAddFood() {
        try {
            FoodItem food = extractFoodItemFromForm();
            boolean success = foodService.addFood(food);
            if (success) {
                JOptionPane.showMessageDialog(this, "Food item '" + food.getFoodName() + "' added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFormFields();
                loadFoodTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Could not add food item. Verify database connection or check if food name already exists.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onUpdateFood() {
        try {
            if (txtFoodId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a food item from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int foodId = Integer.parseInt(txtFoodId.getText().trim());
            FoodItem food = extractFoodItemFromForm();
            food.setFoodId(foodId);

            boolean success = foodService.updateFood(food);
            if (success) {
                JOptionPane.showMessageDialog(this, "Food item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFormFields();
                loadFoodTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. Food may have been modified or removed.", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onDeleteFood() {
        try {
            if (txtFoodId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Select a food item from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int foodId = Integer.parseInt(txtFoodId.getText().trim());
            String foodName = txtFoodName.getText().trim();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete '" + foodName + "'?\nNote: If this food is logged in past meals, deletion may be restricted.",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = foodService.deleteFood(foodId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Food deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    clearFormFields();
                    loadFoodTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Cannot delete food item. It is currently referenced in one or more logged meals.",
                            "Referential Integrity Restriction", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private FoodItem extractFoodItemFromForm() {
        String name = txtFoodName.getText().trim();
        String unit = cmbServingUnit.getSelectedItem() != null ? cmbServingUnit.getSelectedItem().toString() : "grams";
        double servingSize = ValidationUtils.parsePositiveDouble(txtDefaultServingSize.getText(), "Default Serving Size");
        double calories = ValidationUtils.parseNonNegativeDouble(txtCalories.getText(), "Calories");
        double protein = ValidationUtils.parseNonNegativeDouble(txtProtein.getText(), "Protein");
        double carbs = ValidationUtils.parseNonNegativeDouble(txtCarbs.getText(), "Carbohydrates");
        double fats = ValidationUtils.parseNonNegativeDouble(txtFats.getText(), "Fats");

        return new FoodItem(name, unit, servingSize, calories, protein, carbs, fats);
    }

    private void populateFormFromSelectedRow() {
        int row = tblFoods.getSelectedRow();
        if (row != -1) {
            txtFoodId.setText(tableModel.getValueAt(row, 0).toString());
            txtFoodName.setText(tableModel.getValueAt(row, 1).toString());
            cmbServingUnit.setSelectedItem(tableModel.getValueAt(row, 2).toString());
            txtDefaultServingSize.setText(tableModel.getValueAt(row, 3).toString());
            txtCalories.setText(tableModel.getValueAt(row, 4).toString());
            txtProtein.setText(tableModel.getValueAt(row, 5).toString());
            txtCarbs.setText(tableModel.getValueAt(row, 6).toString());
            txtFats.setText(tableModel.getValueAt(row, 7).toString());

            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        }
    }

    private void clearFormFields() {
        txtFoodId.setText("");
        txtFoodName.setText("");
        cmbServingUnit.setSelectedIndex(0);
        txtDefaultServingSize.setText("100.0");
        txtCalories.setText("");
        txtProtein.setText("");
        txtCarbs.setText("");
        txtFats.setText("");

        tblFoods.clearSelection();
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    public void loadFoodTableData() {
        onFilterOrSort();
    }

    private void onFilterOrSort() {
        String keyword = (txtSearch != null) ? txtSearch.getText().trim() : "";
        List<FoodItem> items = foodService.searchFood(keyword);

        String criterion = (cmbSortCriterion != null) ? cmbSortCriterion.getSelectedItem().toString() : "Name";
        boolean ascending = (chkAscending == null) || chkAscending.isSelected();

        List<FoodItem> sorted = foodService.getSortedFoods(items, criterion, ascending);

        tableModel.setRowCount(0);
        for (FoodItem item : sorted) {
            tableModel.addRow(new Object[]{
                item.getFoodId(),
                item.getFoodName(),
                item.getServingUnit(),
                item.getDefaultServingSize(),
                item.getCalories(),
                item.getProtein(),
                item.getCarbohydrates(),
                item.getFats()
            });
        }
    }

    /**
     * Standalone main runner allowing Member 1 to test this panel in an independent window.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }

            JFrame frame = new JFrame("NutriTrack - Food Management Module (Member 1 Test)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(950, 600);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new FoodManagementPanel());
            frame.setVisible(true);
        });
    }
}
