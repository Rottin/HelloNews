package com.rottin.administrator.hellonews;

/**
 * Created by Administrator on 2017/11/12.
 */

public class ChatData {
    private String content;
    private int type;

    public ChatData(String content,int type){
        this.content = content;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(int type) {
        this.type = type;
    }
}
