package com.hammersoft.coiner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.hammersoft.coiner.core.CryptoCompareService;
import com.hammersoft.coiner.core.LiveCoinService;
import com.hammersoft.coiner.core.data.CoinHistoryDay;
import com.hammersoft.coiner.core.data.Datum;
import com.hammersoft.coiner.core.post.exception.HttpPostException;

import org.json.JSONException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

//https://github.com/PhilJay/MPAndroidChart

public class CurrencyInfoActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "currencies_prefs";

    private String symbol;
    private Float valUSD;
    private Float valBTC;

    private View decorView;

    private RelativeLayout mainLayout;
    private RelativeLayout loadingLayout;
    private RelativeLayout netLayout;
    private RelativeLayout errorLayout;

    private HashMap<String, Integer> iconMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_info);

        Intent myIntent = getIntent(); // gets the previously created intent
        String strSymbol = myIntent.getStringExtra(getString(R.string.str_param_currency_symbol));

        this.symbol = strSymbol;

        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        mainLayout = (RelativeLayout) findViewById(R.id.layout_main);
        mainLayout.setVisibility(View.GONE);

        netLayout = (RelativeLayout) findViewById(R.id.net_layout);
        netLayout.setVisibility(View.GONE);

        errorLayout = (RelativeLayout) findViewById(R.id.error_layout);
        errorLayout.setVisibility(View.GONE);

        loadingLayout = (RelativeLayout) findViewById(R.id.loadingPanel);
        loadingLayout.setVisibility(View.VISIBLE);

        ImageView imgView = (ImageView)findViewById(R.id.imageViewBack);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(symbol.compareToIgnoreCase(getString(R.string.btc)) != 0) {
            findViewById(R.id.chartBtc).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.chartBtc).setVisibility(View.GONE);
        }

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean alertOn = settings.getBoolean(symbol + ".alertOn", false);
        imgView = (ImageView)findViewById(R.id.imageViewNotify);
        if(alertOn){
            imgView.setImageResource(R.drawable.ic_notify_filled);
        }else{
            imgView.setImageResource(R.drawable.ic_notify);
        }

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                boolean alertOn = settings.getBoolean(symbol + ".alertOn", false);

                if(!alertOn){
                    final DialogNotifyConfig diag = new DialogNotifyConfig(CurrencyInfoActivity.this, valUSD);
                    diag.setmListenerOk(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            boolean alertOn = settings.getBoolean(symbol + ".alertOn", false);
                            ImageView imgView = (ImageView)findViewById(R.id.imageViewNotify);
                            imgView.setImageResource(R.drawable.ic_notify_filled);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(symbol + ".alertOn", !alertOn);
                            editor.putFloat(symbol + ".thresholdLower", diag.getValueLower());
                            editor.putFloat(symbol + ".thresholdUpper", diag.getValueUpper());
                            System.out.println(diag.getValueLower());
                            editor.commit();
                        }
                    });
                    diag.setCancelable(false);
                    diag.show();
                }else{
                    ImageView imgView = (ImageView)findViewById(R.id.imageViewNotify);
                    imgView.setImageResource(R.drawable.ic_notify);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(symbol + ".alertOn", !alertOn);
                    editor.commit();
                }
            }
        });

        AdView mAdView = (AdView) findViewById(R.id.adViewGraph);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("C8658EEBC09767406F6E100549EDF08B")
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread() {
            @Override
            public void run() {
                getTickerInfo();
            }
        }.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void getTickerInfo(){
        if(!isOnline()){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    loadingLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                    netLayout.setVisibility(View.VISIBLE);
                }
            });
            return;
        }

        try {
            valUSD = LiveCoinService.getPairExInfo(symbol, getString(R.string.str_usd)).getAverage();
            if(symbol.compareToIgnoreCase(getString(R.string.btc)) != 0) {
                valBTC = LiveCoinService.getPairExInfo(symbol, getString(R.string.str_btc)).getAverage();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    loadingLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.GONE);
                    netLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                }
            });
            return;
        } catch (IOException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    loadingLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.GONE);
                    netLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                }
            });
            return;
        } catch (HttpPostException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    loadingLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.GONE);
                    netLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                }
            });
            return;
        }

        final CoinHistoryDay chdbtc;
        final CoinHistoryDay chdusd;

        try {
            chdusd = CryptoCompareService.getHistoryDay(symbol, getString(R.string.str_usd), 100, 1);
            if(symbol.compareToIgnoreCase(getString(R.string.btc)) != 0) {
                chdbtc = CryptoCompareService.getHistoryDay(symbol, getString(R.string.str_btc), 100, 1);
            }else{
                chdbtc = new CoinHistoryDay();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            loadingLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.GONE);
            netLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            return;
        } catch (IOException e) {
            e.printStackTrace();
            loadingLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.GONE);
            netLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            return;
        } catch (HttpPostException e) {
            e.printStackTrace();
            loadingLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.GONE);
            netLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            return;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ArrayList<Entry> entriesBtc = new ArrayList<>();
                ArrayList<Entry> entriesUsd = new ArrayList<>();

                ImageView imgView = (ImageView)findViewById(R.id.imageViewCoin);
                imgView.setImageResource(getIcon(symbol));

                TextView txtView = (TextView) findViewById(R.id.txtViewSymbol);
                txtView.setText(symbol);

                TextView txtViewUSD = (TextView) findViewById(R.id.textViewInfoUSDVal);
                txtViewUSD.setText(String.format("$ %1$,.6f", valUSD));

                TextView txtViewBTC = (TextView) findViewById(R.id.textViewInfoBTCVal);
                if(symbol.compareToIgnoreCase(getString(R.string.btc)) != 0) {
                    txtViewBTC.setText(String.format("$ %1$,.6f", valBTC));
                }

                if(symbol.compareToIgnoreCase(getString(R.string.btc)) != 0) {
                    for (Datum d : chdbtc.getData()) {
                        BigDecimal dec = new BigDecimal(d.getHigh());

                        if (dec.floatValue() == 0) {
                            continue;
                        }

                        entriesBtc.add(new Entry(d.getTime(), dec.floatValue()));
                        System.out.println(d.getTime());
                    }
                }

                for(Datum d : chdusd.getData()){
                    BigDecimal dec = new BigDecimal(d.getHigh());

                    if(dec.floatValue() == 0){
                        continue;
                    }

                    entriesUsd.add(new Entry(d.getTime(), dec.floatValue()));
                }

                if(symbol.compareToIgnoreCase(getString(R.string.btc)) != 0) {
                    drawGraph((LineChart) findViewById(R.id.chartBtc), entriesBtc, getString(R.string.str_btc));
                }
                drawGraph((LineChart) findViewById(R.id.chartUsd), entriesUsd, getString(R.string.str_usd));

                loadingLayout.setVisibility(View.GONE);
                netLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private int getIcon(String symbol){
        String mDrawableName = "ic_" + symbol.toLowerCase();
        return getResources().getIdentifier(mDrawableName , "drawable", getPackageName());
    }

    private void drawGraph(LineChart chart, ArrayList<Entry> entries, String label){
        LineDataSet dataSet = new LineDataSet(entries, label);

        LineData lineData = new LineData(dataSet);
        lineData.setDrawValues(false);
        lineData.setValueTextColor(Color.BLACK);

        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        dataSet.setColor(Color.BLACK);

        chart.getXAxis().setLabelRotationAngle(45);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        chart.getXAxis().setTextColor(Color.DKGRAY);
        chart.getXAxis().setTextSize(6);

        Description desc = new Description();
        desc.setText(label);
        desc.setTextSize(18);
        chart.setDescription(desc);
        chart.getLegend().setEnabled(false);
        chart.setScaleMinima(1f, 1f);

        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                final DateFormat dateTimeFormatter = DateFormat.getDateInstance();
                return dateTimeFormatter.format(new Date((long) value * 1000));
            }
        });
        chart.setData(lineData);
        chart.invalidate();
        chart.animate();
    }
}
