package com.justclick.clicknbook.adapter.viewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justclick.clicknbook.R;

public class MyFooterViewHolder extends RecyclerView.ViewHolder {
  public TextView name;
  public LinearLayout footer_lin;
  public MyFooterViewHolder(View view) {
    super(view);
    name = view.findViewById(R.id.name_tv);
    footer_lin = view.findViewById(R.id.footer_lin);
  }
}