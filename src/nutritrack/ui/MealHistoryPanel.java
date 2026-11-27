package nutritrack.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.util.Objects;

import nutritrack.model.Meal;
import nutritrack.model.MealItem;
import nutritrack.model.Nutrition;
import nutritrack.service.HistoryService;
import nutritrack.utils.DateUtils;
import nutritrack.utils.UIConstants;

public class MealHistoryPanel extends JPanel {

    private final HistoryService historyService;
    private final JTextField dateField;
    private final JLabel nutritionSummary;
    private final DefaultTableModel tableModel;
    private final JTable mealTable;

    public MealHistoryPanel() {
        this(new HistoryService());
    }

    public MealHistoryPanel(HistoryService historyService) {
        this.historyService = Objects.requireNonNull(
            historyService, "historyService must not be null"
        );

        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        dateField = new JTextField(LocalDate.now().toString(), 10);
        nutritionSummary = new JLabel("Daily nutrition: ");

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setOpaque(false);
        toolbar.add(new JLabel("Date (YYYY-MM-DD):"));
        toolbar.add(dateField);

        JButton refreshButton = new JButton("Load History");
        refreshButton.addActionListener(e -> loadHistory());
        toolbar.add(refreshButton);

        JButton deleteButton = new JButton("Delete Selected Meal");
        deleteButton.addActionListener(e -> deleteSelectedMeal());
        toolbar.add(deleteButton);

        tableModel = new DefaultTableModel(
            new Object[] {
                "Meal ID", "Meal", "Date", "Time", "Food", "Quantity",
                "Calories", "Protein", "Carbs", "Fats"
            },
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        mealTable = new JTable(tableModel);
        mealTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mealTable.setRowHeight(24);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setOpaque(false);
        footer.add(nutritionSummary);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(mealTable), BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        loadHistory();
    }

    private void loadHistory() {
        final LocalDate date;

        try {
            date = LocalDate.parse(dateField.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Enter a valid date in YYYY-MM-DD format.",
                "Invalid Date",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            List<Meal> meals = historyService.getDailyMealHistory(date);
            Nutrition totals = historyService.getDailyNutritionReport(date);

            tableModel.setRowCount(0);
            for (Meal meal : meals) {
                for (MealItem item : meal.getMealItems()) {
                    tableModel.addRow(new Object[] {
                        meal.getMealId(),
                        meal.getMealName(),
                        DateUtils.formatDisplayDate(meal.getMealDate()),
                        DateUtils.formatDisplayTime(meal.getMealTime()),
                        item.getFoodItem().getFoodName(),
                        item.getQuantity() + " " + item.getFoodItem().getServingUnit(),
                        item.getCalculatedCalories(),
                        item.getCalculatedProtein(),
                        item.getCalculatedCarbs(),
                        item.getCalculatedFats()
                    });
                }
            }

            nutritionSummary.setText(String.format(
                "Daily nutrition: %.1f kcal | Protein: %.1f g | Carbs: %.1f g | Fats: %.1f g",
                totals.getTotalCalories(),
                totals.getTotalProtein(),
                totals.getTotalCarbs(),
                totals.getTotalFats()
            ));
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Could not load meal history: " + ex.getMessage(),
                "History Load Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void refreshHistory() {
        loadHistory();
    }

    private void deleteSelectedMeal() {
        int selectedRow = mealTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Select a meal row first.",
                "No Meal Selected",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        int mealId = ((Number) tableModel.getValueAt(selectedRow, 0)).intValue();
        String mealName = tableModel.getValueAt(selectedRow, 1).toString();

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Delete meal \"" + mealName + "\" and its history?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (historyService.deleteHistoricalMeal(mealId)) {
                loadHistory();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Could not delete the meal. Check the database connection and its foreign-key settings.",
                    "Delete Failed",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Could not delete the meal: " + ex.getMessage(),
                "Delete Failed",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
