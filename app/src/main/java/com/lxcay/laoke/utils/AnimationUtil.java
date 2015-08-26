package com.lxcay.laoke.utils;


import android.app.Activity;

import com.lxcay.laoke.R;

public class AnimationUtil {

    /**
     * activity之间切换动画
     *
     * @param activity
     * @param type  0 进入  1 出去
     */
    public static void ActivityAnimation(Activity activity, int type) {
        switch (type) {
            case 0:
                //左右推出效果
                activity.overridePendingTransition(R.animator.in_from_right, R.animator.out_to_left);
                break;
            case 1:
                activity.overridePendingTransition(R.animator.in_from_left, R.animator.out_to_right);
                break;
            default:
                break;
        }
    }
}
