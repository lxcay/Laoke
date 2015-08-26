package com.lxcay.laoke;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeChatTargetType;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeMessageStatus;
import com.gotye.api.GotyeNotify;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;
import com.lxcay.laoke.activity.CreateGroupSelectUser;
import com.lxcay.laoke.activity.SearchPage;
import com.lxcay.laoke.adapter.FragmentAdapters;
import com.lxcay.laoke.fragment.ContactsFragment;
import com.lxcay.laoke.fragment.MessageFragment;
import com.lxcay.laoke.fragment.SettingFragment;
import com.lxcay.laoke.listener.PageChangeListener;
import com.lxcay.laoke.utils.BeepManager;
import com.lxcay.laoke.utils.ImageCache;
import com.lxcay.laoke.utils.ProgressDialogUtil;
import com.lxcay.laoke.utils.Utils;
import com.lxcay.laoke.view.CustomRadioGroup;

import ads.Lxcay;


public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private int[] itemImage = {R.mipmap.main_footer_message, R.mipmap.main_footer_contanct, R.mipmap.main_footer_me};
    private int[] itemCheckedImage = {R.mipmap.main_footer_message_selected, R.mipmap.main_footer_contanct_selected, R.mipmap.main_footer_me_selected};
    private String[] itemText = {"信息", "通讯录", "设置"};
    private ViewPager mViewPager;
    private FragmentAdapters mAdapter;
    private CustomRadioGroup footer;
    private int Message = 0, Contacts = 1, Setting = 2;
    private MessageFragment mMessageFragment;
    private ContactsFragment mContactsFragment;
    private SettingFragment mSettingFragment;
    private PopupWindow mPopupWindow;
    public static TextView mainTitle;
    public TextView tv_back;
    //------------------------------------

    private GotyeAPI api;
    private BeepManager beep;
    private int currentPosition = 0;
    //当前的用户名
    public String currentLoginName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        api = GotyeAPI.getInstance();
        setContentView(R.layout.activity_main);
        api.addListener(mDelegate);

        //初始化id
        ImageView main_add = (ImageView) findViewById(R.id.title_bar_layout_add);
        mainTitle=(TextView)findViewById(R.id.title_bar_layout_tv);
        main_add.setOnClickListener(this);

        tv_back = (TextView)findViewById(R.id.title_bar_layout_back);
        tv_back.setOnClickListener(this);
        tv_back.setText(R.string.app_name);

        currentLoginName = api.getLoginUser().getName();
        beep = new BeepManager(MainActivity.this);
        beep.updatePrefs();


        //---------------------------------------
        // 底部
        footer = (CustomRadioGroup) findViewById(R.id.main_footer);
        for (int i = 0; i < itemImage.length; i++) {
            footer.addItem(itemImage[i], itemCheckedImage[i], itemText[i]);
        }
        Lxcay.getbanner(this);
        //主体
        mViewPager = (ViewPager) findViewById(R.id.main_body);
        mAdapter = new FragmentAdapters(getSupportFragmentManager());
        //设置适配器
        mViewPager.setAdapter(mAdapter);
        mMessageFragment = (MessageFragment) mAdapter.getItem(Message);
        mContactsFragment = (ContactsFragment) mAdapter.getItem(Contacts);
        mSettingFragment = (SettingFragment) mAdapter.getItem(Setting);

        //界面切换的监听器
        PageChangeListener bodyChangeListener = new PageChangeListener(footer);
        mViewPager.addOnPageChangeListener(bodyChangeListener);

        //设置底部的选中item
        footer.setCheckedIndex(mViewPager.getCurrentItem());
        //设置底部 item的监听
        footer.setOnItemChangedListener(new CustomRadioGroup.OnItemChangedListener() {
            public void onItemChanged() {
                //设置ViewPager的联动
                mViewPager.setCurrentItem(footer.getCheckedIndex(), false);
                updateUnReadTip();
                currentPosition = footer.getCheckedIndex();
            }
        });
        Lxcay.getpush(this);
        //设置默认选中item
        mViewPager.setCurrentItem(0);
        //设置预加载个数
        mViewPager.setOffscreenPageLimit(2);
        /**
         * BUG :显示不出数字。数字尺寸太大
         */
