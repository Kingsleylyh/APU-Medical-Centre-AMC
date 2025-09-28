package features.doctor;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class TimeSpinnerEditor extends AbstractCellEditor implements TableCellEditor {

    private final JSpinner spinner;
    private final JFormattedTextField textField;
    private int lastCaret = -1; // remember caret position to avoid jumping to hours

    public TimeSpinnerEditor() {
        Date now = new Date();
        SpinnerDateModel model = new SpinnerDateModel(now, null, null, Calendar.MINUTE);
        spinner = new JSpinner(model);

        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);

        textField = editor.getTextField();
        textField.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

        if (textField.getFormatter() instanceof DefaultFormatter df) {
            df.setCommitsOnValidEdit(true);
            df.setOverwriteMode(true);
            df.setAllowsInvalid(false);
        }

        textField.addCaretListener(e -> lastCaret = e.getDot());

        // IMPORTANT: do NOT stopCellEditing() on every change — it causes the jump.
        spinner.addChangeListener(e ->
                SwingUtilities.invokeLater(() -> {
                    if (lastCaret >= 0 && lastCaret <= textField.getText().length()) {
                        textField.requestFocusInWindow();
                        textField.setCaretPosition(lastCaret);
                    }
                })
        );

        textField.addActionListener(ae -> stopCellEditing());
    }

    @Override
    public Object getCellEditorValue() {
        return spinner.getValue();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        if (value instanceof Date d) {
            spinner.setValue(d);
        } else if (value instanceof LocalTime lt) {
            Date d = Date.from(lt.atDate(java.time.LocalDate.now())
                    .atZone(ZoneId.systemDefault()).toInstant());
            spinner.setValue(d);
        } else if (value instanceof String s && s.matches("\\d{2}:\\d{2}")) {
            try {
                String[] parts = s.split(":");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                spinner.setValue(cal.getTime());
            } catch (Exception ignore) {
                setDefaultTime();
            }
        } else {
            setDefaultTime();
        }

        SwingUtilities.invokeLater(() -> {
            String txt = textField.getText(); // "HH:mm"
            int idx = txt.indexOf(':');
            int caret = (idx >= 0 && idx + 1 < txt.length()) ? idx + 1 : txt.length();
            textField.requestFocusInWindow();
            textField.setCaretPosition(caret);
            lastCaret = caret;
        });

        return spinner;
    }

    private void setDefaultTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        spinner.setValue(cal.getTime());
    }

    @Override
    public boolean stopCellEditing() {
        try {
            spinner.commitEdit(); // commit any pending text edits to the model
        } catch (java.text.ParseException e) {
            // keep editing if parse fails
            Toolkit.getDefaultToolkit().beep();
            return false;
        }
        return super.stopCellEditing();
    }
}
