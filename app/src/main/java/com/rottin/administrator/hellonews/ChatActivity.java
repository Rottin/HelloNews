package com.rottin.administrator.hellonews;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.library.bubbleview.BubbleTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
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

        sendNowTime();
        sendGreeting();
        getUsername();

//        for (int i=0;i<20;i++)
//            sendGreeting();

        mAsyncTask asyncTask = new mAsyncTask();
        asyncTask.execute();
    }

    //初始化
    private void init() {
        chatListView = (ListView) findViewById(R.id.chat_list);
        chatArrayList = new ArrayList<ChatData>();
        chatAdapter = new ChatAdapter(this, chatArrayList);
        chatListView.setAdapter(chatAdapter);
    }

    private void getUsername(){
        final SharedPreferences preferences = getSharedPreferences("com.hellonews", Context.MODE_PRIVATE);
        boolean isFirst = preferences.getBoolean("first", true);
        String username = preferences.getString("username", null);
        if(isFirst == true || username == null){
            final EditText editText = (EditText)findViewById(R.id.chat_edittext);
            final Button sendButton = (Button)findViewById(R.id.chat_send_button);
            final LinearLayout layout = (LinearLayout)findViewById(R.id.send_layout);

            sendAChat("初次见面，你叫什么名字？", 0);

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2017/11/14 用户名规范性检查
                    String username = editText.getText().toString().trim();
                    editText.setText("");
                    if(username.equals("") || username == null)
                        sendAChat("输入不能为空，也不能是空白符号", 0);
                    else {
                        sendAChat(username, 1);
                        sendAChat(username+"，你好", 0);
                        sendAChat("很高兴认识你", 0);
                        preferences.edit().putBoolean("first", false).commit();
                        preferences.edit().putString("username", username).commit();
                        layout.setVisibility(View.GONE);

                    }

                }
            });
        }
    }
    //发送一个聊天内容（在界面添加一个气泡
    private void sendAChat(String content, int type) {

        // TODO: 2017/11/12 如果同时大量添加，气泡顺序混乱

        ChatData chatData = new ChatData(content, type);
        chatArrayList.add(chatData);
        chatAdapter.notifyDataSetChanged();
//        Thread.sleep(100);
    }
    private void sendNowTime(){
        Calendar calendar = Calendar.getInstance();
        String time = ""
                + calendar.get(Calendar.YEAR)+"年"
                + calendar.get(Calendar.MONTH)+"月"
                +calendar.get(Calendar.DAY_OF_MONTH)+"日"
                + "    "
                + calendar.get(Calendar.HOUR_OF_DAY)+":";
        if(calendar.get(Calendar.MINUTE)/10 == 0)
            time  += "0"+calendar.get(Calendar.MINUTE);
        else
            time += calendar.get(Calendar.MINUTE);
        Log.d(TAG, "NowTime:"+time);

        ChatData chatData = new ChatData(time, 2);
        chatArrayList.add(chatData);
        chatAdapter.notifyDataSetChanged();
    }
    private void sendGreeting(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String greeting = "";
        if(hour>=4 &&hour <10)
            greeting = "早啊";
        else if (hour>=10 &&hour <12)
            greeting = "上午好";
        else if (hour>=12 &&hour <18)
            greeting = "下午好";
        else if (hour>=18 &&hour <=24)
            greeting = "晚上好";
        else if (hour>=0&&hour<4)
            greeting = "夜深了";

        sendAChat(greeting,0);
    }


    private class ChatAdapter extends BaseAdapter {

        Context context;
        ArrayList<ChatData> chatArrayList = null;
        int[] bubbleLayout = {R.layout.bubble_arrow_to_left, R.layout.bubble_arrow_to_right, R.layout.time_list_item};
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
            ChatData chatData = chatArrayList.get(position);
            int type = chatData.getType();
            convertView = inflater.inflate(bubbleLayout[type], null);
            if(type == 0|| type == 1){
                final BubbleTextView bubbleTextView = (BubbleTextView) convertView.findViewById(R.id.bubble);
                char[] contentToChar = chatData.getContent().toCharArray();
                Log.d("Chat", chatData.getContent() + "");
                bubbleTextView.setText(contentToChar, 0, contentToChar.length);
                bubbleTextView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //长按复制内容
                        ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                        ClipData.Item item = new ClipData.Item(bubbleTextView.getText());
                        ClipDescription description = new ClipDescription(null,new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN});
                        ClipData clipData = new ClipData(description,item);
                        clipboardManager.setPrimaryClip(clipData);
                        Toast.makeText(ChatActivity.this, "已复制",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }else if (type == 2){
                TextView timeTextView = (TextView)convertView.findViewById(R.id.time_textview);
                timeTextView.setText(chatData.getContent());
            }

            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
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
            sendAChat(s, 0);
        }
    }
}
