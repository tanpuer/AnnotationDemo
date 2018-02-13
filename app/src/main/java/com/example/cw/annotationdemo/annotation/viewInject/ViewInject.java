package com.example.cw.annotationdemo.annotation.viewInject;

/**
 * Created by cw on 2018/2/12.
 */

public interface ViewInject<T>{

    void inject(T t, Object source);

}
