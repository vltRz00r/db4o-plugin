package pl.vltr.db4oplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.generic.GenericClass;
import com.db4o.reflect.generic.GenericObject;

public class DbViewer {

    private static DbViewer instance;

    private static ObjectContainer db;

    private DbViewer() {
    }

    public static DbViewer getInstance() {
        if (instance == null) {
            instance = new DbViewer();
        }
        return instance;
    }

    public static Map<String, Object> objData(Object obj) {
        Map<String, Object> values = new HashMap<String, Object>();
        Class c = obj.getClass();
        if (c == GenericObject.class) {
            GenericObject go = (GenericObject) obj;
            GenericClass realClass = go.getGenericClass();
            ReflectField[] fields = getFields(realClass);
            for (int i = 0; i < fields.length; i++) {
                values.put(fields[i].getName(), go.get(i));
            }
        }
        return values;
    }

    public static ReflectField[] getFields(GenericClass gc) {
        List<ReflectField[]> fieldSets = new ArrayList<ReflectField[]>();

        fieldSets.add(gc.getDeclaredFields());

        ReflectClass rc = gc.getSuperclass();
        if (rc != null) {
            while (rc.getSuperclass() != null) {
                fieldSets.add(rc.getDeclaredFields());
                rc = rc.getSuperclass();
            }
        }

        int totalSize = 0;
        for (ReflectField[] rf : fieldSets) {
            totalSize += rf.length;
        }

        ReflectField[] ret = new ReflectField[totalSize];
        int cnt = 0;
        for (int i = fieldSets.size() - 1; i >= 0; i--) {
            ReflectField[] rf = fieldSets.get(i);
            for (int j = 0; j < rf.length; j++) {
                ret[cnt] = rf[j];
                cnt++;
            }
        }
        return ret;
    }

    public static ReflectField getField(ReflectClass clazz, String name){
        ReflectField ret = null;
        while(ret == null) {
            ret = clazz.getDeclaredField(name);
            if(ret == null){
                clazz = clazz.getSuperclass();
            }
        }
        return ret;
    }

    public static ReflectField[] getFields(ReflectClass rc) {
        List<ReflectField[]> fieldSets = new ArrayList<ReflectField[]>();

        fieldSets.add(rc.getDeclaredFields());

        ReflectClass tmpRc = rc.getSuperclass();
        if (rc != null) {
            if (tmpRc != null) {
                while (tmpRc.getSuperclass() != null) {
                    fieldSets.add(tmpRc.getDeclaredFields());
                    tmpRc = tmpRc.getSuperclass();
                }
            }
        }

        int totalSize = 0;
        for (ReflectField[] rf : fieldSets) {
            totalSize += rf.length;
        }

        ReflectField[] ret = new ReflectField[totalSize];
        int cnt = 0;
        for (int i = fieldSets.size() - 1; i >= 0; i--) {
            ReflectField[] rf = fieldSets.get(i);
            for (int j = 0; j < rf.length; j++) {
                ret[cnt] = rf[j];
                cnt++;
            }
        }
        return ret;
    }

    public void open(String path) {
        EmbeddedConfiguration conf = Db4oEmbedded.newConfiguration();
        db = Db4oEmbedded.openFile(conf, path);
    }

    public void close() {
        db.close();
    }

    public List<GenericClass> getClasses() {
        List<GenericClass> retList = new ArrayList<GenericClass>();
        ObjectSet<Object> objs = db.query(Object.class);

        for (Object obj : objs) {
            Class c = obj.getClass();
            if (c == GenericObject.class) {
                GenericObject go = (GenericObject) obj;
                GenericClass realClass = go.getGenericClass();

                if (retList.contains(realClass) == false) {
                    retList.add(realClass);
                }
            }
        }
        return retList;
    }

    public List<Object> getObjects(GenericClass gc) {
        ObjectSet<Object> objs = db.query(new Predicate<Object>() {
            public boolean match(Object obj) {
                Class c = obj.getClass();
                if (c == GenericObject.class) {
                    GenericObject go = (GenericObject) obj;
                    if (go.getGenericClass().equals(gc)) {
                        return true;
                    }
                }
                return false;
            }
        });

        List<Object> retList = new ArrayList<Object>();
        for (int i = 0; i < objs.size(); i++) {
            retList.add(objs.get(i));
        }

        return retList;
    }

    public List<Object> getObjects(ReflectClass rc) {
        ObjectSet<Object> objs = db.query(new Predicate<Object>() {
            public boolean match(Object obj) {
                Class c = obj.getClass();
                if (c == GenericObject.class) {
                    GenericObject go = (GenericObject) obj;
                    if (go.getGenericClass().equals(rc)) {
                        return true;
                    }
                    /*if( go.getGenericClass().getSuperclass().equals(rc)){
                        return true;
                    }*/
                }
                return false;
            }
        });

        List<Object> retList = new ArrayList<Object>();
        for (int i = 0; i < objs.size(); i++) {
            retList.add(objs.get(i));
        }

        return retList;
    }

    public void deleteObject(Object obj) {
        db.delete(obj);
        db.commit();
    }

    public void updateObject(Object obj) {
        db.store(obj);
        db.commit();
    }

}
