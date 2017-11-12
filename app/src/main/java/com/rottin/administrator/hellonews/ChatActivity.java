package com.rottin.administrator.hellonews;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellIdentityGsm;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.github.library.bubbleview.BubbleTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.zip.Inflater;

public class ChatActivity extends AppCompatActivity {

    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatData> chatArrayList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        chatAdapter = new ChatAdapter(this, chatArrayList);
        chatListView.setAdapter(chatAdapter);
        for (int i=0;i<20;i++){
            try {
                sendAChat("早上好"+i,0);
                sendAChat("早上好"+i,1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //初始化
    private void init() {
        chatListView = (ListView) findViewById(R.id.chat_list);
        chatArrayList = new ArrayList<ChatData>();
    }

    //发送一个聊天内容（在界面添加一个气泡
    private void sendAChat(String content, int type) throws InterruptedException {

        // TODO: 2017/11/12 如果同时大量添加，气泡顺序混乱！

        ChatData chatData = new ChatData(content, type);
        chatArrayList.add(chatData);
        chatAdapter.notifyDataSetChanged();
        Thread.sleep(100);
    }

    private class ChatAdapter extends BaseAdapter {

        Context context;
        ArrayList<ChatData> chatArrayList = null;
        int[] bubbleLayout = {R.layout.bubble_arrow_to_left, R.layout.bubble_arrow_to_right};
        LayoutInflater inflater;

        public ChatAdapter(Context context, ArrayList<ChatData> chatArrayList) {
            this.context = context;
            this.chatArrayList = chatArrayList;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return chatArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO: 2017/11/12 在这里，如果使用convertView，会出现左右方向气泡混乱的问题，
            // 大概是因为是将缓存中的convertView使用在下一个view中
//            if (convertView == null) {
//                int type = chatArrayList.get(position).getType();
//                convertView = inflater.inflate(bubbleLayout[type], null);
//            }
            int type = chatArrayList.get(position).getType();
            convertView = inflater.inflate(bubbleLayout[type], null);

            BubbleTextView bubbleTextView = (BubbleTextView) convertView.findViewById(R.id.bubble);
            char[] contentToChar = chatArrayList.get(position).getContent().toCharArray();
            Log.d("Chat", chatArrayList.get(position).getContent() + "");
            bubbleTextView.setText(contentToChar, 0, contentToChar.length);
            return convertView;
        }
    }
}
