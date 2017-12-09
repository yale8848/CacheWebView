package ren.yale.android.cachewebviewlib.utils;

/**
 * Created by yale on 2017/9/24.
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonWrapper extends JSONObject {
    public JsonWrapper() {
        super();
    }
    public JsonWrapper(String jsonString) throws JSONException {
        super(jsonString);
    }

    public static String map2Str(Map map){
        JSONObject object = new JSONObject(map);
        return object.toString();
    }
    public static HashMap<String,String> str2Map(String jsonString){
        HashMap<String,String> result = new HashMap();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator iterator = jsonObject.keys();
            String key = null;
            String value = null;
            while (iterator.hasNext()) {
                try {
                    key = (String) iterator.next();
                    value = jsonObject.getString(key);
                    result.put(key, value);
                }catch (Exception e){
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String obj2JosnStr(Object obj,Class cls) throws Exception{
        Field[] fs = cls.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            try {
                String name = fs[i].getName();
                fs[i].setAccessible(true);
                String value = (String) fs[i].get(obj);
                this.put(name,value);
            }catch ( Exception e){
            }
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
