package com.example.lixia.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
        Button activeButton = (Button) findViewById(R.id.active);
        Button dlButton = (Button) findViewById(R.id.downloadParams);
        Button downParaProgButton = (Button) findViewById(R.id.downloadParamsWithProgress);
        Button signInButton = (Button) findViewById(R.id.signIn);
        mProgressBar = (ProgressBar) findViewById(R.id.downloadProgressBar);
        Button dealButton = (Button) findViewById(R.id.deal);
        //输入框
        mMerText = (EditText) findViewById(R.id.merCode);
        mTermText = (EditText) findViewById(R.id.termCode);
        //控件监听事件
        activeButton.setOnClickListener(this);
        dlButton.setOnClickListener(this);
        downParaProgButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        dealButton.setOnClickListener(this);
        findViewById(R.id.bills).setOnClickListener(this);
        findViewById(R.id.settle).setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    @author：lixia   the common methond of clicking button
    */
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
     * @author:lixia  结算
     */
    private void settle() {
       int batchNum=CILSDK.getBatchNum();
        String batchNumTemp=String.valueOf(batchNum);
        CILSDK.transSettleAsync(batchNumTemp, new Callback<CILResponse>() {
            @Override
            public void onResult(final CILResponse response) {
                //打印结算单
                Intent intent = new Intent(MainActivity.this, SettleResultActivity.class);
                intent.putExtra(ResultActivity.EXCHANGE_RESULT, response.getData());
                startActivity(intent);
            }

            @Override
            public void onError(Parcelable cilRequest, Exception e) {
                textChange("结算出错");
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
                    //激活成功\
                    textChange("激活成功");
                } else {
                    //激活失败
                    textChange("激活失败");
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
                    textChange("参数下载错误");
                }
            }

            @Override
            public void onError(Parcelable p, Exception e) {
                //下载出错
                textChange("下载出错");
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
                mTextView.setText("秘钥下载出错");
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
                textChange("签到出错");
            }
        });

    }
}
