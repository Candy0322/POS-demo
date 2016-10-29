package com.cardinfolink.sdk.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cardinfolink.pos.sdk.CILSDK;
import com.cardinfolink.sdk.R;
import com.cardinfolink.pos.sdk.util.DeviceUtil;
import com.newland.mtype.Device;
import com.newland.mtype.ModuleType;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.pin.AccountInputType;
import com.newland.mtype.module.common.pin.K21Pininput;
import com.newland.mtype.module.common.pin.K21PininutEvent;
import com.newland.mtype.module.common.pin.KeyManageType;
import com.newland.mtype.module.common.pin.KeySoundParams;
import com.newland.mtype.module.common.pin.KeySoundType;
import com.newland.mtype.module.common.pin.KeyboardRandom;
import com.newland.mtype.module.common.pin.PinConfirmType;
import com.newland.mtype.module.common.pin.PinInputEvent;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.util.ISOUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


/**
 * 交易前密码输入页面
 * Ｎ９００随机密码键盘页面
 */
public class PinInputActivity extends Activity {

    private static final String TAG = PinInputActivity.class.getSimpleName();

    private static final int MSG_INPUT = 0x20;
    public static final String PIN_KEY_INDEX = "PIK";
    public static final String EXTRA_ACCT = "ACCT";
    public static final String EXTRA_DISPLAY_INFO = "INFO";
    public static final String EXTRA_DISPLAY_AMOUNT = "AMOUNT";
    public static final String EXTRA_IS_DISPLAY_AMOUNT = "DISPLAY_AMOUNT";
    public static final String RESULT_PIN = "PIN";
    public static final int RESULT_ERROR = 0x800;
    public static final int RESULT_USER_CANCELED = 0x801;
    public static final int RESULT_FAILED = 0x802;

    private String account = "";
    private String info = "";
    private String amount = "";
    private boolean isDisplayAmount = true;
    private int pikIndex;

    private Context mContext;

