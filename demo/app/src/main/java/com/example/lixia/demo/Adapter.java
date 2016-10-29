package com.example.lixia.demo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cardinfolink.pos.sdk.model.Trans;

import java.util.List;

/**
 * Created by wanny-n1 on 2016/10/28.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<Trans> mTransList;

    public Adapter(List<Trans> transList) {
        this.mTransList = transList;
    }

    @Override
    public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new Adapter.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
        holder.init(position);
    }

    @Override
    public int getItemCount() {
        return mTransList != null ? mTransList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mContentTv;

        public ViewHolder(View itemView) {
            super(itemView);
            mContentTv = (TextView) itemView.findViewById(R.id.bill_content);
        }

        public void init(int position) {
            Trans trans = mTransList.get(position);
            mContentTv.setText(trans.toString());
        }

    }
}
