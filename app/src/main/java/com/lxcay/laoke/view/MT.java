package com.lxcay.laoke.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lxcay.laoke.R;

/**
 * Created by lxcay on 2015/8/7.
 */
public class MT extends Activity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        .cast
//        .var
    }
    class myAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getBaseContext(), R.layout.activity_main, null);
            ViewHolder holder=null;
            if(view==null){
                holder=new ViewHolder(view);
                view.setTag(holder);
            }else{
                holder= (ViewHolder) view.getTag();
            }
            return null;
        }

        public class ViewHolder {
            public final TextView titlebarlayoutback;
            public final TextView titlebarlayouttv;
            public final ImageView titlebarlayoutadd;
            public final RelativeLayout titlebarlayout;
            public final CustomRadioGroup mainfooter;
            public final ViewPager mainbody;
            public final View root;

            public ViewHolder(View root) {
                titlebarlayoutback = (TextView) root.findViewById(R.id.title_bar_layout_back);
                titlebarlayouttv = (TextView) root.findViewById(R.id.title_bar_layout_tv);
                titlebarlayoutadd = (ImageView) root.findViewById(R.id.title_bar_layout_add);
                titlebarlayout = (RelativeLayout) root.findViewById(R.id.title_bar_layout);
                mainfooter = (CustomRadioGroup) root.findViewById(R.id.main_footer);
                mainbody = (ViewPager) root.findViewById(R.id.main_body);
                this.root = root;
            }
        }
    }
}
