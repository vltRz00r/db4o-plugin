package pl.vltr.db4oplugin;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Map;

public class EditFrame extends JFrame {

    private MigLayout lay;

    private Object object;
    private Map<String, Object> values;

    public EditFrame(Object object, Map<String, Object> values) {
        super("Edit");
        setObject(object);
        setValues(values);
        setSize(300,300);

        lay = new MigLayout();
        setLayout(lay);

        for(String str : values.keySet()){
            Object val = values.get(str);

            JBLabel label = new JBLabel(str+":");
            JBTextField field = new JBTextField(val.toString());
            add(label, "w 25%");
            add(field, "w 75%, wrap");
        }

        pack();
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
}
