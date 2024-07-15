package com.justclick.clicknbook.jctPayment.Models;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class AepsListResponseModel implements Serializable {
    public String statusCode, statusMessage;
    private int totalCount;

    public int getTotalCount() {
        return totalCount;
    }

    @Nullable
    public ArrayList<transactionListDetail> transactionListDetail;

    public class transactionListDetail{
        public String jckTransactionId, rowNum, transactionId, agentCode, createdDate,
                txnStatusDesc, txnStatus, distCode, distName, txnType,rechargeType,
                bankName, mobile, apiStatusMessage, apiStatusCode, accountNo, rrn,
                cardHolderName, txnAmount, balanceAmount;
    }
}
