package pl.vltr.db4oplugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.generic.GenericObject;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

public class SelectFrame extends JFrame {

    private MigLayout lay;

    private ReflectClass reflectClass;
    private Object object;
    private ReflectField reflectField;

    public SelectFrame(ReflectClass _reflectClass, Object _object, ReflectField _field) {
        setReflectClass(_reflectClass);
        setObject(_object);
        setReflectField(_field);
        lay = new MigLayout();
        setLayout(lay);

        ReflectField[] rf = DbViewer.getFields(reflectClass);
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

        List<Object> objects = DbViewer.getInstance().getObjects(reflectClass);
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

        JBScrollPane scrollPane = new JBScrollPane(tab);
        JButton selectBtn = new JButton("Select");
        selectBtn.addActionListener((e) -> {
            int selRow = tab.getSelectedRow();
            if (selRow > -1) {
                Object newVal = objects.get(selRow);
                if (newVal != null) {
                    if (reflectField == null) {
                        System.out.println("FIELD NULL : O");
                    }
                    reflectField.set(object, newVal);
                    DbViewer.getInstance().updateObject(object);
                }
            }
            dispose();
        });
        add(scrollPane, "w 100%, wrap");
        add(selectBtn, "w 100%, wrap");
        setSize(700, 400);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public ReflectClass getReflectClass() {
        return reflectClass;
    }

    public void setReflectClass(ReflectClass reflectClass) {
        this.reflectClass = reflectClass;
    }

    public ReflectField getReflectField() {
        return reflectField;
    }

    public void setReflectField(ReflectField reflectField) {
        this.reflectField = reflectField;
    }
}