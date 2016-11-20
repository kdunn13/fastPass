package edu.gatech.seclass.fastpass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class personal_health_tab extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_health_tab);

        RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.activity_personal_health_tab);
        rlayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(personal_health_tab.this, form_launch.class));
            }
        });
    }
}
