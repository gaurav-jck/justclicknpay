package com.justclick.clicknbook.model;

import java.util.ArrayList;

public class LoginModel
{
    public String StatusCode, Status;
    public Data Data;
    public String LoginSessionId;
    public class Data{
        public String UserData,Email,UserType,Mobile,Name, MerchantID,MerchantUrl,
                RefAgency,ValidationCode,AgencyName, DoneCardUser, BankNames, ModuleAccess,
                CreditFlag, UserId, remainpassdays;
    }

    public ArrayList<DataList> DataList;
    public ArrayList<ProductList> ProductList;

    public class DataList {
        public String Mainmenu,MainmenuCode;
        public ArrayList<subMenu> subMenu;

        public class subMenu {
            public String SubMenu,SubMenuCode;
        }
    }

    public class ProductList {
        public String Active, Product, ProductCode;
    }

}
