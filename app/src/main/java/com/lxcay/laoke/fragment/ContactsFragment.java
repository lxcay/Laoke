package com.lxcay.laoke.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeMedia;
import com.gotye.api.GotyeUser;
import com.lxcay.laoke.R;
import com.lxcay.laoke.activity.ChatPage;
import com.lxcay.laoke.activity.GroupRoomListPage;
import com.lxcay.laoke.activity.UserInfoPage;
import com.lxcay.laoke.adapter.ContactsAdapter;
import com.lxcay.laoke.bean.GotyeUserProxy;
import com.lxcay.laoke.utils.CharacterParser;
import com.lxcay.laoke.utils.PinyinComparator;
import com.lxcay.laoke.utils.ProgressDialogUtil;
import com.lxcay.laoke.utils.Utils;
import com.lxcay.laoke.view.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lxcay on 2015/7/23.
 */
public class ContactsFragment extends Fragment {
    private ListView userListView;
    private SideBar sideBar;
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator = new PinyinComparator();
    private ArrayList<GotyeUserProxy> proxyFrinds = new ArrayList<GotyeUserProxy>();
    private List<GotyeUser> friends = new ArrayList<GotyeUser>();
    private ContactsAdapter adapter;
    public String currentLoginName;
    private EditText search;
    private boolean showAddFriendTip = false;
    public GotyeAPI api = GotyeAPI.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return inflater.inflate(R.layout.contacts_fragment_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        api.addListener(mDelegate);
        characterParser = CharacterParser.getInstance();
        GotyeUser user = api.getLoginUser();
        currentLoginName = user.getName();
        loadLocalFriends();
        api.reqFriendList();
        initView();
        setAdapter();
    }

    private void loadLocalFriends() {
        friends = api.getLocalFriendList();
        if (characterParser != null)
            handleUser(friends);
    }

    public void initView() {
        sideBar = (SideBar) getView().findViewById(R.id.sidrbar);
        userListView = (ListView) getView().findViewById(R.id.listview);
        search = (EditText) getView().findViewById(R.id.contact_search_input);
        search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                search(keyword);
            }
        });
    }

    private void search(String keyWord) {
        if (TextUtils.isEmpty(keyWord)) {
            selectUserByKeyword(null);
        } else {
            selectUserByKeyword(keyWord);
        }
        adapter.notifyDataSetChanged();

    }

    private void handleUser(List<GotyeUser> userList) {
        proxyFrinds.clear();
        if (userList != null) {
            for (GotyeUser user : userList) {
                String pinyin = characterParser.getSelling(user.getName());
                String sortString = pinyin.substring(0, 1).toUpperCase();
                GotyeUserProxy userProxy = new GotyeUserProxy(user);
                if (sortString.matches("[A-Z]")) {
                    userProxy.firstChar = sortString.toUpperCase();
                } else {
                    userProxy.firstChar = "#";
                }
                proxyFrinds.add(userProxy);
            }
            Collections.sort(proxyFrinds, pinyinComparator);
        }
        GotyeUserProxy room = new GotyeUserProxy(new GotyeUser());
        room.gotyeUser.setId(-2);
        room.firstChar = "↑";
        proxyFrinds.add(0, room);
        GotyeUserProxy group = new GotyeUserProxy(new GotyeUser());
        group.gotyeUser.setId(-1);
        group.firstChar = "↑";
        proxyFrinds.add(1, group);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

    }

    private void selectUserByKeyword(String keyWord) {
        proxyFrinds.clear();
        if (friends != null) {
            for (GotyeUser user : friends) {
                if (user.getId() < 0) {
                    continue;
                }
                String pinyin = characterParser.getSelling(user.getName());
                if (keyWord != null) {
                    if (!pinyin.startsWith(keyWord.toLowerCase())) {
                        continue;
                    }
                }
                String sortString = pinyin.substring(0, 1).toUpperCase();
                GotyeUserProxy userProxy = new GotyeUserProxy(user);
                if (sortString.matches("[A-Z]")) {
                    userProxy.firstChar = sortString.toUpperCase();
                } else {
                    userProxy.firstChar = "#";
                }
                proxyFrinds.add(userProxy);
            }
            Collections.sort(proxyFrinds, pinyinComparator);

        }
        GotyeUserProxy room = new GotyeUserProxy(new GotyeUser());
        room.gotyeUser.setId(-2);
        room.firstChar = "↑";
        proxyFrinds.add(0, room);
        GotyeUserProxy group = new GotyeUserProxy(new GotyeUser());
        group.gotyeUser.setId(-1);
        group.firstChar = "↑";
        proxyFrinds.add(1, group);
    }

    private void setAdapter() {
        adapter = new ContactsAdapter(getActivity(), proxyFrinds);
        userListView.setAdapter(adapter);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                GotyeUserProxy userProxy = ((GotyeUserProxy) adapter.getItem(arg2));
                if (userProxy.gotyeUser.getId() == -2) {
                    Intent room = new Intent(getActivity(), GroupRoomListPage.class);
                    room.putExtra("type", 0);
                    startActivity(room);
                    return;
                }
                if (userProxy.gotyeUser.getId() == -1) {
                    Intent group = new Intent(getActivity(), GroupRoomListPage.class);
                    group.putExtra("type", 1);
                    startActivity(group);
                    return;
                }
                Intent i = new Intent(getActivity(), ChatPage.class);
                i.putExtra("user", userProxy.gotyeUser);
                i.putExtra("from", 200);
                startActivity(i);

            }
        });
        userListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                GotyeUserProxy userProxy = ((GotyeUserProxy) adapter.getItem(arg2));
                if (userProxy.gotyeUser.getId() < 0) {
                    return true;
                }
                Intent i = new Intent(getActivity(), UserInfoPage.class);
                i.putExtra("user", userProxy.gotyeUser);
                startActivity(i);
                return true;
            }
        });
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    userListView.setSelection(position);
                }

            }
        });

    }

    public void refresh() {
        loadLocalFriends();
    }

    public void hideKeyboard(View view) {
        // 隐藏输入法
        InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        // 显示或者隐藏输入法
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    public void onDestroy() {
        api.removeListener(mDelegate);
        super.onDestroy();
    }


    private GotyeDelegate mDelegate = new GotyeDelegate() {

        @Override
        public void onGetFriendList(int code, List<GotyeUser> mList) {
            refresh();
        }

        @Override
        public void onGetBlockedList(int code, List<GotyeUser> mList) {
            refresh();
        }

        @Override
        public void onAddFriend(int code, GotyeUser user) {
            ProgressDialogUtil.dismiss();

            if (code == 0) {
                if (showAddFriendTip) {
                    Utils.ShowToast(getActivity(), "添加好友成功");
                }

                loadLocalFriends();
            } else {
                if (showAddFriendTip) {
                    Utils.ShowToast(getActivity(), "添加好友失败");
                }
            }
            showAddFriendTip = false;
        }

        @Override
        public void onDownloadMedia(int code, GotyeMedia media) {
            if (getActivity().isTaskRoot()) {
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onLogin(int code, GotyeUser currentLoginUser) {
        }

        @Override
        public void onLogout(int code) {
        }

        @Override
        public void onReconnecting(int code, GotyeUser currentLoginUser) {
        }
    };

}
