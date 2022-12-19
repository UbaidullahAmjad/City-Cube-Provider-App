package com.citycubeprovider.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CarListModel {

    @SerializedName("result")
    @Expose
    public List<Result> result = null;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("message")
    @Expose
    public String message;

    public class Result {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("car_name")
        @Expose
        public String carName;
        @SerializedName("car_image")
        @Expose
        public String carImage;
        @SerializedName("charge")
        @Expose
        public String charge;
        @SerializedName("no_of_seats")
        @Expose
        public String noOfSeats;
        @SerializedName("min_charge")
        @Expose
        public String minCharge;
        @SerializedName("per_km")
        @Expose
        public String perKm;
        @SerializedName("hold_charge")
        @Expose
        public String holdCharge;
        @SerializedName("ride_time_charge_permin")
        @Expose
        public String rideTimeChargePermin;
        @SerializedName("service_tax")
        @Expose
        public String serviceTax;
        @SerializedName("free_time_min")
        @Expose
        public String freeTimeMin;
        @SerializedName("status")
        @Expose
        public String status;


        @SerializedName("chk")
        @Expose
        public boolean chk;

        public boolean isChk() {
            return chk;
        }

        public void setChk(boolean chk) {
            this.chk = chk;
        }
    }

}



