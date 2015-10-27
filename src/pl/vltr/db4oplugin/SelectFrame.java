package pl.vltr.db4oplugin;

import java.awt.*;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.db4o.reflect.ReflectClass;
import javafx.scene.control.TableSelectionModel;
import net.miginfocom.swing.MigLayout;

import com.db4o.reflect.ReflectField;
import com.db4o.reflect.generic.GenericClass;
import com.db4o.reflect.generic.GenericObject;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

public class SelectFrame extends JFrame {

    private MigLayout lay;

    private ReflectClass reflectClass;
    private Object object;

    public SelectFrame(ReflectClass _reflectClass, Object _object) {
        setReflectClass(_reflectClass);
        setObject(_object);
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

        for (Object obj : DbViewer.getInstance().getObjects(reflectClass)) {
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
        add(scrollPane, "w 100%, wrap");
        add(new JButton("Select"), "w 100%, wrap");
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
}