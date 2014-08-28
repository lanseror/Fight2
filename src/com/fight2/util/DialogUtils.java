package com.fight2.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;

import com.fight2.GameActivity;

public class DialogUtils {

    public static void ConfirmDialog(final GameActivity activity, final String title, final String message, final OnClickListener onConfirmListener) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle(title);
                alert.setMessage(message);

                alert.setPositiveButton("确定", onConfirmListener);

                alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int whichButton) {

                    }
                });

                final AlertDialog dialog = alert.create();
                dialog.setOnShowListener(new OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                    }
                });
                dialog.show();
            }
        });
    }

}
