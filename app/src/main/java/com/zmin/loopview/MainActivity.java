package com.zmin.loopview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private LoopView loopview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loopview = (LoopView) findViewById(R.id.loopview);
        loopview.setOnTimeCountListener(new LoopView.OnTimeCountListener() {
            @Override
            public void finish() {
                Toast.makeText(MainActivity.this, "倒计时完成", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void click1(View view) {
        loopview.setTotalTime(30 * 60);
        loopview.setRemineTime(30*60);
    }

    public void click2(View view) {
        loopview.setRemineTime(1*60);
    }
}
