package com.febi.myvoipcaller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.calling.Call;

import static com.febi.myvoipcaller.R.id.stopButton;

public class PlaceCallActivity extends BaseActivity {

    private Button mCallButton;
    private EditText mCallName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mCallName           = (EditText) findViewById(R.id.callName);
        mCallButton         = (Button) findViewById(R.id.callButton);
        Button stopButton   = (Button) findViewById(R.id.stopButton);

        mCallButton.setEnabled(false);
        mCallButton.setOnClickListener(buttonClickListener);
        stopButton.setOnClickListener(buttonClickListener);
    }

    @Override
    protected void onServiceConnected() {
        TextView userName   = (TextView) findViewById(R.id.loggedInName);
        userName.setText(getSinchServiceInterface().getUserName());
        mCallButton.setEnabled(true);
    }

    private void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }

    private void callButtonClicked() {
        String phoneNumber  = mCallName.getText().toString();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a number to call", Toast.LENGTH_LONG).show();
            return;
        }

        Call call           = getSinchServiceInterface().callPhoneNumber(phoneNumber);
        String callId       = call.getCallId();

        Intent callScreen   = new Intent(this, CallScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        startActivity(callScreen);
    }

    private OnClickListener buttonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.callButton:
                    callButtonClicked();
                    break;

                case stopButton:
                    stopButtonClicked();
                    break;

            }
        }
    };
}
