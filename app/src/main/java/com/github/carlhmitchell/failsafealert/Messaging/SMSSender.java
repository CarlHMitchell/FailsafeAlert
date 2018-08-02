package com.github.carlhmitchell.failsafealert.Messaging;

import android.Manifest;
import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.github.carlhmitchell.failsafealert.utilities.ToastHelper;
import com.intentfilter.androidpermissions.PermissionManager;

import static java.util.Collections.singleton;

public class SMSSender {
    public static void sendSMS(final Context context,  final String phoneNumber, final String message) {
        final SmsManager sms = SmsManager.getDefault();
        PermissionManager permissionManager = PermissionManager.getInstance(context);
        permissionManager.checkPermissions(singleton(Manifest.permission.SEND_SMS), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                ToastHelper.toast(context, "Permissions Granted", Toast.LENGTH_SHORT);
                sms.sendTextMessage(phoneNumber, null, message, null, null);
            }

            @Override
            public void onPermissionDenied() {
                ToastHelper.toast(context, "Permissions Denied", Toast.LENGTH_SHORT);
            }
        });
    }
}
