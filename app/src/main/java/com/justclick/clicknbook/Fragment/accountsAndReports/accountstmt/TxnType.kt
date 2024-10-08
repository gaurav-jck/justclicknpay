package com.justclick.clicknbook.Fragment.accountsAndReports.accountstmt

class TxnType {
    companion object{
//        DMT
        val DMTBooking="DMTW Booking"
        val DMTRefund="DMTW Refund"
        val DMTValidation="DMTW Validation"
        val DMTValidationRefund="DMTW Validation Refund"
//        MATM
        val MATMBooking="MATM Booking"
        val MATMQRTXN="QRTXN"
        val MATMQRActivate="QRActivate"
        val MATMOneTime="MATM One Time"
//        Recharge
        val RechargeLICPayment="LIC Payment"
        val RechargeLICRefund="LIC Refund"
        val RechargeUtilityPayment="Utility Payment"
        val RechargeUtilityRefund="Utility Refund"
        val RechargeFastagPayment="Fastag Payment"
        val RechargeFastagRefund="Fastag Refund"
        val RechargePolicy="Policy"
        val RechargePolicyRejected="Policy Rejected"
        val Recharge="Recharge"
        val RechargePancardRefund="Pancard Refund"
        val RechargeInsurance="Insurance"
        val RechargePancardPayment="Pancard Payment"

//        Aeps
        val AepsACPBooking="ACP Booking"
        val AepsACWBooking="ACW Booking"
        val AepsMiniStatement="Mini Statement"
        val AepsOneTime="AEPS One Time"
        val AepsAMSBooking="AMS Booking"
        val AepsPayoutReversal="Payout Reversal"
        val AepsPayout="Payout"
    }
}