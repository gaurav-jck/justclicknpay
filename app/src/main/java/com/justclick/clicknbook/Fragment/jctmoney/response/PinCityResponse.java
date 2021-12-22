package com.justclick.clicknbook.Fragment.jctmoney.response;

import java.util.ArrayList;

public class PinCityResponse {
    private String Status, Message;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    private ArrayList<PostOffice> PostOffice;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public ArrayList<PinCityResponse.PostOffice> getPostOffice() {
        return PostOffice;
    }

    public void setPostOffice(ArrayList<PinCityResponse.PostOffice> postOffice) {
        PostOffice = postOffice;
    }

    public class PostOffice{
        private String Name,BranchType,Block,Circle,DeliveryStatus,Description,
                District,Division,Region,State,Country,Pincode;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getBranchType() {
            return BranchType;
        }

        public void setBranchType(String branchType) {
            BranchType = branchType;
        }

        public String getBlock() {
            return Block;
        }

        public void setBlock(String block) {
            Block = block;
        }

        public String getCircle() {
            return Circle;
        }

        public void setCircle(String circle) {
            Circle = circle;
        }

        public String getDeliveryStatus() {
            return DeliveryStatus;
        }

        public void setDeliveryStatus(String deliveryStatus) {
            DeliveryStatus = deliveryStatus;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getDistrict() {
            return District;
        }

        public void setDistrict(String district) {
            District = district;
        }

        public String getDivision() {
            return Division;
        }

        public void setDivision(String division) {
            Division = division;
        }

        public String getRegion() {
            return Region;
        }

        public void setRegion(String region) {
            Region = region;
        }

        public String getState() {
            return State;
        }

        public void setState(String state) {
            State = state;
        }

        public String getCountry() {
            return Country;
        }

        public void setCountry(String country) {
            Country = country;
        }

        public String getPincode() {
            return Pincode;
        }

        public void setPincode(String pincode) {
            Pincode = pincode;
        }
    }
}
