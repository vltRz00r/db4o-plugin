package pl.vltr.db4oplugin;

import java.awt.*;
import java.util.Collection;
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

    private final static Integer TYPE_INTEGER = 0;
    private final static Integer TYPE_STRING = 1;
    private final static Integer TYPE_FLOAT = 2;
    private final static Integer TYPE_DOUBLE = 3;
    private final static Integer TYPE_CHAR = 4;
    private final static Integer TYPE_BOOLEAN = 5;
    private final static Integer TYPE_COLLECTION = 6;

    private final static Integer TYPE_OTHER = 7;

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

        for (String key : values.keySet()) {
            Object val = values.get(key);
            JPanel panel = makeRow(key, val);
            add(panel, "w 100%, wrap");
        }

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener((e) -> {
            saveObject();
            dispose();
            afterExec.accept(null);
        });
        add(saveBtn, "w 100%, wrap");
        pack();

        setSize(getWidth() + 100, getHeight());

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }

    private void saveObject() {
        for (String key : values.keySet()) {
            Object newVal = fields.get(key).getText();
            Integer type = primitiveTypes.get(key);
            ReflectClass rc = clazz;
            ReflectField rField = rc.getDeclaredField(key);
            while (rField == null) {
                rc = rc.getSuperclass();
                rField = rc.getDeclaredField(key);
            }

            if (type == TYPE_INTEGER) {
                newVal = Integer.parseInt((String) newVal);
            } else if (type == TYPE_STRING) {
                newVal = String.valueOf(newVal);
            } else if (type == TYPE_DOUBLE) {
                newVal = Double.valueOf((String) newVal);
            } else if (type == TYPE_FLOAT) {
                newVal = Float.valueOf((String) newVal);
            } else if (type == TYPE_CHAR) {
                newVal = Character.valueOf(((String) newVal).charAt(0));
            } else if (type == TYPE_BOOLEAN) {
                newVal = Boolean.valueOf((String) newVal);
            } else if (type == TYPE_OTHER) {
                newVal = values.get(key);
            }

            rField.set(object, newVal);
        }
        DbViewer.getInstance().updateObject(object);
        dispose();
    }

    private JPanel makeRow(String key, Object value) {
        JPanel panel = new JPanel();
        Boolean isInteger = (value instanceof Integer);
        Boolean isString = (value instanceof String);
        Boolean isFloat = (value instanceof Float);
        Boolean isDouble = (value instanceof Double);
        Boolean isChar = (value instanceof Character);
        Boolean isBoolean = (value instanceof Boolean);
        Boolean isCollection = (value instanceof Collection);

        panel.setLayout(new MigLayout());
        panel.add(new JBLabel(key + ":"), "w 25%");
        if (isInteger || isString || isFloat || isDouble || isChar || isBoolean) {
            if (isInteger)
                primitiveTypes.put(key, TYPE_INTEGER);
            else if (isString)
                primitiveTypes.put(key, TYPE_STRING);
            else if (isFloat)
                primitiveTypes.put(key, TYPE_FLOAT);
            else if (isDouble)
                primitiveTypes.put(key, TYPE_DOUBLE);
            else if (isChar)
                primitiveTypes.put(key, TYPE_CHAR);
            else if (isBoolean)
                primitiveTypes.put(key, TYPE_BOOLEAN);

            JBTextField field = new JBTextField(String.valueOf(value));
            fields.put(key, field);
            panel.add(field, "w 75%");
        } else {
            if(isCollection){
                primitiveTypes.put(key, TYPE_COLLECTION);
            }else{
                primitiveTypes.put(key, TYPE_OTHER);
            }
            complexTypes.put(key, clazz.getDeclaredField(key).getFieldType());

            JBTextField field = new JBTextField(value != null ? value.toString() : "");
            fields.put(key, field);
            JButton moreBtn = new JButton("...");
            if(isCollection) {
                moreBtn.setEnabled(false);
            }
            moreBtn.addActionListener((e) -> {
                SelectFrame sf = new SelectFrame(complexTypes.get(key), value, clazz.getDeclaredField(key));
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
