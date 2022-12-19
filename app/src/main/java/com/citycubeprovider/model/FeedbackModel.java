package com.citycubeprovider.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedbackModel {

    @SerializedName("result")
    @Expose
    public List<Result> result = null;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("status")
    @Expose
    public String status;

    public class Result {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("request_id")
        @Expose
        public String requestId;
        @SerializedName("driver_id")
        @Expose
        public String driverId;
        @SerializedName("user_id")
        @Expose
        public String userId;
        @SerializedName("rating")
        @Expose
        public String rating;
        @SerializedName("review")
        @Expose
        public String review;
        @SerializedName("date_time")
        @Expose
        public String dateTime;
        @SerializedName("user_detail")
        @Expose
        public UserDetail userDetail;

        public class UserDetail {

            @SerializedName("id")
            @Expose
            public String id;
            @SerializedName("type")
            @Expose
            public String type;
            @SerializedName("user_name")
            @Expose
            public String userName;
            @SerializedName("user_image")
            @Expose
            public String userImage;



        }

    }


}







