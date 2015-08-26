package com.lxcay.laoke.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeChatTargetType;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeGroup;
import com.gotye.api.GotyeMedia;
import com.gotye.api.GotyeRoom;
import com.gotye.api.GotyeUser;
import com.lxcay.laoke.R;
import com.lxcay.laoke.activity.ChatPage;
import com.lxcay.laoke.activity.NotifyListPage;
import com.lxcay.laoke.adapter.MessageListAdapter;
import com.lxcay.laoke.utils.AnimationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxcay on 2015/7/23.
 */
public class MessageFragment extends Fragment {
    public static final String fixName = "通知列表";
    private ListView mListview;
    private MessageListAdapter adapter;

    private GotyeAPI api = GotyeAPI.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_fragment_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GotyeAPI.getInstance().addListener(mDelegate);
        initview();
    }

    private void initview() {
        mListview = (ListView) getView().findViewById(R.id.swipe_listview);

        mListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.delete_dialog);
                dialog.findViewById(R.id.delete_dialog_tv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GotyeChatTarget target = adapter.getItem(position);
                        api.deleteSession(target, false);
                        refresh();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
        });
        updateList();
        setListener();
    }

    private void setListener() {
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GotyeChatTarget target = (GotyeChatTarget) adapter.getItem(position);
                //传递用户名
                GotyeUser user = api.getUserDetail(target, false);
                if (target.getName().equals(fixName)) {
                    Intent i = new Intent(getActivity(), NotifyListPage.class);
                    startActivity(i);
                    AnimationUtil.ActivityAnimation(getActivity(), 0);
                } else {
                    GotyeAPI.getInstance().markMessagesAsRead(target, true);

                    if (target.getType() == GotyeChatTargetType.GotyeChatTargetTypeUser) {
                        Intent toChat = new Intent(getActivity(), ChatPage.class);
                        toChat.putExtra("user", user);
                        startActivity(toChat);
                        AnimationUtil.ActivityAnimation(getActivity(), 0);
                    } else if (target.getType() == GotyeChatTargetType.GotyeChatTargetTypeRoom) {
                        Intent toChat = new Intent(getActivity(), ChatPage.class);
                        toChat.putExtra("room", (GotyeRoom) target);
                        startActivity(toChat);
                        AnimationUtil.ActivityAnimation(getActivity(), 0);
                    } else if (target.getType() == GotyeChatTargetType.GotyeChatTargetTypeGroup) {
                        Intent toChat = new Intent(getActivity(), ChatPage.class);
                        toChat.putExtra("group", (GotyeGroup) target);
                        startActivity(toChat);
                        AnimationUtil.ActivityAnimation(getActivity(), 0);
                    }
                    refresh();
                }
            }
        });
    }

    private void updateList() {
        List<GotyeChatTarget> sessions = api.getSessionList();
        Log.d("offLine", "List--sessions" + sessions.size());

        GotyeChatTarget target = new GotyeUser(fixName);

        if (sessions == null) {
            sessions = new ArrayList<GotyeChatTarget>();
            sessions.add(target);
        } else {
            sessions.add(0, target);
        }
        if (adapter == null) {
            adapter = new MessageListAdapter(MessageFragment.this, sessions);
            mListview.setAdapter(adapter);
        } else {
            adapter.setData(sessions);
        }
    }


    public void refresh() {
        if (mListview != null)
            updateList();
    }

    private GotyeDelegate mDelegate = new GotyeDelegate() {

        @Override
        public void onDownloadMedia(int code, GotyeMedia media) {

            if (getActivity().isTaskRoot()) {
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onLogout(int code) {

        }

        @Override
        public void onLogin(int code, GotyeUser currentLoginUser) {

        }

        @Override
        public void onReconnecting(int code, GotyeUser currentLoginUser) {

        }
    };

    @Override
    public void onDestroy() {
        GotyeAPI.getInstance().removeListener(mDelegate);
        super.onDestroy();
    }
}
