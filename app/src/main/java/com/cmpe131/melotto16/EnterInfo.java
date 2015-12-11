package com.cmpe131.melotto16;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import checklotto.Lottery;
import control.MyApplication;

public class EnterInfo extends MyActivity implements AdapterView.OnItemSelectedListener {

    private EditText drawDate;
    private EditText numberDraws;
    private EditText[] num;
    private EditText numSpecial;
    private TextView specialNumber;
    private Spinner spinner;
    private static String TAG = "EnterInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_info);

        drawDate = (EditText)findViewById(R.id.edit_date);
        numberDraws = (EditText)findViewById(R.id.edit_numberDraws);
        num = new EditText[5];
        num[0] = (EditText)findViewById(R.id.edit_num1);
        num[1] = (EditText)findViewById(R.id.edit_num2);
        num[2] = (EditText)findViewById(R.id.edit_num3);
        num[3] = (EditText)findViewById(R.id.edit_num4);
        num[4] = (EditText)findViewById(R.id.edit_num5);
        numSpecial = (EditText)findViewById(R.id.edit_numSpecial);
        specialNumber = (TextView)findViewById(R.id.txt_specialNumber);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        MyApplication app = (MyApplication)getApplication();
        String lottoType = app.getInformation().currentTicket.ticketType;
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        int lottoIndex = app.getInformation().getLotteryIndex(lottoType);
        switch (lottoIndex) {
            case 0:
                adapter.add("Normal");
                spinner.setAdapter(adapter);
                specialNumber.setText("POWERBALL Number:");
                break;
            case 1:
                adapter.add("Normal");
                spinner.setAdapter(adapter);
                specialNumber.setText("MEGA Number:");
                break;
            case 2:
                adapter.add("Normal");
                spinner.setAdapter(adapter);
                specialNumber.setText("MEGA Number:");
                break;
            case 3:
                adapter.add("Normal");
                spinner.setAdapter(adapter);
                specialNumber.setVisibility(View.INVISIBLE);
                numSpecial.setVisibility(View.INVISIBLE);
                break;
            case 4:
                adapter.add("Straight");
                adapter.add("Box");
                adapter.add("Straight/Box");
                spinner.setAdapter(adapter);
                num[4].setVisibility(View.INVISIBLE);
                specialNumber.setVisibility(View.INVISIBLE);
                numSpecial.setVisibility(View.INVISIBLE);
                break;
            case 5:
                adapter.add("Straight");
                adapter.add("Box");
                adapter.add("Straight/Box");
                spinner.setAdapter(adapter);
                num[3].setVisibility(View.INVISIBLE);
                num[4].setVisibility(View.INVISIBLE);
                specialNumber.setVisibility(View.INVISIBLE);
                numSpecial.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    public void updateTicket(View view) {
        MyApplication app = (MyApplication)getApplication();
        Lottery lotto = app.getInformation().getLotto();
        String temp, temp2;
        int n;
        temp = drawDate.getText().toString();
        try
        {
            temp2 = temp.substring(0,2);
            n = Integer.parseInt(temp2);
            if (n > 31 || n < 1)
                throw new Exception();
            app.getInformation().currentTicket.drawDay = temp2;
            temp2 = temp.substring(3, 5);
            temp2 = lotto.getMonthByNumber(Integer.parseInt(temp2));
            if (temp2 == null)
                throw new Exception();
            app.getInformation().currentTicket.drawMonth = temp2;
            temp2 = temp.substring(6);
            if (Integer.parseInt(temp2) < 1)
                throw new Exception();
            app.getInformation().currentTicket.drawYear = temp2;
        }
        catch (Exception e)
        {
            Toast.makeText(EnterInfo.this, "Draw date not valid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(numberDraws.getText()))
        {
            Toast.makeText(EnterInfo.this, "Number of draws is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        n = Integer.parseInt(numberDraws.getText().toString());
        app.getInformation().currentTicket.draws = n;
        app.getInformation().currentTicket.numbers = new ArrayList[n];
        app.getInformation().currentTicket.specialNumber = new int[n];
        app.getInformation().currentTicket.isDrawn = new boolean[n];
        app.getInformation().currentTicket.amountWon = new String[n];
        for (int i = 0; i < n; i++)
        {
            app.getInformation().currentTicket.numbers[i] = new ArrayList<>();
        }

        for (int i = 0; i < 5; i++)
        {
            if (TextUtils.isEmpty(num[i].getText()))
                n = 0;
            else
                n = Integer.parseInt(num[i].getText().toString());
            for (ArrayList a : app.getInformation().currentTicket.numbers)
                a.add(n);
        }

        if (TextUtils.isEmpty(numSpecial.getText()))
            n = 0;
        else
            n = Integer.parseInt(numSpecial.getText().toString());
        for (int i = 0; i < app.getInformation().currentTicket.draws; i++)
            app.getInformation().currentTicket.specialNumber[i] = n;

        n = lotto.getDrawNumberByDate(
                app.getInformation().currentTicket.drawDay,
                app.getInformation().currentTicket.drawMonth,
                app.getInformation().currentTicket.drawYear);
        app.getInformation().currentTicket.drawNumber = n;
        for (int i = 0; i < app.getInformation().currentTicket.draws; i++) {
            app.getInformation().currentTicket.isDrawn[i] = lotto.wasDrawn(n+i);
        }
        app.getInformation().currentTicket.valid = true;

        Intent intent = new Intent(this, AddTicket.class);
        startActivity(intent);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String style = parent.getItemAtPosition(pos).toString();
        Log.i(TAG, pos + ": " + style);
        app.getInformation().currentTicket.playType = pos;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        app.getInformation().currentTicket.playType = 0;
    }
}
