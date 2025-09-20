package amc.group.pkg9;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class RemoveButtonRenderer extends JButton implements TableCellRenderer {
    public RemoveButtonRenderer() {
        super("❌");
        setBorder(null);
        setContentAreaFilled(false);
        setFont(new java.awt.Font("Segoe UI Emoji", 0, 14));
        setForeground(new java.awt.Color(255, 0, 51));
        setHorizontalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}
