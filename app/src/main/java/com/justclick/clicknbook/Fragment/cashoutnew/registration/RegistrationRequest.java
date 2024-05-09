package com.justclick.clicknbook.Fragment.cashoutnew.registration;

import android.os.Parcel;
import android.os.Parcelable;

public class RegistrationRequest implements Parcelable {
    //Required
    public String salutation, firstname, lastname, email, mobile;
    public String usertype, companyname, state, city, country, pincode, address, userpin, gstnumber;
    public String addressproof, addproofnumber, pancardname, pannumber;
    public String visited, MerchantId,
            bookuusertype, bookuserid, bookvalidationcode, ipAddress, hostName;
    //Documents
    public String addproofdocument, panfile, shopexterior, picwithsaleperson, agencyAddressProof;

    public String stdcode, landline, salepersonjctid, remark;

    protected RegistrationRequest(Parcel in) {
        salutation = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        email = in.readString();
        mobile = in.readString();
        usertype = in.readString();
        companyname = in.readString();
        state = in.readString();
        city = in.readString();
        country = in.readString();
        pincode = in.readString();
        address = in.readString();
        userpin = in.readString();
        gstnumber = in.readString();
        addressproof = in.readString();
        addproofnumber = in.readString();
        pancardname = in.readString();
        pannumber = in.readString();
        visited = in.readString();
        MerchantId = in.readString();
        bookuusertype = in.readString();
        bookuserid = in.readString();
        bookvalidationcode = in.readString();
        ipAddress = in.readString();
        hostName = in.readString();
        addproofdocument = in.readString();
        panfile = in.readString();
        shopexterior = in.readString();
        picwithsaleperson = in.readString();
        agencyAddressProof = in.readString();
        stdcode = in.readString();
        landline = in.readString();
        salepersonjctid = in.readString();
        remark = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(salutation);
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeString(usertype);
        dest.writeString(companyname);
        dest.writeString(state);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(pincode);
        dest.writeString(address);
        dest.writeString(userpin);
        dest.writeString(gstnumber);
        dest.writeString(addressproof);
        dest.writeString(addproofnumber);
        dest.writeString(pancardname);
        dest.writeString(pannumber);
        dest.writeString(visited);
        dest.writeString(MerchantId);
        dest.writeString(bookuusertype);
        dest.writeString(bookuserid);
        dest.writeString(bookvalidationcode);
        dest.writeString(ipAddress);
        dest.writeString(hostName);
        dest.writeString(addproofdocument);
        dest.writeString(panfile);
        dest.writeString(shopexterior);
        dest.writeString(picwithsaleperson);
        dest.writeString(agencyAddressProof);
        dest.writeString(stdcode);
        dest.writeString(landline);
        dest.writeString(salepersonjctid);
        dest.writeString(remark);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RegistrationRequest> CREATOR = new Creator<RegistrationRequest>() {
        @Override
        public RegistrationRequest createFromParcel(Parcel in) {
            return new RegistrationRequest(in);
        }

        @Override
        public RegistrationRequest[] newArray(int size) {
            return new RegistrationRequest[size];
        }
    };
}
