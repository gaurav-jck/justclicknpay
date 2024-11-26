package com.justclick.clicknbook.Fragment.changepassword;

public class ChangePasswordResponse {
    public String status, description;
    public errors errors;

    public class errors{
        public String[] newpassword;
    }
}
