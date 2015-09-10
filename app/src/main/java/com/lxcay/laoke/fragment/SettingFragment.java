package com.lxcay.laoke.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeMedia;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;
import com.lxcay.laoke.MyApplication;
import com.lxcay.laoke.R;
import com.lxcay.laoke.utils.BitmapUtil;
import com.lxcay.laoke.utils.ImageCache;
import com.lxcay.laoke.utils.PreferenceUtil;
import com.lxcay.laoke.utils.URIUtil;
import com.lxcay.laoke.utils.Utils;
import com.lxcay.lock.Lock_ForeverService;

/**
 * Created by lxcay on 2015/7/23.
 */
public class SettingFragment extends Fragment {
    private static final int REQUEST_PIC = 1;
    private static final int RESULT_OK = -1;

    private GotyeUser user;
    private ImageView iconImageView;
    private EditText nickName;
    private EditText qianming;
    private TextView info;
    private GotyeAPI api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.setting_fragment_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        api = GotyeAPI.getInstance();
        api.addListener(mdelegate);
        user = api.getLoginUser();
        api.getUserDetail(user, true);
        initView();
        super.onActivityCreated(savedInstanceState);
    }

    private void initView() {
        iconImageView = (ImageView) getView().findViewById(R.id.icon);
        nickName = (EditText) getView().findViewById(R.id.nick_name);
        qianming = (EditText) getView().findViewById(R.id.setting_fragment_et_info);
        qianming.setText(user.getInfo());
        qianming.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    qianming.setCursorVisible(true);
                } else {
                    // 此处为失去焦点时的处理内容
                }
            }
        });
        info = (TextView) getView().findViewById(R.id.info_name);
        Button btn = (Button) getView().findViewById(R.id.logout_btn);
        btn.setText("退出应用");
        nickName.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        nickName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {

                if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
                    String text = arg0.getText().toString();
                    if (!"".equals(text)) {
                        GotyeUser forModify = new GotyeUser(user.getName());
                        forModify.setNickname(text);
                        //这里是设置个性签名了
                        if (Utils.checkNameChese(qianming.getText().toString())) {
                            forModify.setInfo(qianming.getText().toString());
                        } else {
                            Utils.ShowToast(getActivity(), "error_签名只能是中文哦");
                            return false;
                        }

                        forModify.setGender(user.getGender());
                        String headPath = null;
                        api.reqModifyUserInfo(forModify, headPath);
                        //隐藏光标
                        qianming.setCursorVisible(false);
                    }
                    return true;
                }
                return false;
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                //清楚缓存
//                api.clearCache();
                //推出登陆
                int code = api.logout();
                if (code == GotyeStatusCode.CodeNotLoginYet) {
                    MyApplication.LOGIN_OUT = 1;
                }
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        getView().findViewById(R.id.icon_layout).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        takePic();
                    }
                });

        iconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                takePic();
            }
        });
        CheckBox receiveNewMsg = ((CheckBox) getView().findViewById(R.id.new_msg));
        receiveNewMsg.setChecked(MyApplication.isNewMsgNotify());
        receiveNewMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyApplication.setNewMsgNotify(isChecked, user.getName());
            }
        });
        //设置是否开启省电模式
        CheckBox PowerSaving = ((CheckBox) getView().findViewById(R.id.power_saving));
        PowerSaving.setChecked(PreferenceUtil.getBoolean(getActivity(),PreferenceUtil.KEY.POWERSAVING,false));
        PowerSaving.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceUtil.setBoolean(getActivity(),PreferenceUtil.KEY.POWERSAVING,isChecked);
            }
        });
        //如果勾选了，就可以执行省电模式
        if(PowerSaving.isChecked()){
            Intent intent = new Intent(getActivity(), Lock_ForeverService.class);
            getActivity().startService(intent);
        }else{
            Intent intent = new Intent(getActivity(), Lock_ForeverService.class);
            intent.putExtra("isStop",true);
            getActivity().stopService(intent);
        }

        //是否接收群消息
        MyApplication.setNotReceiveGroupMsg(false, user.getName());

        //敏感词过滤
        SharedPreferences spf = getActivity().getSharedPreferences("fifter_cfg", Context.MODE_PRIVATE);
        spf.edit().putBoolean("fifter", true).commit();
        getView().findViewById(R.id.setting_fragment_view_save).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        String name = nickName.getText().toString().trim();
                        GotyeUser forModify = new GotyeUser(user.getName());
                        forModify.setNickname(name);
                        //个性签名设置
                        if (Utils.checkNameChese(qianming.getText().toString())) {
                            forModify.setInfo(qianming.getText().toString());
                        } else {
                            Utils.ShowToast(getActivity(), "error_签名只能是中文哦");
                            return;
                        }
                        forModify.setGender(user.getGender());
                        api.reqModifyUserInfo(forModify, null);
                        qianming.setCursorVisible(false);
                    }
                });

        setUserInfo(user);
    }

    boolean hasRequest = false;

    private void setUserInfo(GotyeUser user) {
        if (user.getIcon() == null && !hasRequest) {
            hasRequest = true;
            api.getUserDetail(user, true);
        } else {
            Bitmap bm = BitmapUtil.getBitmap(user.getIcon().getPath());
            if (bm != null) {
                iconImageView.setImageBitmap(bm);
                ImageCache.getInstance().put(user.getName(), bm);
            } else {
                api.downloadMedia(user.getIcon());
            }
        }
        nickName.setText(user.getNickname());
        info.setText(user.getName());
    }

    private void takePic() {
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//		intent.setType("image/*");
//		getActivity().startActivityForResult(intent, REQUEST_PIC);
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/jpeg");
        startActivityForResult(intent, REQUEST_PIC);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 选取图片的返回值
        if (resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    String path = URIUtil.uriToPath(getActivity(), selectedImage);
                    setPicture(path);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setPicture(String path) {

        String smallImagePath = path;
        smallImagePath = BitmapUtil.check(smallImagePath);
        Bitmap smaillBit = BitmapUtil.getSmallBitmap(smallImagePath, 50, 50);
        String smallPath = BitmapUtil.saveBitmapFile(smaillBit);
        modifyUserIcon(smallPath);
    }

    public void hideKeyboard() {
        // 隐藏输入法
        InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // 显示或者隐藏输入法
        imm.hideSoftInputFromWindow(nickName.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onDestroy() {
        api.removeListener(mdelegate);
        super.onDestroy();
    }

    public void modifyUserIcon(String smallImagePath) {
        String name = nickName.getText().toString().trim();
        GotyeUser forModify = new GotyeUser(user.getName());
        forModify.setNickname(name);
        //个性签名设置
        //这里是设置个性签名了
        if (Utils.checkNameChese(qianming.getText().toString())) {
            forModify.setInfo(qianming.getText().toString());
        } else {
            Utils.ShowToast(getActivity(), "error_签名只能是中文哦");
            return;
        }
        forModify.setGender(user.getGender());
        api.reqModifyUserInfo(forModify, smallImagePath);
        qianming.setCursorVisible(false);

    }

    private GotyeDelegate mdelegate = new GotyeDelegate() {

        @Override
        public void onDownloadMedia(int code, GotyeMedia media) {
            if (media.getUrl() != null && media.getUrl().equals(user.getIcon().getUrl())) {
                Bitmap bm = BitmapUtil.getBitmap(media.getPath());
                if (bm != null) {
                    iconImageView.setImageBitmap(bm);
                }
            }
        }

        @Override
        public void onGetUserDetail(int code, GotyeUser user) {
            if (user != null && user.getName().equals(SettingFragment.this.user.getName())) {
                setUserInfo(user);
            }
        }

        @Override
        public void onModifyUserInfo(int code, GotyeUser user) {
            if (code == 0) {
                setUserInfo(user);
                // ToastUtil.show(getActivity(), "修改成功!");
            } else {
                Utils.ShowToast(getActivity(), "修改失败!");
            }
        }

        @Override
        public void onLogin(int code, GotyeUser currentLoginUser) {

        }

        @Override
        public void onLogout(int code) {
            if (code == 0) {
                return;
            }
        }

        @Override
        public void onReconnecting(int code, GotyeUser currentLoginUser) {

        }
    };
}
