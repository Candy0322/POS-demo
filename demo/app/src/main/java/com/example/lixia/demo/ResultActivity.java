package com.example.lixia.demo;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cardinfolink.pos.listener.Callback;
import com.cardinfolink.pos.sdk.CILSDK;
import com.cardinfolink.pos.sdk.model.Trans;
import com.newland.mtype.module.common.printer.PrinterResult;

public class ResultActivity extends AppCompatActivity {
    public static final String EXCHANGE_RESULT = "exchange_result";
    private TextView mResultTv;
    private Trans trans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mResultTv = (TextView) findViewById(R.id.result);
        trans = getIntent().getParcelableExtra(EXCHANGE_RESULT);
        if (trans != null && TextUtils.equals(trans.getRespCode(), "00")) {
            mResultTv.setText("成功\n" + "交易详情：\n" +
                    trans.toString());
        } else if (trans != null) {
            mResultTv.setText("失败\n" + "交易详情：\n" + trans.toString());
        } else {
            mResultTv.setText("失败\n");
        }
    }

    /**
     * 打印交易小票
     */
    private void printKindsReceipt(final Trans trans) {
        if (trans == null || TextUtils.isEmpty(trans.getTransCode())) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean isForeignCard =!TextUtils.equals(trans.getBillingCurr(), "156");
                CILSDK.printKindsReceipts(trans, 3, trans.getTransCode(), CILSDK.RECEIPT_CUSTOMER, isForeignCard, null, new Callback<PrinterResult>() {
                    @Override
                    public void onResult(PrinterResult printerResult) {
                        if (null == printerResult || !"打印成功".equals(printerResult.toString())) {
                        }
                    }

                    @Override
                    public void onError(Parcelable p, Exception e) {
                    }
                });
            }
        }).start();
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.printKindsReceipts:
                printKindsReceipt(trans);
                break;
        }
    }
}