    private ImageView cancel;
    private TextView tvTimer;
    private TextView tvDisplay, tvAmount;
    private TextView tvPassword;
    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9, tv0;
    private TextView clear, ensure;
    private List<View> componentList = new ArrayList<>();
    private Map<String, List<Integer>> coordinateMap = new TreeMap<>(new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            return lhs.compareTo(rhs);
        }
    });

    private Device device;
    private K21Pininput pinInput;

    private int SECONDS = 30;
    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SECONDS--;
                    tvTimer.setText("" + SECONDS);
                    if (SECONDS <= 5)
                        tvTimer.setTextColor(getResources().getColor(R.color.red));
                    if (SECONDS < 0) {
                        timer.cancel();
                        tvTimer.setVisibility(View.GONE);
                    }
                }
            });
        }
    };

    /**
     * 初始化控件
     */
    private void initView() {
        cancel = (ImageView) findViewById(R.id.cancel);
        tvTimer = (TextView) findViewById(R.id.tv_timer);
        tvDisplay = (TextView) findViewById(R.id.tv_display);
        tvAmount = (TextView) findViewById(R.id.tv_amount);
        tvPassword = (TextView) findViewById(R.id.tv_password);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7 = (TextView) findViewById(R.id.tv7);
        tv8 = (TextView) findViewById(R.id.tv8);
        tv9 = (TextView) findViewById(R.id.tv9);
        tv0 = (TextView) findViewById(R.id.tv0);
        clear = (TextView) findViewById(R.id.clear);
        ensure = (TextView) findViewById(R.id.ensure);

        componentList.add(cancel);
        componentList.add(tv0);
        componentList.add(tv1);
        componentList.add(tv2);
        componentList.add(tv3);
        componentList.add(tv4);
        componentList.add(tv5);
        componentList.add(tv6);
        componentList.add(tv7);
        componentList.add(tv8);
        componentList.add(tv9);
        componentList.add(clear);
        componentList.add(ensure);

        tvTimer.setText("" + SECONDS);
        tvDisplay.setText(info);
        isDisplayAmount = !"0.001".equals(amount);
        if (isDisplayAmount)
            tvAmount.setText("￥ " + amount);
        else {
            info = info.substring(0, info.length() - 4);
            tvDisplay.setText(info);
        }

    }

    private void addListener() {

        for (int i = 0; i < componentList.size(); i++) {
            final View view = componentList.get(i);
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    String tag = (String) view.getTag();
                    List loc = getLocationOnScreen(view);
                    coordinateMap.put(tag, loc);
                    if (coordinateMap.size() == 13) {
                        getRandomKeyBoardNumber();
                        /**
                         * workingKey - 工作密钥
                         * pinManageType - 密钥的管理类型
                         * acctInputType - 关联账号输入方式
                         * acctSymbol - 关联账号标识，若输入方式为AccountInputType.USE_ACCOUNT ，则该参数传入的为主账号明文，若输入方式为AccountInputType.USE_ACCT_HASH,则该参数传入的为主账号hash ，若输入方式为AccountInputType.UNUSE_ACCOUNT,则该参数不传值。
                         * inputMaxLen - 最大允许的密码输入长度，范围[0x00,0x0C].
                         * pinPadding - 加密算法中后补数据填充,字节长度为10。根据ANSI x9.8 format带账号的pinblock运算规则,默认账号最长可能需要补充10个 'F',如:041234FFFFFFFFFF。该域为空,则默认使用10个'0'填充。
                         * isEnterEnabled - 是否启用回车,若不启用,则输入完成后自动返回.
                         * displayContent - 等待输入密码时，设备上显示的内容
                         * timeout - 超时时间
                         * timeunit - 超时时间单位
                         * inputListener - 密码输入事件监听器
                         */
                        //TODO : 填入正确的参数

                        pinInput.startStandardPinInput(null, new WorkingKey(pikIndex), KeyManageType.MKSK,
                                AccountInputType.USE_ACCOUNT, account, 6, null,
                                new byte[]{'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F'},
                                PinConfirmType.ENABLE_ENTER_COMMANG, SECONDS, TimeUnit.SECONDS,
                                new KeySoundParams(KeySoundType.NumKeySound.TURNON_KEYSOUND, KeySoundType.StarKeySound.TURNON_KEYSOUND,
                                        KeySoundType.PoundKeySound.TURNON_KEYSOUND, KeySoundType.CancelKeySound.TURNON_KEYSOUND,
                                        KeySoundType.BackspaceKeySound.TURNON_KEYSOUND, KeySoundType.EnterKeySound.TURNON_KEYSOUND),
                                null, pinInputListener);
                    }
                }
            });
        }

    }

    private List getLocationOnScreen(View view) {
        List<Integer> list = new ArrayList<>();
        int[] first = new int[2];
        view.getLocationOnScreen(first);

        int[] second = new int[2];
        second[0] = first[0] + view.getWidth();
        second[1] = first[1] + view.getHeight();
        list.add(DeviceUtil.compare240(mContext, first[0]));
        list.add(DeviceUtil.compare240(mContext, first[1]));
        list.add(DeviceUtil.compare240(mContext, second[0]));
        list.add(DeviceUtil.compare240(mContext, second[1]));
        return list;
    }

    private void getRandomKeyBoardNumber() {

        //坐标基点
        int[] coordinateInt = new int[52];
        for (int i = 0, j = 0; i < coordinateMap.size(); i++, j += 4) {
            List loc = coordinateMap.get(String.valueOf(i));
            coordinateInt[j] = (int) loc.get(0);
            coordinateInt[j + 1] = (int) loc.get(1);
            coordinateInt[j + 2] = (int) loc.get(2);
            coordinateInt[j + 3] = (int) loc.get(3);
        }

        // 初始坐标集合
        byte[] initCoordinate = new byte[coordinateInt.length * 2];
        for (int i = 0, j = 0; i < coordinateInt.length; i++, j++) {
            initCoordinate[j] = (byte) ((coordinateInt[i] >> 8) & 0xff);
            j++;
            initCoordinate[j] = (byte) (coordinateInt[i] & 0xff);
        }
        Log.i(TAG, "初始坐标:" + ISOUtils.hexString(initCoordinate));

        //获取随机键盘键值
        device = CILSDK.pos.getN900Device();
        pinInput = (K21Pininput) device.getStandardModule(ModuleType.COMMON_PININPUT);
        if (pinInput == null)
            return;
        byte[] keySeq = new byte[]{(byte) 0x1B, (byte) 0x7E, (byte) 0x7E, (byte) 0x7E, (byte) 0x7E, (byte) 0x7E, (byte) 0x7E, (byte) 0x7E, (byte) 0x7E, (byte) 0x7E, (byte) 0x0A, (byte) 0x7E, (byte) 0x0D};
        byte[] randomCoordinate = pinInput.loadRandomKeyboard(new KeyboardRandom(initCoordinate, keySeq));
        StringBuffer sb = new StringBuffer();
        byte[] numserial = new byte[10];
        int d = 0;
        for (int i = 0; i < randomCoordinate.length; i++) {
            if (i == 0 || i == 10 || i == 12 || i == 13 || i == 14)
                continue;
            numserial[d] = (byte) (randomCoordinate[i] & 0x0f);
            sb.append(numserial[d]);
            d++;
        }
        Log.i(TAG, "随机数字：" + sb.toString());

        //按键赋值
        TextView[] buttons = new TextView[]{tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9, tv0};
        for (int i = 0; i < buttons.length; i++) {
            char number = sb.charAt(i);
            buttons[i].setText(number + "");
        }

    }

    private StringBuffer buffer;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INPUT:
                    int len = (Integer) msg.obj;
                    buffer = new StringBuffer();
                    for (int i = 0; i < len; i++) {
                        buffer.append("\u0020\u0020*\u0020\u0020");
                    }
                    tvPassword.setText(buffer.toString());
                    break;
                default:
                    break;
            }
        }
    };

    private int inputLen = 0;
    private DeviceEventListener<K21PininutEvent> pinInputListener = new DeviceEventListener<K21PininutEvent>() {

        @Override
        public void onEvent(K21PininutEvent event, Handler handler) {

            if (event.isProcessing()) {//正在输入
                PinInputEvent.NotifyStep notifyStep = event.getNotifyStep();
                if (notifyStep == PinInputEvent.NotifyStep.ENTER) {
                    Log.i(TAG, "按了数字键");
                    inputLen = inputLen + 1;
                } else if (notifyStep == PinInputEvent.NotifyStep.BACKSPACE) {
                    inputLen = (inputLen <= 0 ? 0 : inputLen - 1);
                    Log.i(TAG, "按了退格键");
                }
                Message msg = mHandler.obtainMessage(MSG_INPUT);
                msg.obj = inputLen;
                msg.sendToTarget();

            } else if (event.isUserCanceled()) {//取消
                Log.i(TAG, "按了取消键");
                setResult(RESULT_USER_CANCELED);
                finish();

            } else if (event.isSuccess()) {//确定
                //TODO 未有密码验证
                Log.i(TAG, "输入成功：" + ISOUtils.hexString(event.getEncrypPin()));
                Intent data = new Intent();
                if (event.getInputLen() != 0) {
                    byte[] pin = event.getEncrypPin();
                    data.putExtra(RESULT_PIN, pin);
                } else {
                    byte[] pin = new byte[]{};
                    data.putExtra(RESULT_PIN, pin);
                }
                setResult(RESULT_OK, data);
                finish();

            } else {
                Log.i(TAG, "密码输入异常", event.getException());
                setResult(RESULT_ERROR);
                finish();
            }
        }

        @Override
        public Handler getUIHandler() {
            return null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_pininput);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        account = getIntent().getStringExtra(EXTRA_ACCT);
        info = getIntent().getStringExtra(EXTRA_DISPLAY_INFO);
        amount = getIntent().getStringExtra(EXTRA_DISPLAY_AMOUNT);
        isDisplayAmount = getIntent().getBooleanExtra(EXTRA_IS_DISPLAY_AMOUNT, true);
        pikIndex = getIntent().getIntExtra(PIN_KEY_INDEX, 10);
        initView();
        addListener();
        timer.schedule(timerTask, 1000, 1000);       // timeTask
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
