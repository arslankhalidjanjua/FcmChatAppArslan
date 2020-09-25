package com.example.chatappfcm.Activities.Notify;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAADgFjDMg:APA91bHkCre-dypu5VhpzXEz7W3wWCRlnqyOF7ElARZvgqOW9ByEMY0tTGTlrAX0Hx_-AoAvEaclp0zDcmmx3_kGYF18p3lZIN8u-yxX0rmeAVAzscZ02qfFf4zBrOMjYSckWNXhM-N0\n"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}