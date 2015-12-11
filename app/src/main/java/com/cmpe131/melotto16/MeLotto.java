package com.cmpe131.melotto16;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.drive.Drive;

public class MeLotto extends MyActivity implements View.OnClickListener {

    private static String TAG = "MeLotto";
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Intent intent = getIntent();
        userName = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);
        if (userName == null)
            userName = "";
        setContentView(R.layout.activity_me_lotto);
        Log.i(TAG, "home screen started");
        TextView welcome = (TextView)findViewById(R.id.txt_welcome);
        welcome.setText("Welcome " + userName);
        findViewById(R.id.btn_addTicket).setOnClickListener(this);
        findViewById(R.id.btn_viewTickets).setOnClickListener(this);
        findViewById(R.id.btn_calendar).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_addTicket:
                addTicket();
                break;
            case R.id.btn_viewTickets:
                viewTickets();
                break;
            case R.id.btn_calendar:
                calendar();
                break;
            case R.id.btn_exit:
                exit();
                break;
        }
    }

    private void exit() {
        finish();
    }

    private void calendar() {
        startActivity(new Intent(this, ViewCalendar.class));
    }

    private void viewTickets() {
        startActivity(new Intent(this, ViewActivity.class));
    }

    private void addTicket() {
        startActivity(new Intent(this, ChooseLotto.class));
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        TextView textView = (TextView) findViewById(R.id.txt_welcome);
        textView.setText("Welcome ");
    }
}
