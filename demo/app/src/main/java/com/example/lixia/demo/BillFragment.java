package com.example.lixia.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cardinfolink.pos.listener.Callback;
import com.cardinfolink.pos.sdk.CILResponse;
import com.cardinfolink.pos.sdk.CILSDK;
import com.cardinfolink.pos.sdk.model.Trans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BillFragment extends Fragment {
    private final static String TXN_TYPE = "txn_type";
    private final static int SIZE = 20;
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private List<Trans> mTransList = new ArrayList<>();

    public static BillFragment newInstance(int txnType) {
        BillFragment billFragment = new BillFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TXN_TYPE, txnType);
        billFragment.setArguments(bundle);
        return billFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bill, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.bill_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new Adapter(mTransList);
        mRecyclerView.setAdapter(mAdapter);

        if (getArguments() == null) {
            return;
        }

        CILSDK.getBillsAsync(0, SIZE, getArguments().getInt(TXN_TYPE), new Callback<CILResponse>() {
            @Override
            public void onResult(final CILResponse response) {
                if (null != response && 0 == response.getStatus()
                        && response.getTxn() != null) {


                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            //账单获取成功
                            mTransList.clear();
                            mTransList.addAll(Arrays.asList(response.getTxn()));
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onError(Parcelable p, Exception ex) {
                showToast("获取账单失败");
            }
        });
    }


    private void showToast(final String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();

            }
        });
    }
}
