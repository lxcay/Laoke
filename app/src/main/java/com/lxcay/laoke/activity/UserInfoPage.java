package com.lxcay.laoke.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeMedia;
import com.gotye.api.GotyeRoom;
import com.gotye.api.GotyeUser;
import com.lxcay.laoke.MainActivity;
import com.lxcay.laoke.R;
import com.lxcay.laoke.utils.AnimationUtil;
import com.lxcay.laoke.utils.BitmapUtil;
import com.lxcay.laoke.utils.ImageCache;
import com.lxcay.laoke.utils.ProgressDialogUtil;
import com.lxcay.laoke.utils.Utils;

public class UserInfoPage extends Activity implements OnClickListener {
    private GotyeUser user;
    private ImageView userIconView;
    private int from;
    private GotyeRoom room;
    public GotyeAPI api = GotyeAPI.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_user_info);
        user = (GotyeUser) getIntent().getSerializableExtra("user");
        api.addListener(mdelegate);
        from = getIntent().getIntExtra("from", -1);
        room = (GotyeRoom) getIntent().getSerializableExtra("room");
        GotyeUser tempUser = api.getUserDetail(user, true);
        if (tempUser != null) {
            user = tempUser;
        }
        initView();
        setValue();
    }

    private void initView() {
        findViewById(R.id.back).setOnClickListener(this);
        if (user.getName().equals(api.getLoginUser().getName())) {
            findViewById(R.id.add_friend).setEnabled(false);
            findViewById(R.id.del_frieng).setEnabled(false);
        } else {
            if (user.isFriend()) {
                findViewById(R.id.add_friend).setEnabled(false);
                findViewById(R.id.del_frieng).setEnabled(true);
                findViewById(R.id.del_frieng).setOnClickListener(this);
            } else {
                findViewById(R.id.add_friend).setEnabled(true);
                findViewById(R.id.add_friend).setOnClickListener(this);
                findViewById(R.id.del_frieng).setEnabled(false);
            }
        }
    }

    private void setValue() {
        ((TextView) findViewById(R.id.id)).setText("昵称: " + user.getNickname());
        ((TextView)findViewById(R.id.user_info_qianming)).setText(user.getInfo());
        userIconView = (ImageView) findViewById(R.id.user_icon);
        Bitmap bmp = ImageCache.getInstance().get(user.getName());
        if (bmp != null) {
            userIconView.setImageBitmap(bmp);
        } else {
            if (user.getIcon() != null) {
                Bitmap bm = BitmapUtil.getBitmap(user.getIcon().getPath());
                if (bm != null) {
                    userIconView.setImageBitmap(bm);
                    ImageCache.getInstance().put(user.getName(), bm);
                } else {
                    api.downloadMedia(user.getIcon());
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        api.removeListener(mdelegate);
        ProgressDialogUtil.dismiss();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_friend:
                GotyeUser userLogin = GotyeAPI.getInstance().getLoginUser();
                if (user.getName().equals(userLogin.getName())) {
                    Toast.makeText(this, "不能添加自己", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    ProgressDialogUtil.showProgress(this, "正在加为好友...");
                    api.reqAddFriend(user);
                }
                break;
            case R.id.del_frieng:
                ProgressDialogUtil.showProgress(this, "正在删除好友");
                api.reqRemoveFriend(user);
                api.reqAddBlocked(user);
                api.reqRemoveBlocked(user);
                break;
            case R.id.back:
                finish();
                AnimationUtil.ActivityAnimation(UserInfoPage.this,1);
                break;
            default:
                break;
        }
    }

    private GotyeDelegate mdelegate = new GotyeDelegate() {

        @Override
        public void onGetUserDetail(int code, GotyeUser user) {
            // TODO Auto-generated method stub
            if (code == 0) {
                if (user.getName().equals(UserInfoPage.this.user.getName())) {
                    UserInfoPage.this.user = user;
                    ImageCache.getInstance().removeKey(user.getName());
                    setValue();
                }

            }
        }

        @Override
        public void onAddFriend(int code, GotyeUser user) {

            ProgressDialogUtil.dismiss();
            if (code == 0) {
                UserInfoPage.this.user = user;
                Utils.ShowToast(UserInfoPage.this, "添加好友成功!");
                initView();
            } else {
                Utils.ShowToast(UserInfoPage.this, "添加好友失败!");
            }
        }

        @Override
        public void onAddBlocked(int code, GotyeUser user) {
            if (code == 0) {
                UserInfoPage.this.user = user;
                ProgressDialogUtil.dismiss();
                initView();
            } else {
                ProgressDialogUtil.dismiss();
            }
        }

        @Override
        public void onRemoveFriend(int code, GotyeUser user) {

            if (from == 100) {
                ProgressDialogUtil.dismiss();
                Intent i = new Intent(UserInfoPage.this, RoomInfoPage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("room", room);
                startActivity(i);
                finish();
                Utils.ShowToast(UserInfoPage.this, "成功删除好友：" + user.getName());
            } else if (from == 1) {
                finish();
            } else {
                ProgressDialogUtil.dismiss();
                Intent i = new Intent(UserInfoPage.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("tab", 1);
                startActivity(i);
                Utils.ShowToast(UserInfoPage.this, "成功删除好友：" + user.getName());
            }

        }

        @Override
        public void onRemoveBlocked(int code, GotyeUser user) {
            // TODO Auto-generated method stub
            ProgressDialogUtil.dismiss();
            if (code == 0) {
                UserInfoPage.this.user = user;
                initView();
            }
        }

        @Override
        public void onDownloadMedia(int code, GotyeMedia media) {
            if (!TextUtils.isEmpty(media.getUrl())) {
                if (user.getIcon() != null && media.getUrl().equals(user.getIcon().getUrl())) {
                    Bitmap bm = BitmapUtil.getBitmap(media.getPath());
                    if (bm != null) {
                        userIconView.setImageBitmap(bm);
                        ImageCache.getInstance().put(user.getName(), bm);
                    }

                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        AnimationUtil.ActivityAnimation(UserInfoPage.this, 1);
    }
}
