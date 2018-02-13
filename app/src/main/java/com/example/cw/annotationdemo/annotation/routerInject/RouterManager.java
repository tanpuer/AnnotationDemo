package com.example.cw.annotationdemo.annotation.routerInject;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.cw.annotationdemo.annotation.routerInject.bean.RouterInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cw on 2018/2/12.
 */

public class RouterManager {

    private static final String TAG = "RouterManager";
    private static Map<String,Class> routerMap = new HashMap<>();

    public static void addRouter(String id, Class clz){
        RouterInfo routerInfo = new RouterInfo(id);
        String host = routerInfo.getHost();
        routerMap.put(host,clz);
    }

    public static void startActivity(String id, Context context){
        RouterInfo routerInfo = new RouterInfo(id);
        String host = routerInfo.getHost();
        if (routerMap.containsKey(host)){
            Class target =  routerMap.get(host);
            Intent intent = new Intent(context,target);
            String query = routerInfo.getQuery();
            if (!query.isEmpty()){
                String[] pairs = query.split("&");
                for (String pair: pairs){
                    String[] keyValuePair = pair.split("=");
                    String key = keyValuePair[0];
                    String value = keyValuePair[1];
                    intent.putExtra(key,value);
                }
            }
            context.startActivity(intent);
        }else {
            Log.d(TAG, host + " is not in the routerMap");
        }
    }

    public static void startActivityForResult(String id, Activity activity,int requestCode){
        RouterInfo routerInfo = new RouterInfo(id);
        String host = routerInfo.getHost();
        if (routerMap.containsKey(host)){
            Class target =  routerMap.get(host);
            Intent intent = new Intent(activity,target);
            String query = routerInfo.getQuery();
            if (!query.isEmpty()){
                String[] pairs = query.split("&");
                for (String pair: pairs){
                    String[] keyValuePair = pair.split("=");
                    String key = keyValuePair[0];
                    String value = keyValuePair[1];
                    intent.putExtra(key,value);
                }
            }
            activity.startActivityForResult(intent,requestCode);
        }else {
            Log.d(TAG, host + " is not in the routerMap");
        }
    }

}
