package pl.vltr.db4oplugin;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.generic.GenericClass;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;

public class EditFrame extends JFrame {

    private MigLayout lay;

    private Object object;
    private GenericClass clazz;
    private Map<String, Object> values;
    private Map<String, JBTextField> fields;
    private Map<String, Integer> primitiveTypes;
    private Map<String, ReflectClass> complexTypes;

    public EditFrame(Object object, Map<String, Object> values, GenericClass clazz, Consumer<Void> afterExec) {
        super("Edit object");
        setObject(object);
        setValues(values);
        setClazz(clazz);
        this.fields = new HashMap<String, JBTextField>();
        this.primitiveTypes = new HashMap<String, Integer>();
        this.complexTypes = new HashMap<String, ReflectClass>();

        lay = new MigLayout();
        setLayout(lay);

        for (ReflectField rf : DbViewer.getFields(clazz)) {
            String key = rf.getName();
            Object val = values.get(key);

            ReflectClass fieldClass = rf.getFieldType();
            primitiveTypes.put(key, FieldUtil.getType(fieldClass));

            JPanel panel = makeRow(key, val);
            add(panel, "w 100%, wrap");
        }

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener((e) -> {
            saveObject();
            dispose();
            afterExec.accept(null);
            System.out.println("--CLICKED--");
        });
        add(saveBtn, "w 100%, wrap");
        pack();

        setSize(getWidth() + 100, getHeight());

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }

    private void saveObject() {
        for (String fKey : fields.keySet()) {
            String val = fields.get(fKey).getText().replaceAll("null", "");
            values.put(fKey, val);
        }

        System.out.println("values size: " + values.size());
        for (String key : values.keySet()) {
            Object newVal = fields.get(key).getText();
            Integer type = primitiveTypes.get(key);
            ReflectClass rc = clazz;
            ReflectField rField = DbViewer.getField(rc, key);

            if (type == FieldUtil.TYPE_INTEGER) {
                newVal = Integer.parseInt((String) newVal);
            } else if (type == FieldUtil.TYPE_STRING) {
                newVal = String.valueOf(newVal);
            } else if (type == FieldUtil.TYPE_DOUBLE) {
                newVal = Double.valueOf((String) newVal);
            } else if (type == FieldUtil.TYPE_FLOAT) {
                newVal = Float.valueOf((String) newVal);
            } else if (type == FieldUtil.TYPE_CHAR) {
                newVal = Character.valueOf(((String) newVal).charAt(0));
            } else if (type == FieldUtil.TYPE_BOOLEAN) {
                newVal = Boolean.valueOf((String) newVal);
            } else if (type == FieldUtil.TYPE_OTHER) {
                newVal = values.get(key);
            }

            rField.set(object, newVal);
        }
        DbViewer.getInstance().updateObject(object);
        dispose();
    }

    private JPanel makeRow(String key, Object value) {
        JPanel panel = new JPanel();

        panel.setLayout(new MigLayout());
        panel.add(new JBLabel(key + ":"), "w 25%");
        Integer type = primitiveTypes.get(key);
        if (type < 5) {
            JBTextField field = new JBTextField(String.valueOf(value));
            fields.put(key, field);
            panel.add(field, "w 75%");
        } else {
            /*if(isCollection){
                primitiveTypes.put(key, TYPE_COLLECTION);
            }else{
                primitiveTypes.put(key, TYPE_OTHER);
            }*/
            complexTypes.put(key, DbViewer.getField(clazz, key).getFieldType());

            JBTextField field = new JBTextField(value != null ? value.toString() : "");
            fields.put(key, field);
            JButton moreBtn = new JButton("...");
            if (type.equals(FieldUtil.TYPE_COLLECTION)) {
                moreBtn.setEnabled(false);
            }
            moreBtn.addActionListener((e) -> {
                SelectFrame sf = new SelectFrame(complexTypes.get(key), value, DbViewer.getField(clazz, key));
                sf.setVisible(true);
            });
            field.setEnabled(false);
            panel.add(field, "w 60%");
            panel.add(moreBtn, "w 15%");
        }

        return panel;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    public GenericClass getClazz() {
        return clazz;
    }

    public void setClazz(GenericClass clazz) {
        this.clazz = clazz;
    }
}
