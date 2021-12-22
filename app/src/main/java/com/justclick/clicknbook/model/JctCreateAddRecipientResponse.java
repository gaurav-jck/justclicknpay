package com.justclick.clicknbook.model;

import java.io.Serializable;

/**
 * Created by Lenovo on 04/13/2017.
 */

public class JctCreateAddRecipientResponse implements Serializable
{
    public String message, recipient_id_type,recipient_id,ifsc_status,account,name,ifsc,ValF,ValName;
    public int statusCode;
}

