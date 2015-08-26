package com.lxcay.laoke.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lxcay.laoke.utils.Utils;

import java.util.Map;

/**
 * Created by lxcay on 2015/8/19.
 * 每个界面显示28个表情
 */
public class GridAdapter extends BaseAdapter {
    private int pagerCount;
    private Context context;
    private Map<String,Integer>  biaoq;

    public GridAdapter(int pagerCount, Context context, Map<String,Integer> biaoq) {
        this.pagerCount = pagerCount;
        this.context=context;
        this.biaoq=biaoq;
    }

    @Override
    public int getCount() {
        return 28;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView ball = new ImageView(context);
        ball.setLayoutParams(new ViewGroup.LayoutParams(Utils.dp2px(context, 22), Utils.dp2px(context, 22)));
        int size = biaoq.size();

        int count = 28 * pagerCount + position;
        if(count<size){
            ball.setBackgroundResource((Integer)biaoq.values().toArray()[count]);
        }
        return ball;
    }
}