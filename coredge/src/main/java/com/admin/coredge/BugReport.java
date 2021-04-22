package com.admin.coredge;

import android.app.Application;
import android.app.job.JobInfo;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraHttpSender;
import org.acra.annotation.AcraMailSender;
import org.acra.annotation.AcraToast;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.HttpSenderConfigurationBuilder;
import org.acra.config.LimiterConfigurationBuilder;
import org.acra.config.SchedulerConfigurationBuilder;
import org.acra.config.ToastConfigurationBuilder;
import org.acra.data.StringFormat;
import org.acra.sender.HttpSender;

@AcraHttpSender(uri = "http://159.65.144.39:9090/report" /*best guess, you may need to adjust this*/,
        basicAuthLogin = "2G5pLuPd9GS7msfV",
        basicAuthPassword = "FECBPaO99So0LZUW",
        httpMethod = HttpSender.Method.POST)
@AcraCore(reportFormat = StringFormat.JSON)

public class BugReport extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}
