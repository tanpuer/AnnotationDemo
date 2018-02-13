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

@RouterMap("router://test1/index?name=hy&age=16")
public class RouterTest1 extends Activity{

    private static final String TAG = "RouterTest1";

    @GetViewTo(R.id.router_test_btn1)
    public Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_test1);
        ViewInjector.injectView(this);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouterManager.startActivityForResult("router://test2/index?name=hy&age=18",RouterTest1.this, 1);
            }
        });
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String age = intent.getStringExtra("age");
        Log.d(TAG, "onCreate: " + "name = " + name + " age = " + age);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (data != null) {
                int age = data.getIntExtra("age", 0);
                Log.d(TAG, age + "");
            }
        }
    }
}
