package com.justclick.clicknbook.jctPayment.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class TextViewAdapter extends BaseAdapter {

    private Context context;
    private final String[] textViewValues;

    public TextViewAdapter(Context context, String[] textViewValues) {
        this.context = context;
        this.textViewValues = textViewValues;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        TextView textView;

        if (convertView == null) {

            gridView = new View(context);
//
//            // get layout from mobile.xml
//            gridView = inflater.inflate(R.layout.item, null);
//
//            // set value into textview
//            TextView textView = (TextView) gridView
//                    .findViewById(R.id.grid_item_label);
//            textView.setText(textViewValues[position]);

            textView=new TextView(context);
            textView.setLayoutParams(new GridView.LayoutParams(85, 85));
//            textView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            textView.setTextScaleX(20f);
            textView.setPadding(8, 8, 8, 8);
textView.setText(textViewValues[position]);


        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {

        return textViewValues.length;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

}
