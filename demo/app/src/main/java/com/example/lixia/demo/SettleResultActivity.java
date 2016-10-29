package com.example.lixia.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.cardinfolink.pos.sdk.CILResponse;
import com.cardinfolink.pos.sdk.model.Trans;

public class SettleResultActivity extends AppCompatActivity {
    private TextView mResultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settle_result);
        mResultTv = (TextView) findViewById(R.id.settleResult);
        CILResponse.Info data = getIntent().getParcelableExtra(ResultActivity.EXCHANGE_RESULT);
        if (data != null) {
            mResultTv.setText("成功\n" + "交易详情：\n" +data.getTermBatchId());
        } else {
            mResultTv.setText("失败\n");
        }


    }
}
