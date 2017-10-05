package com.hammersoft.coiner;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hammersoft.coiner.util.InputFilterMinMax;

/**
 * Created by Joao.Torres on 24/03/2017.
 */

public class DialogNotifyConfig extends Dialog {

    private boolean cancelled = false;
    private View.OnClickListener mListenerOk = null;
    private View.OnClickListener mListenerCancel = null;
    private TextView txtViewLower;
    private TextView txtViewUpper;

    private static final Float MULTIPLIER = 1000000000.0f;
    private Float valueBase = 0.0f;
    private Float valueLower = 0.0f;
    private Float valueUpper = 0.0f;

    public DialogNotifyConfig(Context context) {
        super(context);
    }

    public DialogNotifyConfig(Context context, float valueBase) {
        super(context);
        this.valueBase = valueBase*MULTIPLIER;
        this.valueLower = valueBase*MULTIPLIER;
        this.valueUpper = valueBase*MULTIPLIER;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diag_layout_notify);

        txtViewLower = ((TextView)findViewById(R.id.textViewLower));
        txtViewUpper = ((TextView)findViewById(R.id.textViewUpper));

        txtViewUpper.setText(String.format("%1$,.8f", valueBase/MULTIPLIER));
        txtViewLower.setText(String.format("%1$,.8f", valueBase/MULTIPLIER));

        ((SeekBar)findViewById(R.id.seekBarUpper)).setMax(100);
        ((SeekBar)findViewById(R.id.seekBarUpper))
                .setOnSeekBarChangeListener(
                        new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                System.out.println(progress);
                                valueUpper = (((progress*valueBase/100 + valueBase))/MULTIPLIER);
                                txtViewUpper.setText(String.format("%1$,.8f", valueUpper));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {}

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {}
                        });

        ((SeekBar)findViewById(R.id.seekBarLower)).setMax(100);
        ((SeekBar)findViewById(R.id.seekBarLower)).setProgress(100);
        ((SeekBar)findViewById(R.id.seekBarLower))
                .setOnSeekBarChangeListener(
                        new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                System.out.println(progress);
                                valueLower = (progress*valueBase/MULTIPLIER)/100;
                                txtViewLower.setText(String.format("%1$,.8f", valueLower));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {}

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {}
                        });

        TextView b = (TextView) findViewById(R.id.textViewOK);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListenerOk != null){
                    mListenerOk.onClick(null);
                }
                cancelled = false;
                dismiss();
            }
        });

        b = (TextView) findViewById(R.id.textViewCancel);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListenerCancel != null){
                    mListenerCancel.onClick(null);
                }
                cancelled = true;
                dismiss();
            }
        });
    }

    public boolean isCancelled(){
        return this.cancelled;
    }

    public Float getValueUpper(){
        return valueUpper;
    }

    public Float getValueLower(){
        return valueLower;
    }

    private int getResId(String resName, Class<?> c) {
        String packageName = getContext().getPackageName();
        int resId = getContext().getResources().getIdentifier(resName, "id", packageName);
        return resId;
    }

    public void setmListenerOk(View.OnClickListener mListenerOk) {
        this.mListenerOk = mListenerOk;
    }

    public void setmListenerCancel(View.OnClickListener mListenerCancel) {
        this.mListenerCancel = mListenerCancel;
    }
}
