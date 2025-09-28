package features.doctor;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.table.DefaultTableModel;

import static javax.swing.SwingConstants.CENTER;

public class RemoveButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private final JButton button = new JButton("❌");
    private final JTable table;

    public RemoveButtonEditor(JTable table) {
        this.table = table;

        button.setBorder(null);
        button.setContentAreaFilled(false);
        button.setFont(new java.awt.Font("Segoe UI Emoji", 0, 14));
        button.setForeground(new java.awt.Color(255, 0, 51));
        button.setHorizontalAlignment(CENTER);

        button.addActionListener(e -> {
            int editingRow = table.getEditingRow();

            fireEditingStopped();

            SwingUtilities.invokeLater(() -> {
                try {
                    DefaultTableModel model = (DefaultTableModel) table.getModel();

                    if (model.getRowCount() <= 1) {
                        JOptionPane.showMessageDialog(table, "Cannot remove the last row. At least one row must remain.");
                        return;
                    }

                    // Convert view row to model row (in case of sorting/filtering)
                    int modelRow = table.convertRowIndexToModel(editingRow);

                    if (modelRow >= 0 && modelRow < model.getRowCount()) {
                        model.removeRow(modelRow);
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(table, "Couldn't remove row: " + ex.getMessage());
                }
            });
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setBackground(table.getBackground());
        }
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "❌";
    }
}