package com.example.cloverprintdemo;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.ServiceConnector;
import com.clover.sdk.v1.printer.job.ViewPrintJob;

import java.util.Locale;

import static com.clover.sdk.v1.ServiceConnector.OnServiceConnectedListener;

public class MainActivity extends AppCompatActivity implements OnServiceConnectedListener {
    public static final String TAG = "ServiceConnector";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onPrint(View view) {
        final ViewGroup root = (ViewGroup) getWindow().getDecorView().getRootView();
        final View inflatedView = LayoutInflater.from(this).inflate(R.layout.layout_print, root, false);
        inflatedView.setVisibility(View.INVISIBLE);
        root.addView(inflatedView);

        final LinearLayout llPrint = findViewById(R.id.ll_print);

        TextView heading = new TextView(this);
        String merchantName = "Example Merchant";
        heading.setText(merchantName);
        heading.setGravity(Gravity.CENTER);
        heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
        llPrint.addView(heading);

        for (int i = 0; i < 16; ++i) {
            String name = "Item " + i;
            String value = String.format(Locale.getDefault(), "%.2f", (float) i);
            addRow(llPrint, name, value);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new ViewPrintJob.Builder()
                            .view(llPrint)
                            .printToAny(true)
                            .build()
                            .print(MainActivity.this,
                                    CloverAccount.getAccount(MainActivity.this));
                    MainActivity.this.runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    root.removeView(inflatedView);
                                }
                            }
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addRow(LinearLayout layout, String name, String value) {
        RelativeLayout row = new RelativeLayout(this);

        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextSize(26);
        row.addView(tvName);

        TextView tvValue = new TextView(this);
        tvValue.setText(value);
        tvValue.setTextSize(26);
        tvValue.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        params.addRule(RelativeLayout.RIGHT_OF, tvName.getId());
        params.addRule(RelativeLayout.ALIGN_BOTTOM, tvName.getId());
        row.addView(tvValue, params);

        layout.addView(row);
    }

    @Override
    public void onServiceConnected(ServiceConnector connector) {
        Log.i(TAG, "Service connected: " + connector);
    }

    @Override
    public void onServiceDisconnected(ServiceConnector connector) {
        Log.i(TAG, "Service disconnected: " + connector);
    }
}
