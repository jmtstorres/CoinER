package com.hammersoft.coiner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.hammersoft.coiner.core.CryptoCompareService;
import com.hammersoft.coiner.core.LiveCoinService;
import com.hammersoft.coiner.core.data.CoinHistoryDay;
import com.hammersoft.coiner.core.data.Datum;
import com.hammersoft.coiner.core.post.exception.HttpPostException;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class CurrencyInfoActivity extends AppCompatActivity {

    private String symbol;
    private Double valUSD;
    private Double valBTC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_info);

        Intent myIntent = getIntent(); // gets the previously created intent
        String strSymbol = myIntent.getStringExtra(getString(R.string.str_param_currency_symbol));

        this.symbol = strSymbol;

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
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

    private void getTickerInfo(){

        try {
            valUSD = LiveCoinService.getPairExInfo(symbol, getString(R.string.str_usd)).getAverage();

            valBTC = LiveCoinService.getPairExInfo(symbol, getString(R.string.str_btc)).getAverage();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HttpPostException e) {
            e.printStackTrace();
        }

        final CoinHistoryDay chdbtc;
        final CoinHistoryDay chdusd;

        try {
            chdusd = CryptoCompareService.getHistoryDay(symbol, getString(R.string.str_usd));
            chdbtc = CryptoCompareService.getHistoryDay(symbol, getString(R.string.str_btc));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (HttpPostException e) {
            e.printStackTrace();
            return;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                TextView txtView = (TextView) findViewById(R.id.txtViewSymbol);
                txtView.setText(symbol);

                TextView txtViewUSD = (TextView) findViewById(R.id.textViewInfoUSDVal);
                txtViewUSD.setText(String.format("$ %1$,.6f", valUSD));

                TextView txtViewBTC = (TextView) findViewById(R.id.textViewInfoBTCVal);
                txtViewBTC.setText(String.format("$ %1$,.6f", valBTC));

                String[] labels = new String[chdbtc.getData().size()];

                int i = 0;
                DataPoint[] historyBtc = new DataPoint[chdbtc.getData().size()];
                for(Datum d : chdbtc.getData()){
                    DataPoint point = new DataPoint(d.getTime(), d.getHigh());
                    historyBtc[i++] = point;
                }

                i = 0;
                DataPoint[] historyUsd = new DataPoint[chdusd.getData().size()];
                for(Datum d : chdusd.getData()){
                    DataPoint point = new DataPoint(d.getTime(), d.getHigh());
                    historyUsd[i++] = point;
                }

                LineGraphSeries<DataPoint> seriesBtc = new LineGraphSeries<>(historyBtc);
                seriesBtc.setTitle(getString(R.string.str_btc));
                seriesBtc.setColor(Color.GREEN);
                seriesBtc.setDrawDataPoints(true);
                seriesBtc.setDataPointsRadius(4);
                seriesBtc.setThickness(2);

                LineGraphSeries<DataPoint> seriesUsd = new LineGraphSeries<>(historyUsd);
                seriesBtc.setTitle(getString(R.string.str_usd));
                seriesUsd.setColor(Color.BLUE);
                seriesUsd.setDrawDataPoints(true);
                seriesUsd.setDataPointsRadius(4);
                seriesUsd.setThickness(2);

                drawGraph((GraphView) findViewById(R.id.graphBTC), seriesBtc);
                drawGraph((GraphView) findViewById(R.id.graphUSD), seriesUsd);
            }
        });
    }

    private void drawGraph(GraphView graphview, LineGraphSeries<DataPoint> series){
        graphview.addSeries(series);

        graphview.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                final DateFormat dateTimeFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT);
                if (isValueX) {
                    // show normal x values
                    return dateTimeFormatter.format(new Date((long) value));
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        graphview.getGridLabelRenderer().setHorizontalLabelsAngle(45);
        graphview.getGridLabelRenderer().setLabelHorizontalHeight(-120);
        graphview.getViewport().setXAxisBoundsManual(false);
        graphview.getViewport().setYAxisBoundsManual(false);
    }
}
