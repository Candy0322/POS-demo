package com.example.lixia.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.cardinfolink.pos.CardType;
import com.cardinfolink.pos.bean.CardInfo;
import com.cardinfolink.pos.listener.Callback;
import com.cardinfolink.pos.sdk.CILRequest;
import com.cardinfolink.pos.sdk.CILResponse;
import com.cardinfolink.pos.sdk.CILSDK;
import com.cardinfolink.sdk.base.BaseCardActivity;
import com.example.lixia.demo.utils.Utils;


public class CommonCardHandlerActivity extends BaseCardActivity {

    public final static String MENOY_AMOUT = "money_amout";
    public final static String EXCHANGE_TYPE = "exchange_type";
    public final static String REFE_NUMBER = "refe_number";
    public final static String TRANS_TIME = "trans_time";
    public final static String REVAUTH_CODE = "revauth_code";
    public final static String TRACE_NUM = "trace_num";
    public final static String BATCH_NUM = "batch_num";


    private String mAmout;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_card_handler);
        mAmout = getIntent().getStringExtra(MENOY_AMOUT);
        TextView moneyView = (TextView) findViewById(R.id.takeMoney);
        moneyView.setText(mAmout);
    }

    @Override
    public String getAmount() {
        return mAmout;
    }

    //刷卡会调用这个方法
    @Override
    public void cardReaderHandler(boolean isSuccess, @CardType.Type int cardType, CardInfo cardInfo) {
        if (!isSuccess || cardInfo == null) {
            Toast.makeText(getApplicationContext(), "读卡失败", Toast.LENGTH_SHORT).show();
            return;
        }
        CILRequest request = new CILRequest();
        request.setAmount(getAmount());
        request.setCardNumber(cardInfo.getCardNumber());
        request.setCardExpirationDate(cardInfo.getCardExpirationDate());
        request.setPinEmv(cardInfo.getPinBins());
        request.setCardSequenceNumber(cardInfo.getSequenceSerialNum());
        request.setField55(cardInfo.getField55());
        request.setSecondTrack(cardInfo.getTrack2());

        String exchangeType = getIntent().getStringExtra(EXCHANGE_TYPE);
        if (request == null || TextUtils.isEmpty(exchangeType)) {
            return;
        }

        switch (exchangeType) {
            case "consume":
                consume(request, cardType);
                break;
            case "checkBalance":
                checkBalance(request, cardType);
                break;
            case "preAuth":
                preAuth(request, cardType);
                break;
            case "revokePreAuth":
                revokePreAuth(request, cardType);
                break;
            case "revokeConsume":
                revokeConsume(request, cardType);
                break;
            case "returnConsume":
                returnConsume(request, cardType);
                break;
            case "preAuthComplete":
                preAuthComplete(request, cardType);
                break;
            case "revokePreAuthComplete":
                revokePreAuthComplete(request, cardType);
                break;
        }


    }

    /**
     * 预授权完成撤销
     *
     * @param request
     * @param cardType
     */
    private void revokePreAuthComplete(CILRequest request, int cardType) {
        String amout = getIntent().getStringExtra(MENOY_AMOUT);
        String revAuthCode = getIntent().getStringExtra(REVAUTH_CODE);
        String transDatetime = getIntent().getStringExtra(TRANS_TIME);
        String tracenum = getIntent().getStringExtra(TRACE_NUM);
        String batchNum = getIntent().getStringExtra(BATCH_NUM);
        String referenceNumber = getIntent().getStringExtra(REFE_NUMBER);

        request.setAmount(amout);
        request.setRevAuthCode(revAuthCode);
        request.setTraceNum(tracenum);
        request.setBatchNum(batchNum);
        request.setTransDatetime(transDatetime);
        request.setReferenceNumber(referenceNumber);
        CILSDK.revokePreAuthComplete(request, cardType, new Callback<CILResponse>() {

            @Override
            public void onResult(CILResponse cilResponse) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, cilResponse.getTrans());
                finish();
            }

            @Override
            public void onError(Parcelable parcelable, Exception e) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, Utils.assembleTransWithRequest(parcelable));
                finish();
            }
        });
    }

    /**
     * 预授权完成
     *
     * @param request
     * @param cardType
     */
    private void preAuthComplete(CILRequest request, int cardType) {
        String amout = getIntent().getStringExtra(MENOY_AMOUT);
        String revAuthCode = getIntent().getStringExtra(REVAUTH_CODE);
        String transDatetime = getIntent().getStringExtra(TRANS_TIME);

        request.setAmount(amout);
        request.setRevAuthCode(revAuthCode);
        request.setTransDatetime(transDatetime);
        CILSDK.preAuthComplete(request, cardType, new Callback<CILResponse>() {

            @Override
            public void onResult(CILResponse cilResponse) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, cilResponse.getTrans());
                finish();
            }

            @Override
            public void onError(Parcelable parcelable, Exception e) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, Utils.assembleTransWithRequest(parcelable));
                finish();
            }
        });

    }

    /**
     * 预授权撤销
     *
     * @param request
     * @param cardType
     */
    private void revokePreAuth(CILRequest request, int cardType) {
        String amout = getIntent().getStringExtra(MENOY_AMOUT);
        String revAuthCode = getIntent().getStringExtra(REVAUTH_CODE);
        String transDatetime = getIntent().getStringExtra(TRANS_TIME);
        String tracenum = getIntent().getStringExtra(TRACE_NUM);
        String batchNum = getIntent().getStringExtra(BATCH_NUM);

        request.setAmount(amout);
        request.setRevAuthCode(revAuthCode);
        request.setTraceNum(tracenum);
        request.setBatchNum(batchNum);
        request.setTransDatetime(transDatetime);

        CILSDK.revokePreAuth(request, cardType, new Callback<CILResponse>() {

            @Override
            public void onResult(CILResponse cilResponse) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, cilResponse.getTrans());
                finish();
            }

            @Override
            public void onError(Parcelable parcelable, Exception e) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, Utils.assembleTransWithRequest(parcelable));
                finish();
            }
        });
    }

    /**
     * 刷卡退货
     *
     * @param request
     * @param cardType
     */
    private void returnConsume(CILRequest request, int cardType) {
        String amout = getIntent().getStringExtra(MENOY_AMOUT);
        String refNumber = getIntent().getStringExtra(REFE_NUMBER);
        String transDatetime = getIntent().getStringExtra(TRANS_TIME);

        request.setAmount(amout);
        request.setReferenceNumber(refNumber);
        request.setTransDatetime(transDatetime);
        CILSDK.returnConsume(request, cardType, new Callback<CILResponse>() {

            @Override
            public void onResult(CILResponse cilResponse) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, cilResponse.getTrans());
                finish();
            }

            @Override
            public void onError(Parcelable parcelable, Exception e) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, Utils.assembleTransWithRequest(parcelable));
                finish();
            }
        });

    }

    /**
     * 刷卡撤销
     *
     * @param request
     * @param cardType
     */
    private void revokeConsume(CILRequest request, int cardType) {
        String amout = getIntent().getStringExtra(MENOY_AMOUT);
        String refNumber = getIntent().getStringExtra(REFE_NUMBER);
        String revAuthCode = getIntent().getStringExtra(REVAUTH_CODE);
        String tracenum = getIntent().getStringExtra(TRACE_NUM);
        String batchNum = getIntent().getStringExtra(BATCH_NUM);

        request.setAmount(amout);
        request.setReferenceNumber(refNumber);
        request.setRevAuthCode(revAuthCode);
        request.setBatchNum(batchNum);
        request.setTraceNum(tracenum);

        CILSDK.revokeConsume(request, cardType, new Callback<CILResponse>() {

            @Override
            public void onResult(CILResponse cilResponse) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, cilResponse.getTrans());
                finish();
            }

            @Override
            public void onError(Parcelable parcelable, Exception e) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, Utils.assembleTransWithRequest(parcelable));
                finish();
            }
        });
    }

    /***
     * 银行卡消费
     *
     * @param request
     * @param cardType
     */
    private void consume(CILRequest request, @CardType.Type int cardType) {
        CILSDK.consume(request, cardType, new Callback<CILResponse>() {
            @Override
            public void onResult(CILResponse cilResponse) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, cilResponse.getTrans());
                finish();
            }

            @Override
            public void onError(Parcelable parcelable, Exception e) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, Utils.assembleTransWithRequest(parcelable));
                finish();
            }
        });

    }

    /**
     * 余额查询
     *
     * @param request
     * @param cardType
     */
    private void checkBalance(CILRequest request, @CardType.Type int cardType) {
        CILSDK.checkBalance(request, cardType, new Callback<CILResponse>() {
            @Override
            public void onResult(CILResponse cilResponse) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, cilResponse.getTrans());
                finish();
            }

            @Override
            public void onError(Parcelable parcelable, Exception e) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, Utils.assembleTransWithRequest(parcelable));
                finish();
            }
        });
    }

    /**
     * 预授权
     *
     * @param request
     * @param cardType
     */
    private void preAuth(CILRequest request, @CardType.Type int cardType) {
        CILSDK.preAuth(request, cardType, new Callback<CILResponse>() {
            @Override
            public void onResult(CILResponse cilResponse) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, cilResponse.getTrans());
                finish();
            }

            @Override
            public void onError(Parcelable parcelable, Exception e) {
                Utils.startResultActivity(CommonCardHandlerActivity.this, Utils.assembleTransWithRequest(parcelable));
                finish();
            }
        });
    }

    @Override
    public void waitLoadingShow() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        if (mProgressDialog != null && !mProgressDialog.isShowing()
                && !isFinishing()) {
            mProgressDialog.show();
        }

    }

    @Override
    public void waitLoadingDismiss() {
        if (mProgressDialog != null && mProgressDialog.isShowing()
                && !isFinishing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 刷卡失败
     *
     * @param e
     */
    @Override
    public void cardHandlerError(Exception e) {
        Toast.makeText(getApplicationContext(), e.getMessage() == null ? "" : e.getMessage(), Toast.LENGTH_SHORT).show();
        //TODO 刷卡失败时,是否自动重启
        initCardEvent();
    }

}
