package com.hammersoft.coiner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hammersoft.coiner.core.data.ListData;

import java.util.HashMap;
import java.util.List;

public class StableArrayAdapter extends ArrayAdapter<ListData> {

        private List<ListData> objects;
        private HashMap<String, Integer> iconMap;

        public StableArrayAdapter(Context context, int textViewResourceId, int layoutResourceId,
                                  List<ListData> objects) {

            super(context, textViewResourceId, layoutResourceId, objects);
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.coin_list_item, parent, false);

            ImageView image = (ImageView)convertView.findViewById(R.id.imageView);
            Integer icon = getIcon(objects.get(position).getSymbol());
            if(icon == null){
                icon = R.drawable.ic_btc;
            }
            image.setImageResource(icon);
            image.setFocusable(false);

            image = (ImageView)convertView.findViewById(R.id.imageViewNotify);
            if(!objects.get(position).isAlert()){
                image.setVisibility(View.GONE);
            }

            TextView symbolView = (TextView)convertView.findViewById(R.id.textViewCoinSymbol);
            symbolView.setText(objects.get(position).getSymbol());

            TextView nameView = (TextView)convertView.findViewById(R.id.textViewCoinName);
            nameView.setText(objects.get(position).getName().replace(" ", "\n"));

            TextView btcView = (TextView)convertView.findViewById(R.id.textViewBTC);
            btcView.setText("BTC");

            TextView btcViewValue = (TextView)convertView.findViewById(R.id.textViewBTCVal);
            btcViewValue.setText(String.format("%1$,.8f", objects.get(position).getValBTC()));

            TextView usdView = (TextView)convertView.findViewById(R.id.textViewUSD);
            usdView.setText("USD");

            TextView usdViewValue = (TextView)convertView.findViewById(R.id.textViewUSDVal);
            usdViewValue.setText(String.format("%1$,.8f", objects.get(position).getValUSD()));

            return convertView;
        }

    private int getIcon(String symbol){
        String mDrawableName = "ic_" + symbol.toLowerCase();
        return getContext().getResources().getIdentifier(mDrawableName , "drawable", getContext().getPackageName());
    }

        public ListData getListDataItem(int position){
            return objects.get(position);
        }
    }