package com.chat;

import java.io.Serializable;

public class Message implements Serializable {
    private  String sender="";
    private  String message="";
    private  String receiver="";
    private  String type="";

    public Message(String message){
        this.message=message;
    }
    public Message(String sender,String message){
        this.message=message;
        this.sender=sender;
        this.receiver=null;
    }
    public Message(String sender, String receiver, String message){
        this.sender=sender;
        this.receiver=receiver;
        this.message=message;
    }
    @Override
    public String toString() {
        return message;
    }
    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }
    public void setType(String type){
        this.type=type;
    }
    public String type(){
        return type;
    }
    public void setReceiver(String receiver){
        this.receiver=receiver;
    }
    public void setContent(String message){
        this.message=message;
    }
}
