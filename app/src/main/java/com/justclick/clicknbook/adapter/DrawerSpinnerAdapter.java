package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.OptModel;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class DrawerSpinnerAdapter extends BaseAdapter {
    Context context;
    OptModel OptModel;
    LayoutInflater inflter;

    public DrawerSpinnerAdapter(Context context, OptModel OptModel) {
        this.context = context;
        this.OptModel = OptModel;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return OptModel.Data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_item, null);
        TextView name = (TextView) view.findViewById(R.id.name_tv);
        name.setText(OptModel.Data.get(i).OptName);
        return view;
    }
}