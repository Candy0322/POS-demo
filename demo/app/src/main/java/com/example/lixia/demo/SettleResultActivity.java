package com.example.lixia.demo;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.cardinfolink.pos.listener.Callback;
import com.cardinfolink.pos.sdk.CILResponse;
import com.cardinfolink.pos.sdk.CILSDK;
import com.cardinfolink.pos.sdk.constant.TransConstants;
import com.cardinfolink.pos.sdk.model.TransSettle;
import com.newland.mtype.module.common.printer.PrinterResult;

import java.util.ArrayList;
import java.util.List;

public class SettleResultActivity extends AppCompatActivity {
    private TextView mResultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_result);
        mResultTv = (TextView) findViewById(R.id.settleResult);
        CILResponse.Info data = getIntent().getParcelableExtra(ResultActivity.EXCHANGE_RESULT);
        List<TransSettle> transSettleList = new ArrayList<>();
        double amount = 0;
        if (data != null && (transSettleList = data.getTransSettleList()) != null) {
            for (TransSettle transSettle : transSettleList) {
                amount += Double.parseDouble(transSettle.getTransAmt());
            }
            mResultTv.setText("成功\n" + "结算金额总计：\n" + amount);
        } else {
            mResultTv.setText("最近没有结算信息");
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.printKindsReceipts:
                break;
        }
    }

    /**
     * 打印结算小票
     */
    private void printKindsReceipt(final CILResponse.Info info) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CILSDK.printSettleReceipts(info.getTransSettleList(), info.getDatetime(), info.getTermBatchId(), 4,
                        TransConstants.TRANS_SETTLE_DETAILS, new Callback<PrinterResult>() {
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
}
