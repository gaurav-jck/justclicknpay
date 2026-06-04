package com.justclick.clicknbook.Fragment.jctmoney.instapay.model

import java.io.Serializable

data class KycData(
    val outletId: String,
    val outletAadhaarNumber: String,
    val pidOptionWadh: String,
    val referenceKey: String,
    val referenceKeyType: String,
    val isServiceAvailable: String,
    val isBiometricKycMandatory: String,
    val isFaceAuthAvailable: String,
    val status: String,
    val action: String,
    val bankName: String,
):Serializable
