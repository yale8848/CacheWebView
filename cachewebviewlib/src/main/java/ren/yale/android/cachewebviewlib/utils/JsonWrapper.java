package ren.yale.android.cachewebviewlib.utils;

/**
 * Created by yale on 2017/9/24.
 */
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class JsonWrapper extends JSONObject {
    public JsonWrapper() {
        super();
    }
    public JsonWrapper(String jsonString) throws JSONException {
        super(jsonString);
    }

    public String obj2JosnStr(Object obj,Class cls) throws Exception{
        Field[] fs = cls.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            String name = fs[i].getName();
            fs[i].setAccessible(true);
            String value = (String) fs[i].get(obj);
            this.put(name,value);
        }
        return this.toString();
    }

    public <T> T getBean(Class<T> cls) throws Exception{
        T obj = cls.newInstance();
        Field[] fs = cls.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            String name = fs[i].getName();
            fs[i].setAccessible(true);
            try {
                Object value = this.get(name);
                if(value==null)continue;
                fs[i].set(obj, value);
            }catch (Exception e){
            }
        }
        return obj;
    }

}
