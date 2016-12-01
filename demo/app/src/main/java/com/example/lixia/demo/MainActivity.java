package com.example.lixia.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cardinfolink.pos.listener.Callback;
import com.cardinfolink.pos.listener.ProgressCallback;
import com.cardinfolink.pos.sdk.CILResponse;
import com.cardinfolink.pos.sdk.CILSDK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTextView;
    private String mMerCode;//100000000000009
    private String mTermCode;//00000038
    private ProgressBar mProgressBar;
    private EditText mMerText;
    private EditText mTermText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        //各控件
        mTextView = (TextView) findViewById(R.id.hello);
        mProgressBar = (ProgressBar) findViewById(R.id.downloadProgressBar);
        //输入框
        mMerText = (EditText) findViewById(R.id.merCode);
        mTermText = (EditText) findViewById(R.id.termCode);
        //控件监听事件
        findViewById(R.id.active).setOnClickListener(this);
        findViewById(R.id.downloadParams).setOnClickListener(this);
        findViewById(R.id.downloadParamsWithProgress).setOnClickListener(this);
        findViewById(R.id.signIn).setOnClickListener(this);
        findViewById(R.id.deal).setOnClickListener(this);
        findViewById(R.id.bills).setOnClickListener(this);
        findViewById(R.id.settle).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.active:
                active();
                break;
            case R.id.downloadParams:
                downloadParams();
                break;
            case R.id.downloadParamsWithProgress:
                downloadParamsWithProgress();
                break;
            case R.id.signIn:
                signIn();
                break;
            case R.id.deal:
                toMenu();
                break;
            case R.id.bills:
                bills();
                break;
            case R.id.settle:
                settle();
                break;

        }
    }

    /**
     * @author:lixia 结算
     */
    private void settle() {
        int batchNum = CILSDK.getBatchNum();
        String batchNumTemp = String.valueOf(batchNum);
        CILSDK.transSettleAsync(batchNumTemp, new Callback<CILResponse>() {
            @Override
            public void onResult(final CILResponse response) {
                if (response.getStatus() == 0) {
                    Intent intent = new Intent(MainActivity.this, SettleResultActivity.class);
                    intent.putExtra(ResultActivity.EXCHANGE_RESULT, response.getData());
                    startActivity(intent);
                } else {
                    textChange(response.getMessage());
                }
            }

            @Override
            public void onError(Parcelable cilRequest, Exception e) {
                textChange(e.getMessage());
                //结算出错
            }
        });
    }

    /**
     * @author :lixia  账单界面
     */
    private void bills() {
        startActivity(new Intent(this, BillActivity.class));
    }

    /*
    *   跳转至菜单
    * */
    private void toMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    /*
    * @author:lixia  the methond that can put the thread on th main thread
    * */
    private void textChange(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(text);
            }
        });
    }

    /**
     * @author :lixia   active the pos merchine
     */
    private void active() {
        mMerCode = mMerText.getText().toString();
        mTermCode = mTermText.getText().toString();

        if (TextUtils.isEmpty(mMerCode) || TextUtils.isEmpty(mTermCode)) {
            Toast.makeText(this, "商户号或终端号为空", Toast.LENGTH_LONG).show();
            return;
        }


        CILSDK.active(mMerCode, mTermCode, new Callback<CILResponse>() {
            @Override
            public void onResult(CILResponse cilResponse) {

                if (cilResponse.getStatus() == 0) {
                    //激活成功
                    textChange("激活成功");
                } else {
                    //激活失败
                    textChange(cilResponse.getMessage());
                }
            }

            @Override
            public void onError(Parcelable p, Exception e) {
                //激活出错
                textChange("激活出错");
            }
        });

    }

    /**
     * @author :lixia   using the method  from CILSDK can download params
     */

    private void downloadParams() {
        mMerCode = mMerText.getText().toString();
        mTermCode = mTermText.getText().toString();
        if (TextUtils.isEmpty(mMerCode) || TextUtils.isEmpty(mTermCode)) {
            mTextView.setText("商户号或终端号错误");
            return;
        }
        CILSDK.downloadParams(mMerCode, mTermCode, new Callback<CILResponse>() {
            @Override
            public void onResult(CILResponse response) {
                if (0 == response.getStatus()) {
                    //参数下载成功,具体返回的参数见 response.Info
                    textChange("参数下载成功");
                } else {
                    //参数下载错误
                    textChange(response.getMessage());
                }
            }

            @Override
            public void onError(Parcelable p, Exception e) {
                //下载出错
                textChange(e.getMessage());
            }
        });

    }


    /**
     * @author ;lixia   download the key
     */
    private void downloadParamsWithProgress() {
        CILSDK.downloadParamsWithProgress(new ProgressCallback<CILResponse>() {
            @Override
            public void onProgressUpdate(int progress) {
                //progress下载密钥的进度
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(progress);
                if (progress >= 100) {
                    mProgressBar.setVisibility(View.GONE);
                    mTextView.setText("秘钥下载完成");
                }
            }

            @Override
            public void onResult(CILResponse response) {

                if (0 == response.getStatus()) {
                    mTextView.setText("秘钥下载成功");
                }
                //密钥下载成功。在这里可以持久化一个标志位
            }

            @Override
            public void onError(Parcelable p, Exception e) {
                //下载密钥出错
                mTextView.setText(e.getMessage());
            }
        });
    }

    /*
    * @author:lixia  terminal sign in
    * */
    private void signIn() {
        CILSDK.signIn(new Callback<CILResponse>() {
            @Override
            public void onResult(CILResponse cilResponse) {
                //签到成功
                textChange("签到成功");

            }

            @Override
            public void onError(Parcelable cilRequest, Exception e) {
                //签到出错
                textChange(e.getMessage());
            }
        });

    }
}
