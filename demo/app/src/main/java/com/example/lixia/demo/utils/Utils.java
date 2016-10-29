package com.example.lixia.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;

import com.cardinfolink.pos.sdk.CILRequest;
import com.cardinfolink.pos.sdk.model.Trans;
import com.example.lixia.demo.ResultActivity;

/**
 * Created by lixia on 2016/10/27.
 */

public class Utils {
    /**
     * 处理异常信息,根据返回值组拼Trans信息
     *
     * @param p
     * @return
     */
    public static Trans assembleTransWithRequest(Parcelable p) {
        Trans trans = null;
        if (p instanceof CILRequest) {
            trans = new Trans();
            CILRequest transV2 = (CILRequest) p;
            trans.setBillingCurr(transV2.getBillingCurr());
            trans.setTransCurr(transV2.getTransCurr());
            trans.setBatchNum(transV2.getBatchNum());
            trans.setBillingAmt(transV2.getBillingAmt());
//            trans.setCardBrand(transV2.getCardBrand());
            trans.setCardNo(transV2.getCardNum());
//            trans.setInsCode(transV2.getInsCode());
            trans.setMerCode(transV2.getMerCode());
//            trans.setProcessFlag(transV2.getProcessFlag());
            trans.setRefNum(transV2.getReferenceNumber());
            trans.setRespCode("98");
            trans.setRevAuthCode(transV2.getRevAuthCode());
//            trans.setRevFlag(transV2.getRevFlag());
//            trans.setRevInsCode(transV2.getRevInsCode());
//            trans.setRevOrderNum(transV2.getRevOrderNum());
            trans.setTermCode(transV2.getTermCode());
//            trans.setTransCode(transV2.getTransCode());
            trans.setTraceNum(transV2.getTraceNum());
            trans.setTransAmt(transV2.getAmount());
            trans.setTransDate(transV2.getTransDatetime());
            trans.setTransDatetime(transV2.getTransDatetime());
//            trans.setTransRate(transV2.getTransRate());
        }
        return trans;
    }

    public static void startResultActivity(Context context, Trans trans) {
        if (context != null) {
            Intent intent = new Intent(context, ResultActivity.class);
            intent.putExtra(ResultActivity.EXCHANGE_RESULT, trans);
            context.startActivity(intent);
        }

    }


}
