package com.qiyue.qdmobile.utils;

import com.github.snowdream.android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Simon on 7/3/15.
 */
public class ReflectUtils {

    public static Object safelyInvokeMethod(Method method, Object receiver, Object... args) {
        try {
            return method.invoke(receiver, args);
        } catch (IllegalArgumentException e) {
            Log.e("Safe invoke fail", "Invalid args", e);
        } catch (IllegalAccessException e) {
            Log.e("Safe invoke fail", "Invalid access", e);
        } catch (InvocationTargetException e) {
            Log.e("Safe invoke fail", "Invalid target", e);
        }

        return null;
    }

}
