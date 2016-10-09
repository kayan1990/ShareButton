package com.example.administrator.sharebutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    ShareButtonView mShareButtonView;
    int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mShareButtonView = (ShareButtonView) findViewById(R.id.share_button);
        mShareButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count % 2 == 0)
                    mShareButtonView.reset();
                else
                    mShareButtonView.startAnimation();
                count++;
            }
        });
    }
}
