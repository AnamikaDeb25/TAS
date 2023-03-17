package com.TAS.tas;

import com.google.firebase.database.PropertyName;

public class ModelChat {
    String message;
    String receiver;
    String Sender;
    String timestamp;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getDelete() {
        return delete;
    }

    public void setDelete(long delete) {
        this.delete = delete;
    }

    String type;
    String audio;
    String image;
    long delete;
    boolean isSeen;

    public ModelChat(){

    }



    public ModelChat(String message, String receiver, String sender, String timestamp, String type, String audio, String image, long delete, boolean isSeen) {
        this.message = message;
        this.receiver = receiver;
        Sender = sender;
        this.timestamp = timestamp;
        this.type = type;
        this.audio = audio;
        this.image = image;
        this.delete = delete;
        this.isSeen = isSeen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String Sender) {
        this.Sender = Sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @PropertyName("isSeen")
    public boolean isSeen() {
        return isSeen;
    }


    @PropertyName("isSeen")
    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
