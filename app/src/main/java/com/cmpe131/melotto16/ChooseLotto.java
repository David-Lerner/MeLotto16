package com.cmpe131.melotto16;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import checklotto.*;
import control.MyApplication;
import control.Ticket;

public class ChooseLotto extends MyActivity {

    private RadioGroup radioGroup;
    private TextView downloadMessage;
    private static String TAG = "ChooseLotto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_lotto);
        /* Initialize Radio Group and attach click handler */
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.clearCheck();

        /* Attach CheckedChangeListener to radio group */
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    Toast.makeText(ChooseLotto.this, rb.getText(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        downloadMessage = (TextView)findViewById(R.id.txt_download);
        downloadMessage.setVisibility(View.INVISIBLE);
    }

    public void onClear(View v) {
        /* Clears all selected radio buttons to default */
        radioGroup.clearCheck();
    }

    public final static String EXTRA_MESSAGE = "com.example.david.myfirstapp.MESSAGE";

    public void onSubmit(View v) {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(ChooseLotto.this, "No lottery selected", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton rb = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        String message = rb.getText().toString();
        downloadMessage.setVisibility(View.VISIBLE);
        new GetLottoTask().execute(message);

        /*Toast.makeText(ChooseLotto.this, rb.getText(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MyActivity.class);
        String message = rb.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);*/
    }

    class GetLottoTask extends AsyncTask<String, Integer, Lottery> {
        Lottery lotto = null;
        String lottoType;
        protected Lottery doInBackground(String... message) {
            //android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            String[] output;
            MyApplication app = (MyApplication)getApplication();
            try {
                lottoType = message[0];
                int lottoIndex = app.getInformation().getLotteryIndex(lottoType);
                switch (lottoIndex) {
                    case 0:
                        lotto = new Powerball();
                        break;
                    case 1:
                        lotto = new MegaMillions();
                        break;
                    case 2:
                        lotto = new SuperLottoPlus();
                        break;
                    case 3:
                        lotto = new Fantasy5();
                        break;
                    case 4:
                        lotto = new Daily4();
                        break;
                    case 5:
                        lotto = new Daily3();
                        break;
                    default:
                        throw new Exception();
                }
                app.getInformation().setLotto(lotto);
                app.getInformation().currentTicket = new Ticket(lottoType);
            }
            catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

            return lotto;
        }

        protected void onPostExecute(Lottery result) {
            downloadMessage.setVisibility(View.INVISIBLE);
            if (lotto == null)
            {
                Toast.makeText(ChooseLotto.this, "Failed to read from web", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(ChooseLotto.this, lottoType, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChooseLotto.this, EnterInfo.class);
                startActivity(intent);
            }
        }
    }
}
