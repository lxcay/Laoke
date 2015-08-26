package com.lxcay.laoke.listener;

import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.lxcay.laoke.view.CustomRadioGroup;

public class PageChangeListener implements OnPageChangeListener {
    private CustomRadioGroup mCustomRadioGroup;

    public PageChangeListener(CustomRadioGroup mCustomRadioGroup) {
        this.mCustomRadioGroup = mCustomRadioGroup;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset != 0.0f) {
            int right, left;
            if (position == mCustomRadioGroup.getCheckedIndex()) {
                //方向向右
                left = mCustomRadioGroup.getCheckedIndex();
                right = mCustomRadioGroup.getCheckedIndex() + 1;
            } else {
                //方向向左
                left = mCustomRadioGroup.getCheckedIndex() - 1;
                right = mCustomRadioGroup.getCheckedIndex();

            }
            mCustomRadioGroup.itemChangeChecked(left, right, positionOffset);
        } else {
            mCustomRadioGroup.setCheckedIndex(position);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }
}
