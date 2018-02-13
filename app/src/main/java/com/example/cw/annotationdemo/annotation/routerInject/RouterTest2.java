package com.example.cw.annotationdemo.annotation.routerInject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.cw.annotationdemo.R;
import com.example.cw.annotationdemo.annotation.viewInject.ViewInjector;
import com.example.libinjector.routerInjector.RouterMap;
import com.example.libinjector.viewInject.GetViewTo;

/**
 * Created by cw on 2018/2/12.
 */

@RouterMap("router://test2/index?name=cw&age=18")
public class RouterTest2 extends Activity{

    private static final String TAG = "RouterTest2";

    @GetViewTo(R.id.router_test_btn2)
    public Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_test2);
        ViewInjector.injectView(this);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouterManager.startActivity("router://test1/index?age=17&name=cw", RouterTest2.this);
            }
        });
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String age = intent.getStringExtra("age");
        Log.d(TAG, "onCreate: " + "name = " + name + " age = " + age);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("age",18);
        setResult(1,intent);
        super.finish();
    }
}
