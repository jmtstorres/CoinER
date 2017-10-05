package com.hammersoft.coiner.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.hammersoft.coiner.CurrencyInfoActivity;
import com.hammersoft.coiner.MainActivity;
import com.hammersoft.coiner.R;
import com.hammersoft.coiner.core.LiveCoinService;
import com.hammersoft.coiner.core.data.AvailableCurrencies;
import com.hammersoft.coiner.core.data.AvailablePairsInfo;
import com.hammersoft.coiner.core.data.Info;
import com.hammersoft.coiner.core.data.ListData;
import com.hammersoft.coiner.core.data.PairInfo;
import com.hammersoft.coiner.core.post.exception.HttpPostException;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class BackgroundService extends Service {

    public Handler handler = null;
    public static Runnable runnable = null;

    private HashMap<String,ListData> newInfo;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                refresh();
                handler.postDelayed(runnable, 15*60*1000);
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    private void refresh(){
        if(!isOnline()){
            return;
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                updateCoinList();
            }
        });
        t.start();
    }

    private synchronized void updateCoinList() {
        if(!isOnline()){
            System.out.println("Error: Network Down");
            return;
        }

        AvailablePairsInfo api;
        AvailableCurrencies ac;

        try {
            api = LiveCoinService.getAvailablePairsInfo();
            ac = LiveCoinService.getAvailableCurrencies();
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            return;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            return;
        } catch (HttpPostException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            return;
        }

        HashMap<String, PairInfo> coinPairMap = new HashMap<>();

        for(PairInfo apinfo : api.getPairInfo()){
            coinPairMap.put(apinfo.getSymbol(), apinfo);
        }

        HashMap<String, ListData> listPairs = new HashMap<>();

        if(coinPairMap.containsKey(getString(R.string.btc) + "/" + "USD")){
            PairInfo infoUSD = coinPairMap.get(getString(R.string.btc) + "/" + "USD");

            if(infoUSD != null){
                ListData data = new ListData(
                        infoUSD.getLast(),
                        0.0f,
                        getString(R.string.btc),
                        getString(R.string.btc),
                        getAlertConfig(getString(R.string.btc)));
                listPairs.put(getString(R.string.btc), data);
            }
        }

        for(Info currency : ac.getInfo()){
            if(coinPairMap.containsKey(currency.getSymbol() + "/" + "USD") &&
                    coinPairMap.containsKey(currency.getSymbol() + "/" + "BTC")){

                PairInfo infoUSD = coinPairMap.get(currency.getSymbol() + "/" + "USD");
                PairInfo infoBTC = coinPairMap.get(currency.getSymbol() + "/" + "BTC");

                if(infoUSD != null && infoBTC != null){
                    ListData data = new ListData(
                            infoUSD.getLast(),
                            infoBTC.getLast(),
                            currency.getSymbol(),
                            currency.getName(),
                            getAlertConfig(currency.getSymbol()));
                    listPairs.put(currency.getSymbol(), data);
                }
            }
        }

        int i = 0;
        if (listPairs.size() > 0){
            newInfo = listPairs;

            String cifra = getString(R.string.str_cifra_dolar);

            Set<String> symbols = listPairs.keySet();
            for (String symbol : symbols) {
                ListData newValue = newInfo.get(symbol);

                if(!newValue.isAlert()){
                    continue;
                }

                Float limitLower = getLowerThresholdConfig(symbol);
                Float limitUpper = getUpperThresholdConfig(symbol);

                System.out.println(symbol + " :<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                System.out.println("current: " + newValue.getValUSD());
                System.out.println("upper: " + limitUpper);
                System.out.println("lower: " + limitLower);

                if(newValue.getValUSD() > limitUpper){
                    sendNotification(
                            symbol,
                            cifra + " " + newValue.getValUSD().toString(),
                            getIcon(symbol),
                            i,
                            true);
                }

                if(newValue.getValUSD() < limitLower){
                    sendNotification(
                            symbol,
                            cifra + " " + newValue.getValUSD().toString(),
                            getIcon(symbol),
                            i,
                            false);
                }
                i++;
            }
        }
    }

    private boolean getAlertConfig(String symbol) {
        SharedPreferences settings = getSharedPreferences(CurrencyInfoActivity.PREFS_NAME, 0);
        boolean alertOn = settings.getBoolean(symbol + ".alertOn", false);
        return alertOn;
    }

    private Float getLowerThresholdConfig(String symbol) {
        SharedPreferences settings = getSharedPreferences(CurrencyInfoActivity.PREFS_NAME, 0);
        Float val = settings.getFloat(symbol + ".thresholdLower", 0f);
        return val;
    }

    private Float getUpperThresholdConfig(String symbol) {
        SharedPreferences settings = getSharedPreferences(CurrencyInfoActivity.PREFS_NAME, 0);
        Float val = settings.getFloat(symbol + ".thresholdUpper", 0f);
        return val;
    }

    private void sendNotification(
            String title,
            String value,
            int iconId,
            int id,
            boolean upper){

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(iconId)
                        .setContentTitle(title)
                        .setContentText(value);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder = mBuilder.setContent(
                    getComplexNotificationView(
                            title,
                            value,
                            iconId,
                            upper));
        } else {
            mBuilder = mBuilder.setContentTitle(title)
                    .setContentText(value)
                    .setSmallIcon(iconId);
        }

        //Intent resultIntent = new Intent(this, MainActivity.class);
        Intent resultIntent = new Intent(this, CurrencyInfoActivity.class);
        resultIntent.putExtra(getString(R.string.str_param_currency_symbol),title);
        resultIntent.putExtra(getString(R.string.str_btc),title);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder.setAutoCancel(true);

        mNotificationManager.notify(id, mBuilder.build());
    }

    private RemoteViews getComplexNotificationView(
            String title,
            String value,
            int iconID,
            boolean upper) {

        RemoteViews notificationView = new RemoteViews(
                this.getPackageName(),
                R.layout.activity_custom_notification
        );

        notificationView.setImageViewResource(
                R.id.imagenotileft,
                iconID);

        if(upper){
            iconID = R.drawable.ic_dash_upper;
        }else{
            iconID = R.drawable.ic_dash_lower;
        }

        notificationView.setImageViewResource(
                R.id.imageViewDash,
                iconID);

        notificationView.setTextViewText(R.id.title, title);
        notificationView.setTextViewText(R.id.textViewValue, value);

        return notificationView;
    }

    private int getIcon(String symbol){
        String mDrawableName = "ic_" + symbol.toLowerCase();
        return getResources().getIdentifier(mDrawableName , "drawable", getPackageName());
    }

    @Override
    public void onDestroy() {
        //handler.removeCallbacks(runnable);
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        //Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}