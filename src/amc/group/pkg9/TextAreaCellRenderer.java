package amc.group.pkg9;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TextAreaCellRenderer extends JTextArea implements TableCellRenderer {

    public TextAreaCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {

        setText(value != null ? value.toString() : "");

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        // Calculate preferred height based on content
        setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
        int preferredHeight = getPreferredSize().height;

        // Set minimum row height to accommodate the text
        if (table.getRowHeight(row) < preferredHeight) {
            table.setRowHeight(row, preferredHeight);
        }

        return this;
    }
}

