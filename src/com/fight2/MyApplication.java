package com.fight2;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender.Method;
import org.acra.sender.HttpSender.Type;

import android.app.Application;

@ReportsCrashes(formKey = "", httpMethod = Method.PUT, reportType = Type.JSON, formUri = "http://112.124.37.194:5984/acra-fight2/_design/acra-storage/_update/report", formUriBasicAuthLogin = "reporter", formUriBasicAuthPassword = "524400")
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
    }
}
