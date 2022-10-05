package com.dashingqi.applicationa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dashingqi.router.annotation.Route;


@Route(path = "home/second", description = "SecondPage")
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}