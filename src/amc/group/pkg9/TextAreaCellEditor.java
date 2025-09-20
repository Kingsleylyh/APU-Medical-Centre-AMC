package amc.group.pkg9;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class TextAreaCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JTextArea textArea;
    private JScrollPane scrollPane;

    public TextAreaCellEditor() {
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {

        textArea.setText(value != null ? value.toString() : "");
        textArea.setCaretPosition(0);

        return scrollPane;
    }

    @Override
    public Object getCellEditorValue() {
        return textArea.getText();
    }

    @Override
    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }
}