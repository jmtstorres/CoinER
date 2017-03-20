package com.hammersoft.coiner;

import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.hammersoft.coiner.core.LiveCoinService;
import com.hammersoft.coiner.core.data.AvailableCurrencies;
import com.hammersoft.coiner.core.data.AvailablePairsInfo;
import com.hammersoft.coiner.core.data.Info;
import com.hammersoft.coiner.core.data.ListData;
import com.hammersoft.coiner.core.data.PairInfo;
import com.hammersoft.coiner.core.post.exception.HttpPostException;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView coinsListView;
    private HashMap<String, PairInfo> coinPairMap;
    private RelativeLayout mainLayout;
    private RelativeLayout loadingLayout;
    private RelativeLayout netLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private View decorView;

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

        loadingLayout = (RelativeLayout) findViewById(R.id.loadingPanel);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread() {
                    @Override
                    public void run() {
                        updateCoinList();
                    }
                }.start();
            }
        });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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

    @Override
    protected void onStart() {
        super.onStart();

        new Thread() {
            @Override
            public void run() {
                updateCoinList();
            }
        }.start();
    }

    private void updateCoinList() {
        if(!isOnline()){
            loadingLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.GONE);
            netLayout.setVisibility(View.VISIBLE);
            return;
        }

        AvailablePairsInfo api = null;
        AvailableCurrencies ac = null;
        try {
            api = LiveCoinService.getAvailablePairsInfo();
            ac = LiveCoinService.getAvailableCurrencies();
        } catch (JSONException e) {
            e.printStackTrace();
            loadingLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        } catch (IOException e) {
            e.printStackTrace();
            loadingLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        } catch (HttpPostException e) {
            e.printStackTrace();
            loadingLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
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
                    ListData data = new ListData(infoUSD.getLast(), infoBTC.getLast(), currency.getSymbol());
                    listPairs.add(data);
                }
            }
        }

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
                coinsListView.setAdapter(adapter);
                loadingLayout.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
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
