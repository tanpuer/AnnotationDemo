package com.example.cw.annotationdemo.annotation.routerInject.bean;

import android.net.Uri;

/**
 * Created by cw on 2018/2/13.
 */

public class RouterInfo {

    private String id;
    private String scheme;
    private String host;
    private String query;

    public RouterInfo(String id){
        this.id = id;
        Uri uri = Uri.parse(id);
        this.scheme = uri.getScheme();
        this.host = uri.getHost();
        this.query = uri.getQuery();
    }

    public String getScheme() {
        return scheme;
    }

    public String getHost() {
        return host;
    }

    public String getQuery() {
        return query;
    }
}
