package pl.vltr.db4oplugin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import com.db4o.reflect.ReflectField;
import com.db4o.reflect.generic.GenericClass;
import com.db4o.reflect.generic.GenericObject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

public class TestAction implements ToolWindowFactory {

    private static JButton openBtn = new JButton("Open");
    private static JButton fetchBtn = new JButton("Get Objects");
    private static JComboBox<GenericClass> classCombo = null;
    private static JPanel topPanel = new JPanel(new MigLayout());
    private static JPanel mainPanel = new JPanel(new MigLayout());

    private Boolean fileOpened = false;
    private GenericClass lastChosenClass = null;

    private void initLayout(JComponent component, Container container) {
        MigLayout migLayout = new MigLayout();
        container.setLayout(migLayout);
        container.removeAll();

        topPanel.add(openBtn);
        container.add(topPanel, "w 100%, wrap");
        container.add(mainPanel, "w 100%, h 500px, span, wrap");
    }

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        JComponent component = toolWindow.getComponent();
        Container container = component.getParent();

        initLayout(component, container);

        fetchBtn.addActionListener((evt) -> {
            GenericClass gc = (GenericClass) classCombo.getSelectedItem();
            lastChosenClass = gc;
            List<Object> objects = DbViewer.getInstance().getObjects(gc);
            ReflectField[] rf = DbViewer.getFields(gc);

            List<String> columns = new ArrayList<String>();
            for (ReflectField r : rf) {
                columns.add(r.getName());
            }

            DefaultTableModel dtm = new DefaultTableModel(null, columns.toArray()) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            JBTable tab = new JBTable(dtm);
            tab.getTableHeader().setVisible(true);

            for (Object obj : objects) {
                GenericObject go = (GenericObject) obj;
                String[] vals = new String[rf.length];
                for (int i = 0; i < rf.length; i++) {
                    Object valObj = go.get(i);
                    if (valObj != null) {
                        vals[i] = valObj.toString();
                    } else {
                        vals[i] = null;
                    }
                }
                dtm.addRow(vals);
            }

            tab.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    int r = tab.rowAtPoint(e.getPoint());
                    if (r >= 0 && r < tab.getRowCount()) {
                        tab.setRowSelectionInterval(r, r);
                    } else {
                        tab.clearSelection();
                    }

                    int rowindex = tab.getSelectedRow();
                    if (rowindex < 0)
                        return;
                    if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                        Object obj = objects.get(tab.getSelectedRow());

                        JPopupMenu popup = new JPopupMenu("Menu");
                        popup.add(new JMenuItem(ActionFactory.delAction(obj, (Void) -> {
                            classCombo.setSelectedItem(lastChosenClass);
                            fetchBtn.doClick();
                        })));
                        popup.add(new JMenuItem(ActionFactory.editAction(obj, DbViewer.getInstance().objData(obj), lastChosenClass)));
                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });

            mainPanel.removeAll();
            JBScrollPane scrollPane = new JBScrollPane(tab);
            mainPanel.add(scrollPane, "w 100%, span, wrap");
            mainPanel.validate();
        });

        openBtn.addActionListener((evt) -> {
            if (fileOpened == false) {
                JFileChooser chooser = new JFileChooser();
                int ret = chooser.showOpenDialog(container);

                if (ret == JFileChooser.APPROVE_OPTION) {
                    String path = chooser.getSelectedFile().getPath();
                    DbViewer.getInstance().open(path);
                    List<GenericClass> classes = DbViewer.getInstance().getClasses();
                    classCombo = new JComboBox<GenericClass>(classes.toArray(new GenericClass[classes.size()]));
                    topPanel.add(classCombo);
                    topPanel.add(fetchBtn, "wrap");
                    topPanel.validate();
                    openBtn.setText("Close");
                    fileOpened = true;
                }
            } else {
                DbViewer.getInstance().close();
                topPanel.removeAll();
                openBtn.setText("Open");
                topPanel.add(openBtn);
                mainPanel.removeAll();
                fileOpened = false;
                initLayout(component, container);
            }
        });
    }
}