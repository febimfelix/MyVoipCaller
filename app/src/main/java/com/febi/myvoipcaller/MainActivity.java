package com.febi.myvoipcaller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginFunction();
            }
        });
    }

    private void loginFunction() {
        Intent intent = new Intent(getApplicationContext(), CallActivity.class);
        intent.putExtra("callerId", ((TextView) findViewById(R.id.callerId)).getText().toString());
        intent.putExtra("recipientId", ((TextView) findViewById(R.id.recipientId)).getText().toString());
        startActivity(intent);
    }
}
