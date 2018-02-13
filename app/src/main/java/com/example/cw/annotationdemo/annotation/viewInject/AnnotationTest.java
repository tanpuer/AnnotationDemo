package com.example.cw.annotationdemo.annotation.viewInject;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cw.annotationdemo.R;
import com.example.libinjector.viewInject.GetViewTo;

import java.lang.reflect.Field;

/**
 * Created by cw on 2018/2/11.
 */

public class AnnotationTest extends Activity {

    @GetViewTo(R.id.annotation_btn)
    Button mButton;

    @GetViewTo(R.id.annotation_text)
    TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annotation_activity);
        ViewInjector.injectView(this);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AnnotationTest.this,"aaa",Toast.LENGTH_SHORT).show();
            }
        });
//        getAllAnnotationView();
    }

    private void getAllAnnotationView(){
        //获得成员变量
        Field[] fields = getClass().getFields();
        for (Field field : fields){
            //判断注解
            if (field.getAnnotations() != null){
                //确定注解类型
                if (field.isAnnotationPresent(GetViewTo.class)){
                    field.setAccessible(true);
                    GetViewTo getViewTo = field.getAnnotation(GetViewTo.class);
                    try {
                        field.set(this, findViewById(getViewTo.value()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
