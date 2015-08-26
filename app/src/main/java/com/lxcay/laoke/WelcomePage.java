package com.lxcay.laoke;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;
import com.lxcay.laoke.utils.Logger;
import com.lxcay.laoke.utils.MobileUtil;
import com.lxcay.laoke.utils.PreferenceUtil;
import com.lxcay.laoke.utils.ProgressDialogUtil;
import com.lxcay.laoke.utils.Utils;

/**
 * Created by lxcay on 2015/7/21.
 */
public class WelcomePage extends Activity {
    private String TAG=WelcomePage.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化api
        GotyeAPI.getInstance().init(this, MyApplication.APPKEY);
        // 判断当前登陆状态
        //-1不在线, 0 退出登录，或者已经退出,1,在线
        int state = GotyeAPI.getInstance().isOnline();
        Logger.d(TAG, "state=" + state);
        GotyeAPI.getInstance().enableLog(true, true, false);//开启日志打印
        // 已经登陆了就直接跳转了
        if (state == GotyeUser.NETSTATE_ONLINE && GotyeAPI.getInstance().getLoginUser() != null&&MyApplication.LOGIN_OUT==0) {
            // 已经登陆或离线可以直接跳到主界面
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            //启动service保存service长期活动
            Intent toService = new Intent(this, GotyeService.class);
            startService(toService);
            finish();
            return;
        }

        //先到本地内存获取手机号码
        String phoneNomber=PreferenceUtil.getLogin(this);
        if(TextUtils.isEmpty(phoneNomber)){
            // 没有登陆需要获取手机号码，用手机号码登录
            phoneNomber = MobileUtil.getNativePhoneNumber(this);
            if(TextUtils.isEmpty(phoneNomber)) {
                Utils.ShowToast(this, "临时账号登录");
                phoneNomber="admin";
            }else{
                //获取手机号码
                phoneNomber = phoneNomber.substring(phoneNomber.length() - 11, phoneNomber.length());
                PreferenceUtil.setLogin(this, phoneNomber);
            }
        }

        Intent login = new Intent(this, GotyeService.class);
        login.setAction(GotyeService.ACTION_LOGIN);
        login.putExtra("name", phoneNomber);
        startService(login);
        ProgressDialogUtil.showProgress(this, "正在登录...");

        // 注意添加LoginListener
        GotyeAPI.getInstance().addListener(mDelegate);
    }

    private GotyeDelegate mDelegate = new GotyeDelegate() {

        public void onLogin(int code, GotyeUser user) {
            ProgressDialogUtil.dismiss();
            // 判断登陆是否成功
            if (code == GotyeStatusCode.CodeOK // 0
                    || code == GotyeStatusCode.CodeReloginOK // 5
                    || code == GotyeStatusCode.CodeOfflineLoginOK) { // 6

                Intent i = new Intent(WelcomePage.this, MainActivity.class);
                startActivity(i);

                if (code == GotyeStatusCode.CodeOfflineLoginOK) {
                    Toast.makeText(WelcomePage.this, "您当前处于离线状态", Toast.LENGTH_SHORT).show();
                } else if (code == GotyeStatusCode.CodeOK) {
                    Toast.makeText(WelcomePage.this, "登录成功", Toast.LENGTH_SHORT).show();
                }
                WelcomePage.this.finish();
            } else {
                // 失败,可根据code定位失败原因
                Toast.makeText(WelcomePage.this, "登录失败 code=" + code, Toast.LENGTH_SHORT).show();
            }
        }

        public void onLogout(int code) {
        }

        public void onReconnecting(int code, GotyeUser currentLoginUser) {
        }
    };


    @Override
    protected void onDestroy() {
        // 移除监听
        GotyeAPI.getInstance().removeListener(mDelegate);
        ProgressDialogUtil.dismiss();
        super.onDestroy();
    }
}
