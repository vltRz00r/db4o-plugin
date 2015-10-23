package pl.vltr.db4oplugin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ActionFactory {

    public static Action delAction(Object object, Consumer<Void> afterExec) {
        Action delAction = new Action() {
            @Override
            public String toString() {
                return "Delete object";
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("ACTION PERFORMED");
                DbViewer.getInstance().deleteObject(object);
                afterExec.accept(null);
            }

            @Override
            public Object getValue(String key) {
                if (key.equals(Action.NAME)) {
                    return "Delete object";
                }
                return null;
            }

            @Override
            public void putValue(String key, Object value) {
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void setEnabled(boolean b) { }

            @Override
            public void addPropertyChangeListener(PropertyChangeListener listener) { }

            @Override
            public void removePropertyChangeListener(PropertyChangeListener listener) { }
        };

        return delAction;
    }

}
