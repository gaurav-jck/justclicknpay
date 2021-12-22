package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.AgentNameModel;
import com.justclick.clicknbook.model.HotelCityListModel;

public class HotelCitySearchAdapter extends BaseAdapter{
    private HotelCityListModel model;
    private String agent_URL = "";
    private Context context;

    public HotelCitySearchAdapter(Context context, HotelCityListModel hotelCityListModel)
    {
        model = hotelCityListModel;
        this.context=context;
    }

    @Override
    public int getCount()
    {
        if(model.CityResponse!=null)
        return model.CityResponse.size();
        return 0;
    }

    @Override
    public HotelCityListModel.CityResponse getItem(int position)
    {
        return model.CityResponse.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.autocomplete_layout, parent, false);

        TextView Name = (TextView) view.findViewById(R.id.countryName);
        Name.setText(model.CityResponse.get(position).CityName);
        return view;
    }


}