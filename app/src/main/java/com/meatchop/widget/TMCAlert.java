/*$Id$*/
package com.meatchop.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;

import com.meatchop.R;


public class TMCAlert extends AlertDialog.Builder {
    public TMCAlert(Context context, int title, int message, int btn1Caption, int btn2Caption, final AlertListener listener) {
        super(context);
        Typeface opensansboldfont = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
        Typeface opensansregfont = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        if (title != 0) {
            TextView titleView = new TextView(context);
            titleView.setTypeface(opensansboldfont);
            titleView.setTextSize(18);
            titleView.setPadding(60,50,0,0);
            titleView.setTextColor(context.getResources().getColor(R.color.black));
            titleView.setText(context.getResources().getString(title));
            setCustomTitle(titleView);
        }
        TextView messageView = new TextView(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            messageView.setPadding(60,30,0,20);
        } else {
            messageView.setPadding(10,20,10,10);
        }
        messageView.setTypeface(opensansregfont);
        messageView.setText(message);
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.GINGERBREAD_MR1){
            messageView.setTextColor(context.getResources().getColor(R.color.primary_text_color));
        }else{
            messageView.setTextColor(context.getResources().getColor(android.R.color.white));
        }

     // titleView.setGravity(Gravity.CENTER);
        messageView.setTextSize(15);
        setView(messageView);
        //setMessage(context.getResources().getString(message));
        setPositiveButton(context.getResources().getString(btn1Caption), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onYes();
            }
        });

        if (btn2Caption != 0) {
            setNegativeButton(context.getResources().getString(btn2Caption), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    listener.onNo();
                }
            });
        }
        try {
            show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public TMCAlert(Context context, String title, String message, String btn1Caption, String btn2Caption, final AlertListener listener) {
        super(context);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
        TextView titleView = new TextView(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            titleView.setPadding(60, 40, 40, 40);
        } else {
            titleView.setPadding(10,20,10,10);
        }
        titleView.setText(message);
        if (message.length() > 50) {
            titleView.setGravity(Gravity.LEFT);
        } else {
            titleView.setGravity(Gravity.CENTER);
        }

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.GINGERBREAD_MR1){
            titleView.setTextColor(context.getResources().getColor(android.R.color.black));
        }else{
            titleView.setTextColor(context.getResources().getColor(android.R.color.white));
        }
        titleView.setTextSize(14);
        setView(titleView);
        //setMessage(message);
        setPositiveButton(btn1Caption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onYes();
            }
        });

        if (!btn2Caption.equals("")) {
            setNegativeButton(btn2Caption, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    listener.onNo();
                }
            });
        }
        try {
            show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public TMCAlert(Context context, int title, int message, int btn1Caption, int btn2Caption, int btn3Caption, final AlertListener2 listener) {
        super(context);
     /* if (title != 0) {
            setTitle(context.getResources().getString(title));
        } */
        TextView titleView = new TextView(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            titleView.setPadding(30,40,30,40);
        } else {
            titleView.setPadding(10,20,10,10);
        }
        titleView.setText(message);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            titleView.setTextColor(context.getResources().getColor(android.R.color.black));
        } else {
            titleView.setTextColor(context.getResources().getColor(android.R.color.white));
        }

        titleView.setGravity(Gravity.CENTER);
        titleView.setTextSize(16);
        setView(titleView);
        setPositiveButton(context.getResources().getString(btn1Caption), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onYes();
            }
        });

        if (btn2Caption != 0) {
            setNegativeButton(context.getResources().getString(btn2Caption), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    listener.onNo();
                }
            });
        }

        if (btn3Caption != 0) {
            setNeutralButton(context.getResources().getString(btn3Caption), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    listener.onNeutral();
                }
            });
        }
        try {
            show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public TMCAlert(Context context, String title, String message, String btn1Caption, String btn2Caption, boolean isexternalvendor, final AlertListener listener) {
        super(context);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
        TextView titleView = new TextView(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            titleView.setPadding(30, 40, 30, 40);
        } else {
            titleView.setPadding(10,20,10,10);
        }
        titleView.setText(message);
        if (message.length() > 50) {
            titleView.setGravity(Gravity.LEFT);
        } else {
            titleView.setGravity(Gravity.CENTER);
        }

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.GINGERBREAD_MR1){
            titleView.setTextColor(context.getResources().getColor(android.R.color.black));
        }else{
            titleView.setTextColor(context.getResources().getColor(android.R.color.white));
        }
        titleView.setTextSize(14);
        setView(titleView);
        //setMessage(message);
        setPositiveButton(btn1Caption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onYes();
            }
        });

        if (!btn2Caption.equals("")) {
            setNegativeButton(btn2Caption, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    listener.onNo();
                }
            });
        }
        try {
            show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public interface AlertListener {
        public void onYes();

        public void onNo();
    }

    public interface AlertListener2 {
        public void onYes();

        public void onNo();

        public void onNeutral();
    }
}