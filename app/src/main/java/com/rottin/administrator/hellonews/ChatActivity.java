package com.rottin.administrator.hellonews;

import android.content.Context;
import android.nfc.Tag;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.zip.Inflater;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatData> chatArrayList = null;
    private static final String HOST = "172.20.46.30";
    private static final int PORT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        chatAdapter = new ChatAdapter(this, chatArrayList);
        chatListView.setAdapter(chatAdapter);
        mAsyncTask asyncTask = new mAsyncTask();
        asyncTask.execute();
    }

    //初始化
    private void init() {
        chatListView = (ListView) findViewById(R.id.chat_list);
        chatArrayList = new ArrayList<ChatData>();
    }

    //发送一个聊天内容（在界面添加一个气泡
    private void sendAChat(String content, int type) throws InterruptedException {

        // TODO: 2017/11/12 如果同时大量添加，气泡顺序混乱

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

    private class mAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String urlName = "http://" + HOST + ":" + PORT + "/hello";
            BufferedReader in = null;
            String content = "";
            try {
                URL readUrl = new URL(urlName);
                URLConnection conn = readUrl.openConnection();
                conn.connect();
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    content += line+"\n";
                    Log.d(TAG,"line:"+line);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(in != null)
                        in.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return content;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                sendAChat(s, 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
