package com.justclick.clicknbook.myinterface;

import android.widget.ListView;

import com.justclick.clicknbook.utils.CodeEnum;

/**
 * Created by pc1 on 4/18/2017.
 */

public interface FragmentBackPressListener{
    void onFragmentBackPress(ListView title);
    void onJctDetailBackPress(CodeEnum classType);
    void onBusBackPress(boolean isHome);
}
