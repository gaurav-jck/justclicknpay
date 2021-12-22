package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.graphics.Typeface;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justclick.clicknbook.Fragment.HomeFragment;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.utils.Common;

import java.util.ArrayList;

/**
 * Created by Lenovo on 04/10/2017.
 */

public class MenuItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private Context context;
    private ArrayList<LoginModel.DataList> arrayList;
    private OnRecyclerItemClickListener itemClickListener;
    private HomeFragment fragment;

    @Override
    public void onClick(View v) {

    }

    public interface OnRecyclerItemClickListener{
        public void onRecyclerItemClick(View view, ArrayList<LoginModel.DataList> list, int position);
    }

    public MenuItemsAdapter(Context context, OnRecyclerItemClickListener fragment, ArrayList<LoginModel.DataList> menuItems, HomeFragment frag) {
        this.context = context;
        this.arrayList=menuItems;
        itemClickListener= fragment;
        this.fragment=frag;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_show_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            MyViewHolder vh = (MyViewHolder) holder;

            Typeface face = Common.homeMenuMainItemTypeFace(context);
            vh.title.setTypeface(face);
            String MainMenuCode= arrayList.get(position).Mainmenu;

            vh.title.setText(arrayList.get(position).Mainmenu.toUpperCase());

           /* if(MainMenuCode.equalsIgnoreCase(MenuCodes.ProductMainCode)) {
                vh.title.setText("PRODUCT");
//            }else if(MainMenuCode.equalsIgnoreCase(MenuCodes.AgentCreditListFragment)) {
//                vh.title.setText("AgentCreditListFragment");
//            }else if(MainMenuCode.equalsIgnoreCase(MenuCodes.AgentDepositListFragment)) {
//                vh.title.setText("AgentDepositListFragment");
//            }else if(MainMenuCode.equalsIgnoreCase(MenuCodes.AdminDepositReportFragment)) {
//                vh.title.setText("AdminDepositReportFragment");
//            }else if(MainMenuCode.equalsIgnoreCase(MenuCodes.AdminCreditReportFragment)) {
//                vh.title.setText("AdminCreditReportFragment");
            }else {
                vh.linear_main_menu.setVisibility(View.GONE);
            }
*/
           MenuSubItemsAdapter menuItemsAdapter=new MenuSubItemsAdapter(context, new MenuSubItemsAdapter.OnRecyclerItemClickListener() {
                @Override
                public void onRecyclerItemClick(View view, ArrayList<LoginModel.DataList.subMenu> list, int position) {

                }
            }, arrayList.get(position).subMenu, fragment);
            LinearLayoutManager layoutManager=new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
            GridLayoutManager gridLayoutManager=new GridLayoutManager(context, 3);
            vh.recyclerView_menu_item.setLayoutManager(gridLayoutManager);
            vh.recyclerView_menu_item.setAdapter(menuItemsAdapter);
            vh.recyclerView_menu_item.setHorizontalScrollBarEnabled(true);

//            if(position==0){
//                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                vh.linear_main_menu.setLayoutParams(layoutParams);
//                vh.linear_main_menu.setBackgroundResource(R.color.dark_blue_color);
//            }else {
//                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                layoutParams.setMargins(5,5,5,5);
//                vh.linear_main_menu.setLayoutParams(layoutParams);
//            }

            /*if(position%3==0)
            {
                vh.linear_main_menu.setBackgroundResource(R.drawable.home_first_card_background);
            }else if(position%3==1)
            {
                vh.linear_main_menu.setBackgroundResource(R.drawable.home_second_card_background);
            }else if(position%3==2)
            {
                vh.linear_main_menu.setBackgroundResource(R.drawable.home_third_card_background);
            }*/
            vh.imageView_sub_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onRecyclerItemClick(v,arrayList,position);
//                    Toast.makeText(context,"selected",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView imageView_sub_item;
        public RecyclerView recyclerView_menu_item;
        public LinearLayout linear_main_menu;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            imageView_sub_item = (ImageView) view.findViewById(R.id.imageView_sub_item);
            recyclerView_menu_item = (RecyclerView) view.findViewById(R.id.recyclerView_menu_item);
            linear_main_menu = (LinearLayout) view.findViewById(R.id.linear_main_menu);

        }
    }

}