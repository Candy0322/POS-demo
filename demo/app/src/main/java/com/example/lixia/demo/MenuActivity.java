package com.example.lixia.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cardinfolink.pos.listener.Callback;
import com.cardinfolink.pos.sdk.CILRequest;
import com.cardinfolink.pos.sdk.CILResponse;
import com.cardinfolink.pos.sdk.CILSDK;
import com.example.lixia.demo.utils.Utils;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mMoneyText;
    private EditText mReferenceNumber;
    private EditText mTransDatetime;
    private EditText mTracenum;
    private EditText mBatchNum;
    private EditText mRevAuthCode;
    private EditText mCardNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //获取控件
        mMoneyText = (EditText) findViewById(R.id.money);
        mReferenceNumber = (EditText) findViewById(R.id.referenceNumber);
        mTransDatetime = (EditText) findViewById(R.id.transDatetime);
        mTracenum = (EditText) findViewById(R.id.tracenum);
        mBatchNum = (EditText) findViewById(R.id.batchNum);
        mRevAuthCode = (EditText) findViewById(R.id.revAuthCode);
        mCardNum = (EditText) findViewById(R.id.cardNum);

        //设置监听
        findViewById(R.id.cardDeal).setOnClickListener(this);
        findViewById(R.id.balance).setOnClickListener(this);
        findViewById(R.id.preAuth).setOnClickListener(this);
        findViewById(R.id.returnQr).setOnClickListener(this);
        findViewById(R.id.revokeQr).setOnClickListener(this);
        findViewById(R.id.revokeConsume).setOnClickListener(this);
        findViewById(R.id.consumeQr).setOnClickListener(this);
        findViewById(R.id.returnConsume).setOnClickListener(this);
        findViewById(R.id.revokePreAuth).setOnClickListener(this);
        findViewById(R.id.preAuthComplete).setOnClickListener(this);
        findViewById(R.id.revokePreAuthComplete).setOnClickListener(this);
        findViewById(R.id.takeTip).setOnClickListener(this);
        findViewById(R.id.revokeTip).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardDeal:
                startDeal("consume");
                break;
            case R.id.returnConsume:
                returnConsume();
                break;
            case R.id.revokeConsume:
                revokeConsume();
                break;
            case R.id.balance:
                mMoneyText.setText("0.001");
                startDeal("checkBalance");
                break;
            case R.id.preAuth:
                startDeal("preAuth");
                break;
            case R.id.revokePreAuth:
                revokePreAuth();
                break;
            case R.id.preAuthComplete:
                preAuthComplete();
                break;
            case R.id.revokePreAuthComplete:
                revokePreAuthComplete();
                break;
            case R.id.consumeQr:
                startCapture();
                break;
            case R.id.returnQr:
                returnQr();
                break;
            case R.id.revokeQr:
                revokeQr();
                break;
            case R.id.takeTip:
                takeTip();
                break;
            case R.id.revokeTip:
                revokeTip();
                break;


        }

    }


    /**
     * @author:lixia 小费撤销
     */
    private void revokeTip() {
        String amout = mMoneyText.getText().toString();
        String transDatetime = mTransDatetime.getText().toString();
        String revAuthCode = mRevAuthCode.getText().toString();
        String tracenum = mTracenum.getText().toString();
        String batchNum = mBatchNum.getText().toString();
        String referenceNumber = mReferenceNumber.getText().toString();
        String cardNum = mCardNum.getText().toString();
        if (TextUtils.isEmpty(transDatetime) || TextUtils.isEmpty(revAuthCode) || TextUtils.isEmpty(tracenum) || TextUtils.isEmpty(batchNum) || TextUtils.isEmpty(amout)
                || TextUtils.isEmpty(referenceNumber) || TextUtils.isEmpty(cardNum)) {
            Toast.makeText(this, "金额/授权号/交易时间/凭证号/批次号/参考号/卡号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        CILRequest request = new CILRequest();
        request.setReferenceNumber(referenceNumber);
        request.setAmount(amout);
        request.setTransDatetime(transDatetime);
        request.setRevAuthCode(revAuthCode);
        request.setBatchNum(batchNum);
        request.setTraceNum(tracenum);
        request.setCardNum(cardNum);
        CILSDK.revokeTip(request, new Callback<CILResponse>() {

            @Override
            public void onResult(CILResponse cilResponse) {
                Utils.startResultActivity(MenuActivity.this, cilResponse.getTrans());
            }

            @Override
            public void onError(Parcelable parcelable, Exception e) {
                Utils.startResultActivity(MenuActivity.this, Utils.assembleTransWithRequest(parcelable));
            }
        });
    }


    /**
     * @author: lixia   小费
     */
    private void takeTip() {
        String amout = mMoneyText.getText().toString();
        String transDatetime = mTransDatetime.getText().toString();
        String revAuthCode = mRevAuthCode.getText().toString();
        String tracenum = mTracenum.getText().toString();
        String batchNum = mBatchNum.getText().toString();
        String referenceNumber = mReferenceNumber.getText().toString();
        String cardNum = mCardNum.getText().toString();
        if (TextUtils.isEmpty(transDatetime) || TextUtils.isEmpty(revAuthCode) || TextUtils.isEmpty(tracenum) || TextUtils.isEmpty(batchNum) || TextUtils.isEmpty(amout)
                || TextUtils.isEmpty(referenceNumber) || TextUtils.isEmpty(cardNum)) {
            Toast.makeText(this, "金额/授权号/交易时间/凭证号/批次号/参考号/卡号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        CILRequest request = new CILRequest();
        request.setReferenceNumber(referenceNumber);
        request.setAmount(amout);
        request.setTransDatetime(transDatetime);
        request.setRevAuthCode(revAuthCode);
        request.setBatchNum(batchNum);
        request.setTraceNum(tracenum);
        request.setCardNum(cardNum);
        CILSDK.takeTip(request, new Callback<CILResponse>() {

            @Override
            public void onResult(CILResponse cilResponse) {
                Utils.startResultActivity(MenuActivity.this, cilResponse.getTrans());
            }

            @Override
            public void onError(Parcelable parcelable, Exception e) {
                Utils.startResultActivity(MenuActivity.this, Utils.assembleTransWithRequest(parcelable));
            }
        });

    }

    /**
     * @author:lixia 预授权完成撤销
     */
    private void revokePreAuthComplete() {
        String amout = mMoneyText.getText().toString();
        String transDatetime = mTransDatetime.getText().toString();
        String revAuthCode = mRevAuthCode.getText().toString();
        String tracenum = mTracenum.getText().toString();
        String batchNum = mBatchNum.getText().toString();
        String referenceNumber = mReferenceNumber.getText().toString();

        if (TextUtils.isEmpty(transDatetime) || TextUtils.isEmpty(revAuthCode) || TextUtils.isEmpty(tracenum) || TextUtils.isEmpty(batchNum) || TextUtils.isEmpty(amout) || TextUtils.isEmpty(referenceNumber)) {
            Toast.makeText(this, "金额/授权号/交易时间/凭证号/批次号/参考号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, CommonCardHandlerActivity.class);
        intent.putExtra(CommonCardHandlerActivity.MENOY_AMOUT, amout);
        intent.putExtra(CommonCardHandlerActivity.TRANS_TIME, transDatetime);
        intent.putExtra(CommonCardHandlerActivity.REVAUTH_CODE, revAuthCode);
        intent.putExtra(CommonCardHandlerActivity.TRACE_NUM, tracenum);
        intent.putExtra(CommonCardHandlerActivity.BATCH_NUM, batchNum);
        intent.putExtra(CommonCardHandlerActivity.REFE_NUMBER, referenceNumber);
        intent.putExtra(CommonCardHandlerActivity.EXCHANGE_TYPE, "revokePreAuthComplete");
        startActivity(intent);

    }

    /**
     * @author:lixia 预授权完成
     */
    private void preAuthComplete() {
        String amout = mMoneyText.getText().toString();
        String transDatetime = mTransDatetime.getText().toString();
        String revAuthCode = mRevAuthCode.getText().toString();

        if (TextUtils.isEmpty(transDatetime) || TextUtils.isEmpty(revAuthCode) || TextUtils.isEmpty(amout)) {
            Toast.makeText(this, "金额/授权号/交易时间/凭证号/批次号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, CommonCardHandlerActivity.class);
        intent.putExtra(CommonCardHandlerActivity.MENOY_AMOUT, amout);
        intent.putExtra(CommonCardHandlerActivity.TRANS_TIME, transDatetime);
        intent.putExtra(CommonCardHandlerActivity.REVAUTH_CODE, revAuthCode);
        intent.putExtra(CommonCardHandlerActivity.EXCHANGE_TYPE, "preAuthComplete");
        startActivity(intent);

    }

    /**
     * @author:lixia 预授权撤销
     */
    private void revokePreAuth() {

        String amout = mMoneyText.getText().toString();
        String transDatetime = mTransDatetime.getText().toString();
        String revAuthCode = mRevAuthCode.getText().toString();
        String tracenum = mTracenum.getText().toString();
        String batchNum = mBatchNum.getText().toString();

        if (TextUtils.isEmpty(transDatetime) || TextUtils.isEmpty(revAuthCode) || TextUtils.isEmpty(tracenum) || TextUtils.isEmpty(batchNum) || TextUtils.isEmpty(amout)) {
            Toast.makeText(this, "金额/授权号/交易时间/凭证号/批次号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, CommonCardHandlerActivity.class);
        intent.putExtra(CommonCardHandlerActivity.MENOY_AMOUT, amout);
        intent.putExtra(CommonCardHandlerActivity.TRANS_TIME, transDatetime);
        intent.putExtra(CommonCardHandlerActivity.REVAUTH_CODE, revAuthCode);
        intent.putExtra(CommonCardHandlerActivity.TRACE_NUM, tracenum);
        intent.putExtra(CommonCardHandlerActivity.BATCH_NUM, batchNum);
        intent.putExtra(CommonCardHandlerActivity.EXCHANGE_TYPE, "revokePreAuth");
        startActivity(intent);
    }

    /**
     * @author:lixia 刷卡退货
     */
    private void returnConsume() {
        String amout = mMoneyText.getText().toString();
        String referenceNumber = mReferenceNumber.getText().toString();
        String transDatetime = mTransDatetime.getText().toString();
        if (TextUtils.isEmpty(referenceNumber) || TextUtils.isEmpty(amout) || TextUtils.isEmpty(transDatetime)) {
            Toast.makeText(this, "金额/参考号/交易时间不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, CommonCardHandlerActivity.class);
        intent.putExtra(CommonCardHandlerActivity.MENOY_AMOUT, amout);
        intent.putExtra(CommonCardHandlerActivity.REFE_NUMBER, referenceNumber);
        intent.putExtra(CommonCardHandlerActivity.TRANS_TIME, transDatetime);
        intent.putExtra(CommonCardHandlerActivity.EXCHANGE_TYPE, "returnConsume");
        startActivity(intent);
    }


    /**
     * @author:lixia 刷卡撤销
     */
    private void revokeConsume() {
        String amout = mMoneyText.getText().toString();
        String referenceNumber = mReferenceNumber.getText().toString();
        String revAuthCode = mRevAuthCode.getText().toString();
        String tracenum = mTracenum.getText().toString();
        String batchNum = mBatchNum.getText().toString();
        if (TextUtils.isEmpty(referenceNumber) || TextUtils.isEmpty(revAuthCode) || TextUtils.isEmpty(tracenum) || TextUtils.isEmpty(batchNum)) {
            Toast.makeText(this, "金额/授权号/参考号/原交易凭证号/批次号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CommonCardHandlerActivity.class);
        intent.putExtra(CommonCardHandlerActivity.MENOY_AMOUT, amout);
        intent.putExtra(CommonCardHandlerActivity.REFE_NUMBER, referenceNumber);
        intent.putExtra(CommonCardHandlerActivity.REVAUTH_CODE, revAuthCode);
        intent.putExtra(CommonCardHandlerActivity.TRACE_NUM, tracenum);
        intent.putExtra(CommonCardHandlerActivity.BATCH_NUM, batchNum);
        intent.putExtra(CommonCardHandlerActivity.EXCHANGE_TYPE, "revokeConsume");
        startActivity(intent);

    }

    /**
     * @author:lixia 扫码撤销  （不需要跳转到刷卡界面）
     */
    private void revokeQr() {
        String referenceNumber = mReferenceNumber.getText().toString();
        String amout = mMoneyText.getText().toString();
        String tracenum = mTracenum.getText().toString();
        String batchNum = mBatchNum.getText().toString();
        if (TextUtils.isEmpty(referenceNumber) || TextUtils.isEmpty(amout) || TextUtils.isEmpty(tracenum) || TextUtils.isEmpty(batchNum)) {
            Toast.makeText(this, "交易金额/参考号/原交易凭证号/批次号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        CILRequest request = new CILRequest();
        request.setReferenceNumber(referenceNumber);
        request.setAmount(amout);
        request.setBatchNum(batchNum);
        request.setTraceNum(tracenum);
        CILSDK.revokeConsumeQr(request, new Callback<CILResponse>() {

            @Override
            public void onResult(CILResponse cilResponse) {
                Utils.startResultActivity(MenuActivity.this, cilResponse.getTrans());
            }

            @Override
            public void onError(Parcelable parcelable, Exception e) {
                Utils.startResultActivity(MenuActivity.this, Utils.assembleTransWithRequest(parcelable));
            }
        });

    }


    /**
     * 扫码退货
     */
    private void returnQr() {
        String referenceNumber = mReferenceNumber.getText().toString();
        String transDatetime = mTransDatetime.getText().toString();
        String amout = mMoneyText.getText().toString();
        if (TextUtils.isEmpty(referenceNumber) || TextUtils.isEmpty(transDatetime) || TextUtils.isEmpty(amout)) {
            Toast.makeText(this, "参考号/交易时间/退款金额不能为空或0", Toast.LENGTH_SHORT).show();
            return;
        }
        CILRequest request = new CILRequest();
        request.setAmount(amout);
        request.setReferenceNumber(referenceNumber);
        request.setTransDatetime(transDatetime);
        CILSDK.returnConsumeQr(request, new Callback<CILResponse>() {

            @Override
            public void onResult(CILResponse cilResponse) {
                Utils.startResultActivity(MenuActivity.this, cilResponse.getTrans());
            }

            @Override
            public void onError(Parcelable parcelable, Exception e) {
                Utils.startResultActivity(MenuActivity.this, Utils.assembleTransWithRequest(parcelable));
            }
        });
    }

    /**
     * 银行卡交易
     *
     * @param exchangeType
     */
    private void startDeal(String exchangeType) {
        String amout = mMoneyText.getText().toString();
        if (TextUtils.isEmpty(amout) || Double.parseDouble(amout) <= 0) {
            Toast.makeText(this, "金额不能为空或小于等于0", Toast.LENGTH_SHORT).show();
            return;
        }
        //activity跳转
        Intent intent = new Intent(this, CommonCardHandlerActivity.class);
        intent.putExtra(CommonCardHandlerActivity.MENOY_AMOUT, amout);
        intent.putExtra(CommonCardHandlerActivity.EXCHANGE_TYPE, exchangeType);
        startActivity(intent);

    }

    /**
     * 扫码交易
     */
    private void startCapture() {
        String amout = mMoneyText.getText().toString();
        if (TextUtils.isEmpty(amout) || Double.parseDouble(amout) <= 0) {
            Toast.makeText(this, "金额不能为空或小于等于0", Toast.LENGTH_SHORT).show();
            return;
        }
        //activity跳转
        Intent intent = new Intent(this, CaptureActivity.class);
        intent.putExtra(CommonCardHandlerActivity.MENOY_AMOUT, Double.parseDouble(amout));
        startActivity(intent);

    }


}