//        footer.setItemNewsCount(0, 15);//设置消息数量
    }


    //-------------------------------------------------------
    // 页面刷新
    private void mainRefresh() {
        updateUnReadTip();
        mMessageFragment.refresh();
        if (mContactsFragment != null) {
            mContactsFragment.refresh();
        }
    }

    private GotyeDelegate mDelegate = new GotyeDelegate() {

        // 此处处理账号在另外设备登陆造成的被动下线
        @Override
        public void onLogout(int code) {
            ImageCache.getInstance().clear();
            if (code == GotyeStatusCode.CodeForceLogout) {
                Toast.makeText(MainActivity.this, "您的账号在另外一台设备上登录了！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), WelcomePage.class);
                startActivity(intent);
                finish();
            } else if (code == GotyeStatusCode.CodeNetworkDisConnected) {
                Toast.makeText(MainActivity.this, "您的账号掉线了！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), WelcomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            } else {
                Toast.makeText(MainActivity.this, "退出登陆！", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, WelcomePage.class);
                startActivity(i);
                finish();
            }
        }

        // 收到消息（此处只是单纯的更新聊天历史界面，不涉及聊天消息处理，当然你也可以处理，若你非要那样做）
        @Override
        public void onReceiveMessage(GotyeMessage message) {
            if (returnNotify) {
                return;
            }
            mMessageFragment.refresh();
            if (message.getStatus() == GotyeMessageStatus.GotyeMessageStatusUnread) {
                updateUnReadTip();
                if (!MyApplication.isNewMsgNotify()) {
                    return;
                }
                if (message.getReceiver().getType() == GotyeChatTargetType.GotyeChatTargetTypeGroup) {
                    if (MyApplication.isNotReceiveGroupMsg()) {
                        return;
                    }
                    if (MyApplication.isGroupDontdisturb(((GotyeGroup) message
                            .getReceiver()).getGroupID())) {
                        return;
                    }
                }
                beep.playBeepSoundAndVibrate();
            }
        }

        // 自己发送的信息统一在此处理
        @Override
        public void onSendMessage(int code, GotyeMessage message) {
            if (returnNotify) {
                return;
            }
            mMessageFragment.refresh();
        }

        // 收到群邀请信息
        @Override
        public void onReceiveNotify(GotyeNotify notify) {
            if (returnNotify) {
                return;
            }
            mMessageFragment.refresh();
            updateUnReadTip();
            if (!MyApplication.isNotReceiveGroupMsg()) {
                beep.playBeepSoundAndVibrate();
            }
        }

        @Override
        public void onRemoveFriend(int code, GotyeUser user) {
            if (returnNotify) {
                return;
            }
            api.deleteSession(user, false);
            mMessageFragment.refresh();
            mContactsFragment.refresh();
        }

        @Override
        public void onAddFriend(int code, GotyeUser user) {
            // TODO Auto-generated method stub
            if (returnNotify) {
                return;
            }
            if (currentPosition == 1) {
                mContactsFragment.refresh();
            }
        }

        @Override
        public void onGetMessageList(int code, int totalCount) {
            if (totalCount > 0) {
                mainRefresh();
            }
        }
    };

    //清理推送通知
    private void clearNotify() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    /**
     * 信息提醒的小红点
     */
    public void updateUnReadTip() {
        int unreadCount = api.getTotalUnreadMessageCount();
        int unreadNotifyCount = api.getUnreadNotifyCount();
        unreadCount += unreadNotifyCount;
        if (unreadCount > 0 && unreadCount < 100) {
            footer.setItemNewsCount(0, unreadCount);//设置消息数量
        } else if (unreadCount >= 100) {
            footer.setItemNewsCount(0, 99);//设置消息数量
        } else {
            footer.setItemNewsCount(0, 0);//设置消息数量
        }
        mMessageFragment.refresh();
    }

    private boolean returnNotify = false;

    @Override
    protected void onResume() {
        super.onResume();
        returnNotify = false;
        mainRefresh();
        // 清理掉通知栏
        clearNotify();
    }

    @Override
    protected void onPause() {
        returnNotify = true;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // 保持好习惯，销毁时请移除监听
        api.removeListener(mDelegate);
        // 告诉service已经处于后台运行状态
        ProgressDialogUtil.dismiss();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //添加按钮点击
            case R.id.title_bar_layout_add:
                showmPopupWindow(v);
                break;
            //返回键处理
            case R.id.title_bar_layout_back:

                break;
            //查询好友
            case R.id.tools_search:
                searchUser();
                break;
            case R.id.tools_add_user:
                //添加好友
                addSingelUser();
                break;
            case R.id.tools_crate_group:
                //创建一个群
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                    Intent toCreateGroup = new Intent(this, CreateGroupSelectUser.class);
                    startActivity(toCreateGroup);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 查询好友
     */
    private void searchUser() {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            Intent toSreach = new Intent(this, SearchPage.class);
            toSreach.putExtra("search_type", 0);
            startActivity(toSreach);
        }
    }

    private void addSingelUser() {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            final EditText input = new EditText(this);
            new AlertDialog.Builder(this).setTitle("添加好友").setIcon(android.R.drawable.ic_dialog_info).setView(input).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = input.getText().toString();
                    if (!TextUtils.isEmpty(name)) {
                        if (name.equals(currentLoginName)) {
                            Utils.ShowToast(MainActivity.this, "不能添加自己");
                            return;
                        }
                        ProgressDialogUtil.showProgress(MainActivity.this, "正在添加好友...");
                        api.reqAddFriend(new GotyeUser(name));
                    }
                }
            }).setNegativeButton("取消", null).show();
        }
    }

    private void showmPopupWindow(View v) {
        View mPopupWindowLayout = LayoutInflater.from(this).inflate(R.layout.layout_tools, null);
        mPopupWindowLayout.findViewById(R.id.tools_add_user).setOnClickListener(this);
        mPopupWindowLayout.findViewById(R.id.tools_crate_group).setOnClickListener(this);
        mPopupWindowLayout.findViewById(R.id.tools_search).setOnClickListener(this);
        mPopupWindow = new PopupWindow(mPopupWindowLayout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.showAsDropDown(v, 0, 20);
        mPopupWindow.update();
    }
}
