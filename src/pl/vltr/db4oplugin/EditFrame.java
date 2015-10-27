package pl.vltr.db4oplugin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;

import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.generic.GenericClass;
import net.miginfocom.swing.MigLayout;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;

public class EditFrame extends JFrame {

    private final static Integer TYPE_INTEGER = 0;
    private final static Integer TYPE_STRING = 1;
    private final static Integer TYPE_FLOAT = 2;
    private final static Integer TYPE_DOUBLE = 3;
    private final static Integer TYPE_CHAR = 4;
    private final static Integer TYPE_OTHER = 5;

    private MigLayout lay;

    private Object object;
    private GenericClass clazz;
    private Map<String, Object> values;
    private Map<String, JBTextField> fields;
    private Map<String, Integer> types;

    public EditFrame(Object object, Map<String, Object> values, GenericClass clazz) {
        super("Edit object");
        setObject(object);
        setValues(values);
        setClazz(clazz);
        this.fields = new HashMap<String, JBTextField>();
        this.types = new HashMap<String, Integer>();

        setSize(400, 300);

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
        });
        add(saveBtn, "w 100%, wrap");
        pack();
    }

    private void saveObject() {
        for(String key : values.keySet()){
            Object newVal = fields.get(key).getText();
            Integer type = types.get(key);
            ReflectClass rc = clazz;
            ReflectField rField = rc.getDeclaredField(key);
            while(rField == null) {
                rc = rc.getSuperclass();
                rField = rc.getDeclaredField(key);
            }

            if(type == TYPE_INTEGER) {
                newVal = Integer.parseInt((String)newVal);
            }
            else if(type == TYPE_STRING) {
                newVal = String.valueOf(newVal);
            }
            else if(type == TYPE_DOUBLE) {
                newVal = Double.valueOf((String)newVal);
            }
            else if(type == TYPE_FLOAT) {
                newVal = Float.valueOf((String)newVal);
            }
            else if(type == TYPE_CHAR) {
                newVal = Character.valueOf(((String)newVal).charAt(0));
            }
            else if (type == TYPE_OTHER) {
                // newVal = null;
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

        panel.setLayout(new MigLayout());
        panel.add(new JBLabel(key + ":"), "w 25%");
        if (isInteger || isString || isFloat || isDouble || isChar) {
            if (isInteger)
                types.put(key, TYPE_INTEGER);
            else if (isString)
                types.put(key, TYPE_STRING);
            else if (isFloat)
                types.put(key, TYPE_FLOAT);
            else if (isDouble)
                types.put(key, TYPE_DOUBLE);
            else if (isChar)
                types.put(key, TYPE_CHAR);

            JBTextField field = new JBTextField(String.valueOf(value));
            fields.put(key, field);
            panel.add(field, "w 75%");
        } else {
            types.put(key, TYPE_OTHER);
            JBTextField field = new JBTextField("(object)");
            fields.put(key, field);
            JButton moreBtn = new JButton("...");
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
