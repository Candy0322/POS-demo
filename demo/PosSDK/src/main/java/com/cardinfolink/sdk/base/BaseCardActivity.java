package com.cardinfolink.sdk.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cardinfolink.pos.CardType;
import com.cardinfolink.pos.IPosCardEventHandler;
import com.cardinfolink.pos.IPosTransferListener;
import com.cardinfolink.pos.bean.CardInfo;
import com.cardinfolink.pos.newland.N900CardEventHandler;
import com.cardinfolink.pos.sdk.CILSDK;
import com.cardinfolink.pos.sdk.util.DBQueryUtil;
import com.cardinfolink.pos.util.POSConfig;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.cardreader.OpenCardReaderEvent;


/**
 * Created by jie on 16/9/26.
 * Newland 密码键盘
 */

public abstract class BaseCardActivity extends AppCompatActivity {
    private static final String TAG = BaseCardActivity.class.getSimpleName();
    private byte[] pin;

    private Handler uiHandler;

    IPosCardEventHandler<OpenCardReaderEvent> eventHandler;

    IPosTransferListener iPosTransferListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initCardEvent();
    }

    /**
     * 必须传入金额
     *
     * @return
     */
    public abstract String getAmount();


    /**
     * 读卡的结果
     *
     * @param isSuccess 是否成功
     * @param cardType  卡片种类
     * @param cardInfo  读取卡片信息
     */
    public abstract void cardReaderHandler(boolean isSuccess, @CardType.Type int cardType, CardInfo cardInfo);


    /**
     * 显示读卡时的缓冲页面
     */
    public abstract void waitLoadingShow();

    /**
     * 取消读卡时的缓冲页面
     */
    public abstract void waitLoadingDismiss();


    /**
     * 读卡失败
     */
    public abstract void cardHandlerError(Exception e);

    private void initData() {
        uiHandler = new Handler(getMainLooper());
        eventHandler = new N900CardEventHandler(CILSDK.pos.getN900Connection(), getApplicationContext());
        iPosTransferListener = new IPosTransferListener() {
            @Override
            public void onEventStart() {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        waitLoadingShow();
                    }
                });
            }

            @Override
            public void sendPinRequest(String accNo) {
                Intent intent = new Intent(getApplicationContext(), PinInputActivity.class);
                intent.putExtra(PinInputActivity.PIN_KEY_INDEX, POSConfig.PIN_KEY_INDEX);
                intent.putExtra(PinInputActivity.EXTRA_ACCT, accNo);
                intent.putExtra(PinInputActivity.EXTRA_DISPLAY_INFO, DBQueryUtil.queryByCardNum(BaseCardActivity.this, accNo));
                intent.putExtra(PinInputActivity.EXTRA_DISPLAY_AMOUNT, getAmount());
                startActivityForResult(intent, POSConfig.REQUEST_ICCARD_PIN);
            }

            @Override
            public void onCardEventFinished(final boolean isSuccess, final CardInfo cardInfo) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        waitLoadingDismiss();
                        switch (eventHandler.getCardType()) {
                            case CardType.MSC_CARD:
                                cardReaderHandler(isSuccess, CardType.MSC_CARD, cardInfo);
                                break;
                            case CardType.IC_CARD:
                                cardReaderHandler(isSuccess, CardType.IC_CARD, cardInfo);
                                break;
                            case CardType.NFC_CARD:
                                cardReaderHandler(isSuccess, CardType.NFC_CARD, cardInfo);
                                break;
                            default:
                                break;
                        }
                    }
                });

            }

            @Override
            public void onError(final Exception e) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        waitLoadingDismiss();
                        cardHandlerError(e);
                    }
                });
            }
        };
    }

    /**
     * openCardReader
     */
    protected void initCardEvent() {
        try {
            CILSDK.pos.getN900CardReader().openCardReader(new DeviceEventListener<OpenCardReaderEvent>() {
                @Override
                public void onEvent(OpenCardReaderEvent openCardReaderEvent, final Handler handler) {
                    eventHandler.handleCardReaderEvent(getAmount(), openCardReaderEvent, iPosTransferListener);
                }

                @Override
                public Handler getUIHandler() {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            CILSDK.pos.getN900CardReader().cancelCardReader();
            uiHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "关闭刷卡模块失败");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Message msg = Message.obtain();
            switch (requestCode) {
                case POSConfig.REQUEST_SWIPE_PIN:
                    pin = data.getByteArrayExtra(PinInputActivity.RESULT_PIN);
                    msg.what = POSConfig.PIN_FINISH;
                    msg.obj = pin;
                    break;
                case POSConfig.REQUEST_ICCARD_PIN:
                    pin = data.getByteArrayExtra(PinInputActivity.RESULT_PIN);
                    msg.what = POSConfig.PIN_FINISH;
                    msg.obj = pin;
                    break;
                case POSConfig.REQUEST_NFCCARD_PIN:
                    pin = data.getByteArrayExtra(PinInputActivity.RESULT_PIN);
                    msg.what = POSConfig.PIN_FINISH;
                    msg.obj = pin;
                    break;
                default:
                    break;
            }
            unlock(msg);
        }
        //输入密码失败重启刷卡模块
        if (resultCode == PinInputActivity.RESULT_ERROR || resultCode == PinInputActivity.RESULT_USER_CANCELED) {
            Message msg = new Message();
            msg.what = POSConfig.PIN_CANCEL;
            msg.obj = null;
            unlock(msg);
        }
    }


    private void unlock(Message msg) {
        try {
            eventHandler.unlock(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
