package com.hammersoft.coiner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.hammersoft.coiner.core.LiveCoinService;
import com.hammersoft.coiner.core.data.AvailableCurrencies;
import com.hammersoft.coiner.core.data.AvailablePairsInfo;
import com.hammersoft.coiner.core.data.Info;
import com.hammersoft.coiner.core.data.ListData;
import com.hammersoft.coiner.core.data.PairInfo;
import com.hammersoft.coiner.core.post.exception.HttpPostException;
import com.hammersoft.coiner.security.SecurityCheck;
import com.hammersoft.coiner.service.BackgroundService;

import org.json.JSONException;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static java.security.CryptoPrimitive.SIGNATURE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView coinsListView;
    private HashMap<String, PairInfo> coinPairMap;
    private RelativeLayout mainLayout;
    private RelativeLayout loadingLayout;
    private RelativeLayout netLayout;
    private RelativeLayout errorLayout;
    private RelativeLayout privacyLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private View decorView;
    private Float usdValue = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        coinsListView = (ListView) findViewById(R.id.coinListView);
        coinsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String text = ((StableArrayAdapter)coinsListView.getAdapter())
                        .getListDataItem(position).getSymbol();

                Intent newIntent = new Intent(MainActivity.this, CurrencyInfoActivity.class);
                newIntent.putExtra(getString(R.string.str_param_currency_symbol),text);
                newIntent.putExtra(getString(R.string.str_btc),text);
                startActivity(newIntent);
            }

        });

        mainLayout = (RelativeLayout) findViewById(R.id.layout_main);
        mainLayout.setVisibility(View.GONE);

        netLayout = (RelativeLayout) findViewById(R.id.net_layout);
        netLayout.setVisibility(View.GONE);

        errorLayout = (RelativeLayout) findViewById(R.id.error_layout);
        errorLayout.setVisibility(View.GONE);

        loadingLayout = (RelativeLayout) findViewById(R.id.loadingPanel);
        loadingLayout.setVisibility(View.VISIBLE);

        privacyLayout = (RelativeLayout) findViewById(R.id.privacy_layout);
        privacyLayout.setVisibility(View.GONE);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);

        ImageView image = (ImageView)findViewById(R.id.imageViewNotifyBTC);
        if(getAlertConfig(getString(R.string.btc))){
            image.setImageResource(R.drawable.ic_notify_filled);
        }else{
            image.setImageResource(R.drawable.ic_notify);
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(CurrencyInfoActivity.PREFS_NAME, 0);
                String symbol = getString(R.string.btc);
                boolean alertOn = settings.getBoolean(symbol + ".alertOn", false);

                if(!alertOn){
                    final DialogNotifyConfig diag = new DialogNotifyConfig(MainActivity.this, usdValue);
                    diag.setmListenerOk(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String symbol = getString(R.string.btc);
                            SharedPreferences settings = getSharedPreferences(CurrencyInfoActivity.PREFS_NAME, 0);
                            boolean alertOn = settings.getBoolean(symbol + ".alertOn", false);
                            ImageView imgView = (ImageView)findViewById(R.id.imageViewNotifyBTC);
                            imgView.setImageResource(R.drawable.ic_notify_filled);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(symbol + ".alertOn", !alertOn);
                            editor.putFloat(symbol + ".thresholdUpper", diag.getValueUpper());
                            editor.putFloat(symbol + ".thresholdLower", diag.getValueLower());
                            editor.commit();
                        }
                    });
                    diag.setCancelable(false);
                    diag.show();
                }else{
                    ImageView imgView = (ImageView)findViewById(R.id.imageViewNotifyBTC);
                    imgView.setImageResource(R.drawable.ic_notify);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(symbol + ".alertOn", !alertOn);
                    editor.commit();
                }
            }
        });

        ImageView imageGOogle = (ImageView)findViewById(R.id.imageViewPrivacy);
        imageGOogle.setOnClickListener(mGooglePlayListener);

        TextView v = (TextView)findViewById(R.id.textViewTitle);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = getString(R.string.str_btc);
                Intent newIntent = new Intent(MainActivity.this, CurrencyInfoActivity.class);
                newIntent.putExtra(getString(R.string.str_param_currency_symbol),text);
                newIntent.putExtra(getString(R.string.str_btc),text);
                startActivity(newIntent);
            }
        });

        ImageView imageReload = (ImageView)findViewById(R.id.imageViewReload);
        imageReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        updateCoinList();
                    }
                }.start();
            }
        });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("C8658EEBC09767406F6E100549EDF08B")
                .build();
        mAdView.loadAd(adRequest);

        if(SecurityCheck.checkAppSignature(this) == SecurityCheck.INVALID){
            loadingLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            netLayout.setVisibility(View.GONE);
            privacyLayout.setVisibility(View.VISIBLE);
            return;
        }

        if(!isServiceRunning(BackgroundService.class)){
            Intent bindIntent = new Intent(this, BackgroundService.class);
            startService(bindIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SecurityCheck.checkAppSignature(this) == SecurityCheck.INVALID){
            loadingLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            netLayout.setVisibility(View.GONE);
            privacyLayout.setVisibility(View.VISIBLE);
            return;
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            new Thread() {
                @Override
                public void run() {
                    updateCoinList();
                }
            }.start();
        }
    };

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

    @Override
    protected void onStart() {
        super.onStart();

        if(SecurityCheck.checkAppSignature(this) == SecurityCheck.INVALID){
            loadingLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            netLayout.setVisibility(View.GONE);
            privacyLayout.setVisibility(View.VISIBLE);
            return;
        }

        new Thread() {
            @Override
            public void run() {
                updateCoinList();
            }
        }.start();
    }

    private synchronized void updateCoinList() {
        if(!isOnline()){
            System.out.println("Error: Network Down");
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

        AvailablePairsInfo api = null;
        AvailableCurrencies ac = null;
        try {
            api = LiveCoinService.getAvailablePairsInfo();
            ac = LiveCoinService.getAvailableCurrencies();
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    loadingLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    netLayout.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
            return;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    loadingLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    netLayout.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
            return;
        } catch (HttpPostException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    loadingLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    netLayout.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
            return;
        }

        if(coinPairMap == null){
            coinPairMap = new HashMap<>();
        }

        for(PairInfo apinfo : api.getPairInfo()){
            coinPairMap.put(apinfo.getSymbol(), apinfo);
        }

        List<ListData> listPairs = new ArrayList<>();

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
                    listPairs.add(data);
                }
            }
        }

        Collections.sort(listPairs);

        final StableArrayAdapter adapter = new StableArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                R.layout.coin_list_item,
                listPairs);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                PairInfo infoUSD = coinPairMap.get("BTC/USD");
                ((TextView)findViewById(R.id.textViewUSDVal)).setText(
                        String.format("$ %1$,.3f", infoUSD.getLast()));
                usdValue = infoUSD.getLast();
                coinsListView.setAdapter(adapter);
                loadingLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                netLayout.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private boolean getAlertConfig(String symbol) {
        SharedPreferences settings = getSharedPreferences(CurrencyInfoActivity.PREFS_NAME, 0);
        boolean alertOn = settings.getBoolean(symbol + ".alertOn", false);
        return alertOn;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_facebook) {
            mFacebookListener.onClick(null);
        } else if (id == R.id.nav_twitter) {
            mTwitterListener.onClick(null);
        } else if (id == R.id.nav_google) {
            mGooglePlayListener.onClick(null);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    View.OnClickListener mFacebookListener = new View.OnClickListener() {
        public void onClick(View v) {
        Intent intent;
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/557897164351272"));
        } catch (Exception e) {
            intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/androidhammersoft"));
        }

        startActivity(intent);
        }
    };

    View.OnClickListener mTwitterListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = null;
            try {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=3366305819"));
            } catch (Exception e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Hammer_Soft"));
            }
            startActivity(intent);
        }
    };

    View.OnClickListener mGooglePlayListener = new View.OnClickListener() {
        public void onClick(View v) {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=hammersoft")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/search?q=hammersoft")));
        }
        }
    };
}
