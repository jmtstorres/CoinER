package com.hammersoft.coiner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hammersoft.coiner.core.LiveCoinService;
import com.hammersoft.coiner.core.data.AvailablePairsInfo;
import com.hammersoft.coiner.core.data.PairInfo;
import com.hammersoft.coiner.core.post.exception.HttpPostException;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread() {
            @Override
            public void run() {
                AvailablePairsInfo api = null;
                try {
                    api = LiveCoinService.getAvailablePairsInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (HttpPostException e) {
                    e.printStackTrace();
                }

                for(PairInfo info : api.getPairInfo()){
                    System.out.println(info);
                }
            }
        }.start();
    }
}
