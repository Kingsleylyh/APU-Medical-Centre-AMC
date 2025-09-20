package amc.group.pkg9;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Date;

public class TimeSpinnerRenderer implements TableCellRenderer {
    private final JSpinner spinner;

    public TimeSpinnerRenderer(){
        spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor de = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(de);
        spinner.setEnabled(false);
        spinner.setBorder(null);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if(value instanceof Date){
            spinner.setValue(value);
        }
        spinner.setBackground(isSelected?table.getSelectionBackground():table.getBackground());
        return spinner;
    }
}
