package com.justclick.clicknbook.jctPayment.Models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "CustOpts")
public class CustOpts {

    public CustOpts() {
    }

    @ElementList(name = "Param", required = false, inline = true)
    public List<Param> params;
}
