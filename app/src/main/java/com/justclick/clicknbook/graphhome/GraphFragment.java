package com.justclick.clicknbook.graphhome;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.justclick.clicknbook.ApiConstants;
import com.justclick.clicknbook.R;
import com.justclick.clicknbook.model.LoginModel;
import com.justclick.clicknbook.myinterface.ToolBarHideFromFragmentListener;
import com.justclick.clicknbook.network.NetworkCall;
import com.justclick.clicknbook.utils.Common;
import com.justclick.clicknbook.utils.EncryptionDecryptionClass;
import com.justclick.clicknbook.utils.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;

public class GraphFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private BarChart chart;
    private PieChart piechart;
    private ToolBarHideFromFragmentListener toolBarHideFromFragmentListener;
    boolean isAnyTxn=false;
    private TextView noChartTv;

//    date
    private Dialog filterDialog;
    private Calendar startDateCalendar, endDateCalendar;
    private TextView start_date_value_tv, end_date_value_tv,
            start_day_value_tv, end_day_value_tv;
    private int startDateDay, startDateMonth, startDateYear,
            endDateDay, endDateMonth, endDateYear;
    private SimpleDateFormat dateFormat, dayFormat, dateToServerFormat;
    private String startDateToSend, endDateToSend;
    private TextView startDateTv;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        toolBarHideFromFragmentListener= (ToolBarHideFromFragmentListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=requireContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_graph, container, false);

        toolBarHideFromFragmentListener.onToolBarHideFromFragment(true);
//        chart = view.findViewById(R.id.chart);
        piechart = view.findViewById(R.id.piechart);
        noChartTv = view.findViewById(R.id.noChartTv);

       /* BarDataSet barDataSet = new BarDataSet(entries, title);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(barDataSet);
        chart.setData(data);
//        chart.setDescription("My Chart");
        chart.animateXY(2000, 2000);
        chart.invalidate();*/

        startDateTv=view.findViewById(R.id.startDateTv);
        initializeDates();
        setDates();
        getDashboardData();  //uncomment this
        view.findViewById(R.id.lin_dateFilter).setOnClickListener(view1 -> openFilterDialog());
        view.findViewById(R.id.back_arrow).setOnClickListener(view1 -> getParentFragmentManager().popBackStack());
//        showBarChart(chart);

