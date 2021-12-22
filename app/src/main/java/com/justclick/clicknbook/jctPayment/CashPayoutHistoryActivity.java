package com.justclick.clicknbook.jctPayment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.jctPayment.Adapters.CashPayoutHistoryAdapter;
import com.justclick.clicknbook.jctPayment.Models.CashPayoutHistoryDataModel;
import com.justclick.clicknbook.jctPayment.Utilities.URLs;
import com.justclick.clicknbook.jctPayment.Utilities.VolleySingleton;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.MyPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CashPayoutHistoryActivity extends AppCompatActivity {
    private final boolean SHOW_PROGRESS=true, NO_PROGRESS=false;
    private int totalPageCount=0, pageNumber=1;
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<CashPayoutHistoryDataModel.Data> paymentHistoryArrayList;
    private CashPayoutHistoryAdapter cashPayoutHistoryAdapter;

    private LinearLayoutManager layoutManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_payout_history);
        recyclerView=findViewById(R.id.recyclerView);
        context=this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        paymentHistoryArrayList=new ArrayList<>();

        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Fetching cash payout list....");
        progressDialog.setCancelable(false);

        cashPayoutHistoryAdapter=new CashPayoutHistoryAdapter(context, new CashPayoutHistoryAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(View view, ArrayList<CashPayoutHistoryDataModel.Data> list, int position) {

            }
        }, paymentHistoryArrayList, totalPageCount);
        layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cashPayoutHistoryAdapter);

        if(paymentHistoryArrayList!=null && paymentHistoryArrayList.size()==0) {
            pageNumber=1;
            if(Common.checkInternetConnection(context)) {
                getCashOutHistory(pageNumber, SHOW_PROGRESS);
            }else {
                Toast.makeText(context,R.string.no_internet_message, Toast.LENGTH_LONG).show();
            }
        }else {
            cashPayoutHistoryAdapter.notifyDataSetChanged();
        }
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);

    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

//            if (!isLoading && !isLastPage) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount <= totalPageCount
                    && dy>0) {
                if(!(pageNumber>totalItemCount)) {
                    pageNumber=pageNumber+1;
                    getCashOutHistory(pageNumber, NO_PROGRESS);
                }

            }
        }
    };

    private void getCashOutHistory(int page, boolean progress) {
        final String str_token = MyPreferences.getToken(context);
        if(progress){
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.cashpayout+page,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if(obj!=null && obj.has("data") && obj.getJSONArray("data").length()>0){
                                CashPayoutHistoryDataModel dataModel=new CashPayoutHistoryDataModel();
                                dataModel.current_page=obj.getInt("current_page");
                                dataModel.first_page_url=obj.getString("first_page_url");
                                dataModel.last_page_url=obj.getString("last_page_url");
                                dataModel.next_page_url=obj.getString("next_page_url");
                                dataModel.path=obj.getString("path");
                                dataModel.prev_page_url=obj.getString("prev_page_url");
                                dataModel.from=obj.getInt("from");
                                dataModel.last_page=obj.getInt("last_page");
                                dataModel.per_page=obj.getInt("per_page");
                                dataModel.to=obj.getInt("to");
                                dataModel.total=obj.getInt("total");
                                totalPageCount=dataModel.total;
                                ArrayList<CashPayoutHistoryDataModel.Data> list=new ArrayList<>();
                                for (int i=0; i<obj.getJSONArray("data").length(); i++){
                                    CashPayoutHistoryDataModel.Data model=new CashPayoutHistoryDataModel().new Data();
                                    model.amount=obj.getJSONArray("data").getJSONObject(i).getDouble("amount");
                                    model.charges=obj.getJSONArray("data").getJSONObject(i).getDouble("charges");
                                    model.created_at=obj.getJSONArray("data").getJSONObject(i).getString("created_at");
//                                    model.deleted_at=obj.getJSONArray("data").getJSONObject(i).getString("deleted_at");
                                    model.updated_at=obj.getJSONArray("data").getJSONObject(i).getString("updated_at");
//                                    model.reference_id=obj.getJSONArray("data").getJSONObject(i).getString("reference_id");
                                    model.isActive=obj.getJSONArray("data").getJSONObject(i).getString("isActive");
//                                    model.response=obj.getJSONArray("data").getJSONObject(i).getString("response");
//                                    model.FK_userMasterId=obj.getJSONArray("data").getJSONObject(i).getInt("FK_userMasterId");
//                                    model.id=obj.getJSONArray("data").getJSONObject(i).getInt("id");
                                    model.totalPageCount=totalPageCount;
                                    list.add(model);
                                }
                                paymentHistoryArrayList.addAll(list);
                                cashPayoutHistoryAdapter.notifyDataSetChanged();
//                                success
                            }else if(obj!=null && obj.has("total") && obj.getInt("total")==0){
                                Toast.makeText(context, "No record found", Toast.LENGTH_LONG).show();
                            }else {
//                                Toast.makeText(context, R.string.response_failure_message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(context, getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, getResources().getString(R.string.aeps_response_failure), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                //pars.put("Content-Type", "application/x-www-form-urlencoded");
                pars.put("Authorization", str_token);
                return pars;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

}
