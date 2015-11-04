package pl.vltr.db4oplugin;

import com.db4o.reflect.ReflectClass;

import java.sql.Ref;

public class FieldUtil {

    public final static Integer TYPE_INTEGER = 0;
    public final static Integer TYPE_STRING = 1;
    public final static Integer TYPE_FLOAT = 2;
    public final static Integer TYPE_DOUBLE = 3;
    public final static Integer TYPE_CHAR = 4;
    public final static Integer TYPE_BOOLEAN = 5;
    public final static Integer TYPE_COLLECTION = 6;
    public final static Integer TYPE_OTHER = 7;

    public static boolean isString(ReflectClass rc) {
        String name = rc.getName().toLowerCase();
        if (name.endsWith("string")) {
            return true;
        }
        return false;
    }

    public static boolean isChar(ReflectClass rc){
        String name = rc.getName().toLowerCase();
        if (name.endsWith("char") || name.endsWith("character")) {
            return true;
        }
        return false;
    }

    public static boolean isInteger(ReflectClass rc) {
        String name = rc.getName().toLowerCase();
        if (name.endsWith("int") || name.endsWith("integer")) {
            return true;
        }
        return false;
    }

    public static boolean isFloat(ReflectClass rc) {
        String name = rc.getName().toLowerCase();
        if (name.endsWith("float")) {
            return true;
        }
        return false;
    }

    public static boolean isDouble(ReflectClass rc) {
        String name = rc.getName().toLowerCase();
        if (name.endsWith("double")) {
            return true;
        }
        return false;
    }

    public static boolean isBoolean(ReflectClass rc) {
        String name = rc.getName().toLowerCase();
        if (name.endsWith("bool") || name.endsWith("boolean")) {
            return true;
        }
        return false;
    }

    public static boolean isCollection(ReflectClass rc) {
        String name = rc.getName().toLowerCase();
        if (name.endsWith("collection") || name.endsWith("list") || name.endsWith("set")) {
            return true;
        }
        return false;
    }

    public static Integer getType(ReflectClass rc) {
        if (isInteger(rc)) {
            return TYPE_INTEGER;
        }
        if (isString(rc)) {
            return TYPE_STRING;
        }
        if (isFloat(rc)) {
            return TYPE_FLOAT;
        }
        if (isDouble(rc)) {
            return TYPE_DOUBLE;
        }
        if (isBoolean(rc)) {
            return TYPE_BOOLEAN;
        }
        if (isCollection(rc)) {
            return TYPE_COLLECTION;
        }
        if(isCollection(rc)){
            return TYPE_CHAR;
        }

        return TYPE_OTHER;
    }

}
