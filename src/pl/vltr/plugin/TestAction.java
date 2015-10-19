package pl.vltr.plugin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.intellij.ui.components.JBScrollPane;
import net.miginfocom.swing.MigLayout;

import com.db4o.reflect.ReflectField;
import com.db4o.reflect.generic.GenericClass;
import com.db4o.reflect.generic.GenericObject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.table.JBTable;

/**
 * Created by kwichowski on 2015-10-06.
 */
public class TestAction implements ToolWindowFactory {

    private static JButton openBtn = new JButton("Open ...");
    private static JButton fetchBtn = new JButton("Get Objects");
    private static JComboBox<GenericClass> classCombo = null;
    private static JPanel mainPanel = new JPanel(new MigLayout());
    private Boolean fileOpened = false;

    private void initLayout(JComponent component, Container container) {
        MigLayout migLayout = new MigLayout();
        container.setLayout(migLayout);
        container.removeAll();

        container.add(openBtn);
    }

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        JComponent component = toolWindow.getComponent();
        Container container = component.getParent();

        initLayout(component, container);

        fetchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GenericClass gc = (GenericClass) classCombo.getSelectedItem();
                List<Object> objects = DbViewer.getInstance().getObjects(gc);
                System.out.println("Objects size: " + objects.size());
                ReflectField[] rf = DbViewer.getFields(gc);

                List<String> columns = new ArrayList<String>();
                for (ReflectField r : rf) {
                    columns.add(r.getName());
                }

                DefaultTableModel dtm = new DefaultTableModel(null, columns.toArray());
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
                //TODO ?? :-(

                JBScrollPane scrollPane = new JBScrollPane();
                tab.setSize(300, 300);
                scrollPane.add(tab.getTableHeader());
                scrollPane.add(tab);
                scrollPane.setSize(500, 500);
                scrollPane.invalidate();

                /*mainPanel.add(scrollPane);
                mainPanel.setSize(500, 500);
                mainPanel.invalidate();*/

                container.add(scrollPane, "span, wrap");
                container.validate();
            }
        });

        openBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileOpened == false) {
                    JFileChooser chooser = new JFileChooser();
                    int ret = chooser.showOpenDialog(container);

                    if (ret == JFileChooser.APPROVE_OPTION) {
                        String path = chooser.getSelectedFile().getPath();
                        DbViewer.getInstance().open(path);
                        List<GenericClass> classes = DbViewer.getInstance().getClasses();
                        classCombo = new JComboBox<GenericClass>(classes.toArray(new GenericClass[classes.size()]));
                        container.add(classCombo);
                        container.add(fetchBtn, "wrap");
                        container.validate();
                        openBtn.setText("Close ...");
                        fileOpened = true;
                    }
                } else {
                    DbViewer.getInstance().close();
                    openBtn.setText("Open ...");
                    fileOpened = false;
                    initLayout(component, container);
                }
            }
        });
    }
}