package com.justclick.clicknbook.jctPayment.Models;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class AepsBankListModel implements Serializable {
    public Boolean status;
    public banklist banklist;

    public class data{
        public String id, bankName, iinno;
    }

    public class banklist{
        @Nullable
        public ArrayList<data> data;
    }
}
