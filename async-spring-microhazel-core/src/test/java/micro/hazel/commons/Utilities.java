package micro.hazel.commons;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Utilities {
    public static <CLASS,TYPE>void spyField(CLASS obj, Class<CLASS> clazz, String name, Class<TYPE> type, TYPE replace)  {
        try {
            Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            Field modField = Field.class.getDeclaredField("modifiers");
            modField.setAccessible(true);
            modField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            f.set(obj,replace);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);

        }
    }

}
