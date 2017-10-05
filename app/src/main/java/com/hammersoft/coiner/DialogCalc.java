package com.hammersoft.coiner;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Joao.Torres on 24/03/2017.
 */

public class DialogCalc extends Dialog {

    private String value = null;
    private TextView txtViewFrom;
    private TextView txtViewTo;
    private double exchangeRate = 2.3;

    private static final int MAX_NUM = 10;

    public DialogCalc(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diag_layout);

        //Rect displayRectangle = new Rect();
        //Window window = getWindow();
        //window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        //getLayoutInflater().setMinimumWidth((int)(displayRectangle.width() * 0.9f));
        //setMinimumHeight((int)(displayRectangle.height() * 0.9f));

        txtViewFrom  = (TextView)findViewById(R.id.textViewFrom);
        txtViewFrom.setText(getFormatted("0"));

        txtViewTo  = (TextView)findViewById(R.id.textViewTo);
        txtViewTo.setText(getFormatted("0"));

        setupButtons();
    }

    private void setupButtons(){
        for(int i = 0; i <= 9; i++){
            TextView txtView = (TextView)findViewById(getResId("button" + i, TextView.class));
            txtView.setOnClickListener(new View.OnClickListener() {
                private int btnValue;

                public View.OnClickListener setValue(int value){
                    this.btnValue = value;
                    return this;
                }

                @Override
                public void onClick(View v) {
                    if(value == null) {
                        value = Integer.toString(btnValue);
                    }else if(value.length() <= MAX_NUM){
                        value = value.concat(Integer.toString(btnValue));
                    }

                    System.out.println(btnValue + " | " + value);
                    txtViewFrom.setText(getFormatted(value));
                    txtViewTo.setText(getFormatted(getExchanged(value)));
                }
            }.setValue(i));
        }

        ImageButton btn = (ImageButton)findViewById(R.id.buttonBackSpace);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(value != null &&
                   value.length() > 1 &&
                   !value.isEmpty()) {
                    value = value.substring(0, value.length() - 1);
                    txtViewFrom.setText(getFormatted(value));
                    txtViewTo.setText(getFormatted(getExchanged(value)));
                }else{
                    value = null;
                    txtViewFrom.setText(getFormatted("0"));
                    txtViewTo.setText(getFormatted(getExchanged("0")));
                }
            }
        });

        btn = (ImageButton)findViewById(R.id.buttonEquals);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private int getResId(String resName, Class<?> c) {
        String packageName = getContext().getPackageName();
        int resId = getContext().getResources().getIdentifier(resName, "id", packageName);
        return resId;
    }

    private String getExchanged(String value){
        double amount = Double.valueOf(value);
        return Double.toString(amount*exchangeRate);
    }

    private String getFormatted(String value){
        double amount = Double.valueOf(value)/100;
        if(amount == 0){
            this.value = "0";
        }
        //Locale locale = getContext().getResources().getConfiguration().locale;
        //NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        //return currencyFormatter.format(amount);
        return String.format("%,.2f", amount);
    }
}
