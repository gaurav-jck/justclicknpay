package com.justclick.clicknbook.jctPayment.Utilities;

import com.justclick.clicknbook.jctPayment.Models.PIDXMLValues;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class XMLParser extends DefaultHandler {

    public List<PIDXMLValues> list=null;

    // string builder acts as a buffer
    StringBuilder builder;

    PIDXMLValues jobsValues=null;


    @Override
    public void startDocument() throws SAXException {
        /******* Create ArrayList To Store XmlValuesModel object ******/
        list = new ArrayList<PIDXMLValues>();
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        // Create StringBuilder object to store xml node value
        builder=new StringBuilder();

        if(localName.equals("PidData")){
            //Log.i("parse","====login=====");
        }
        else if(localName.equals("Resp")){
            // Log.i("parse","====status=====");
            jobsValues = new PIDXMLValues();
        }
        else if(localName.equalsIgnoreCase("errCode")){
            jobsValues.setErrCode(builder.toString());
        }
        else if(localName.equalsIgnoreCase("errInfo")){
            jobsValues.setErrInfo(builder.toString());
        }

    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if(localName.equals("Resp")){
            list.add( jobsValues );
        }
        else  if(localName.equalsIgnoreCase("status")){

        }
        else if(localName.equalsIgnoreCase("errCode")){
            jobsValues.setErrCode(builder.toString());
        }
        else if(localName.equalsIgnoreCase("errInfo")){
            jobsValues.setErrInfo(builder.toString());
        }

        // Log.i("parse",localName.toString()+"========="+builder.toString());
    }


    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        /******  Read the characters and append them to the buffer  ******/
        String tempString=new String(ch, start, length);
        builder.append(tempString);
    }


}
