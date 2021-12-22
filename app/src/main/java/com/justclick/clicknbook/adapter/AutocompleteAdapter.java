package com.justclick.clicknbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.AgentNameModel;

public class AutocompleteAdapter extends BaseAdapter{
    private AgentNameModel agent;
    private String agent_URL = "";
    private Context context;

    public AutocompleteAdapter(Context context, AgentNameModel agentNameModel)
    {
        agent = agentNameModel;
        this.context=context;
    }

    @Override
    public int getCount()
    {
        if(agent.Data!=null)
        return agent.Data.size();
        return 0;
    }

    @Override
    public AgentNameModel.AgentName getItem(int position)
    {
        return agent.Data.get(position);
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
        Name.setText(agent.Data.get(position).AgencyName);
        return view;
    }


}