//        showPieChart();

        return view;
    }

    private void getDashboardData() {
        DashboardChartRequest request=new DashboardChartRequest();
        request.donecarduser= MyPreferences.getLoginData(new LoginModel(), requireContext()).Data.DoneCardUser;
//        request.donecarduser= "JC0A36785";
//        request.donecarduser= "JC0A15275";
//        request.fromdate="2025-04-10";
        request.fromdate=startDateToSend;
//        request.uptodate="2025-04-10";
        request.uptodate=endDateToSend;
        new NetworkCall().callService(NetworkCall.getUatRemmitInterface().accountStmtPost(ApiConstants.getDashboard,
                        request),
                requireContext(),true,
                (response, responseCode) -> {
                    if(response!=null){
                        responseHandler(response);
                    }else {
                        Toast.makeText(requireContext(), "Unable to load chart data, please retry after some time", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void responseHandler(ResponseBody response) {
        try {
            DashboardChartResponse chartResponse = new Gson().fromJson(response.string(), DashboardChartResponse.class);
            if(chartResponse!=null) {
                if(chartResponse.statusCode.equals("00")){
                    if(chartResponse.data!=null && chartResponse.data.size()>0){
//                        Toast.makeText(requireContext(), chartResponse.statusMessage, Toast.LENGTH_SHORT).show();
//                        addChartData(chartResponse.data);
                        isAnyTxn=false;
                        addChartData2(chartResponse.data);
                    }else {
                        Toast.makeText(requireContext(), "No data found on given parameters", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(requireContext(), chartResponse.statusMessage, Toast.LENGTH_SHORT).show();
                }
            }else {

            }
        }catch (Exception e){

        }
    }


    private void addChartData(ArrayList<DashboardChartResponse.data> data) {
        ArrayList<Float> valueList = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        String title = "Product Wise Dashboard Data";

        for(int i=0; i<data.size(); i++){
            valueList.add(Float.valueOf(data.get(i).amount));
            labels.add(data.get(i).proType);
        }
        for (int i = 0; i < valueList.size(); i++) {
            BarEntry barEntry = new BarEntry(i, valueList.get(i).floatValue(), data.get(i).proType);
            entries.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(entries, title);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        chart.setData(barData);
        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(entries.size(), false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelRotationAngle(-45);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        chart.invalidate();

    }
    private void addChartData2(ArrayList<DashboardChartResponse.data> data) {
        Map<String, Float> typeAmountMap = new HashMap<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "type";
        String title = "Product Wise Dashboard Data";

        for(int i=0; i<data.size(); i++){
            if(Float.valueOf(data.get(i).amount)>0) {
                typeAmountMap.put(data.get(i).proType, Float.valueOf(data.get(i).amount));
                isAnyTxn=true;
            }
        }

        if(isAnyTxn){
            piechart.setVisibility(View.VISIBLE);
            noChartTv.setVisibility(View.GONE);
        }else {
            piechart.setVisibility(View.GONE);
            noChartTv.setVisibility(View.VISIBLE);
        }

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#304567"));
        colors.add(Color.parseColor("#309967"));
        colors.add(Color.parseColor("#476567"));
        colors.add(Color.parseColor("#890567"));
        colors.add(Color.parseColor("#a35567"));
        colors.add(Color.parseColor("#ff5f67"));
        colors.add(Color.parseColor("#3ca567"));

        //input data and fit data into pie chart entry
        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
//        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
//        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueTextSize(15f);
        pieDataSet.setValueTypeface(Typeface.DEFAULT_BOLD);
        pieDataSet.setValueTextColor(Color.WHITE);

        piechart.setDragDecelerationFrictionCoef(0.95f);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true);

        // on below line we are setting slice for pie
        pieDataSet.setSliceSpace(2f);
//        pieDataSet.iconsOffset = MPPointF(0f, 40f)
        pieDataSet.setSelectionShift(2f);

        piechart.setData(pieData);
        piechart.invalidate();

      /*  PieDataSet barDataSet = new PieDataSet(pieEntries, title);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData barData = new PieData(barDataSet);
        piechart.setData(barData);
        XAxis xAxis = piechart.getXAxis();
        xAxis.setLabelCount(entries.size(), false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelRotationAngle(-45);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        piechart.invalidate();*/

    }


    private void initializeDates() {
        //Date formats
        dateToServerFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat = new SimpleDateFormat("dd MMM yy");
        dayFormat = new SimpleDateFormat("EEE");

        //default start Date
        startDateCalendar= Calendar.getInstance();
        startDateDay=startDateCalendar.get(Calendar.DAY_OF_MONTH);
        startDateMonth=startDateCalendar.get(Calendar.MONTH);
        startDateYear=startDateCalendar.get(Calendar.YEAR);


        //default end Date
        endDateCalendar = Calendar.getInstance();
        endDateDay=endDateCalendar.get(Calendar.DAY_OF_MONTH);
        endDateMonth=endDateCalendar.get(Calendar.MONTH);
        endDateYear=endDateCalendar.get(Calendar.YEAR);

        startDateToSend=dateToServerFormat.format(startDateCalendar.getTime());
        endDateToSend=dateToServerFormat.format(endDateCalendar.getTime());

    }

    private void setDates() {
        //set default date
        startDateTv.setText(dayFormat.format(startDateCalendar.getTime())+" "+
                dateFormat.format(startDateCalendar.getTime())+ "   -   "+
                dayFormat.format(endDateCalendar.getTime())+" "+
                dateFormat.format(endDateCalendar.getTime())
        );

    }

    private void openFilterDialog() {
        filterDialog = new Dialog(requireContext());
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filterDialog.setContentView(R.layout.filter_dialog_layout);

        start_date_value_tv= (TextView) filterDialog.findViewById(R.id.start_date_value_tv);
        start_day_value_tv= (TextView) filterDialog.findViewById(R.id.start_day_value_tv);
        end_date_value_tv= (TextView) filterDialog.findViewById(R.id.end_date_value_tv);
        end_day_value_tv= (TextView) filterDialog.findViewById(R.id.end_day_value_tv);


        filterDialog.findViewById(R.id.cancel_tv).setOnClickListener(this);
        filterDialog.findViewById(R.id.submit_tv).setOnClickListener(this);
        filterDialog.findViewById(R.id.startDateLinear).setOnClickListener(this);
        filterDialog.findViewById(R.id.endDateLinear).setOnClickListener(this);

        start_date_value_tv.setText(dateFormat.format(startDateCalendar.getTime()));
        start_day_value_tv.setText(dayFormat.format(startDateCalendar.getTime()));

        end_date_value_tv.setText(dateFormat.format(endDateCalendar.getTime()));
        end_day_value_tv.setText(dayFormat.format(endDateCalendar.getTime()));

        filterDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.startDateLinear:
                try {
                    openStartDatePicker();
                }catch (Exception e){

                }

                break;
            case R.id.endDateLinear:
                try {
                    openEndDatePicker();
                }catch (Exception e){

                }

                break;
            case R.id.cancel_tv:
                if(filterDialog!=null)
                    filterDialog.dismiss();
                break;
            case R.id.lin_dateFilter:
                openFilterDialog();
                break;
            case R.id.submit_tv:

                long diff = endDateCalendar.getTime().getTime() - startDateCalendar.getTime().getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                if(days<0){
                    Toast.makeText(context, "you have selected wrong dates", Toast.LENGTH_SHORT).show();
                    break;
                }
                if(days>=30){
                    Toast.makeText(context, "please select 30 days data", Toast.LENGTH_SHORT).show();
                    break;
                }

                filterDialog.dismiss();

                //set date to fragment
                startDateTv.setText(dayFormat.format(startDateCalendar.getTime())+" "+
                        dateFormat.format(startDateCalendar.getTime())+ "   -   "+
                        dayFormat.format(endDateCalendar.getTime())+" "+
                        dateFormat.format(endDateCalendar.getTime())
                );

                getDashboardData();
                break;
        }
    }

    private void openStartDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        startDateCalendar.set(year, monthOfYear, dayOfMonth);

                        if (startDateCalendar.after(Calendar.getInstance())) {
                            Toast.makeText(context, "Can't select date after current date", Toast.LENGTH_SHORT).show();
                        }else {
                            startDateDay=dayOfMonth;
                            startDateMonth=monthOfYear;
                            startDateYear=year;

                            start_date_value_tv.setText(dateFormat.format(startDateCalendar.getTime()));
                            start_day_value_tv.setText(dayFormat.format(startDateCalendar.getTime()));

                            startDateToSend=dateToServerFormat.format(startDateCalendar.getTime());
                        }

                    }

                },startDateYear, startDateMonth, startDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();


    }

    private void openEndDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endDateCalendar = Calendar.getInstance();
                        endDateCalendar.set(year, monthOfYear, dayOfMonth);

                        if (endDateCalendar.after(Calendar.getInstance())) {
                            Toast.makeText(context, "Can't select date after current date", Toast.LENGTH_SHORT).show();
                        }else {
                            endDateDay=dayOfMonth;
                            endDateMonth=monthOfYear;
                            endDateYear=year;

                            end_date_value_tv.setText(dateFormat.format(endDateCalendar.getTime()));
                            end_day_value_tv.setText(dayFormat.format(endDateCalendar.getTime()));

                            endDateToSend=dateToServerFormat.format(endDateCalendar.getTime());
                        }


                    }

                },endDateYear, endDateMonth, endDateDay);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();


    }

}