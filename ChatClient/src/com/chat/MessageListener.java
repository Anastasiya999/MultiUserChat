package com.chat;


public interface MessageListener {
    public void onMsg(String fromLogin, String msgBody);
}
