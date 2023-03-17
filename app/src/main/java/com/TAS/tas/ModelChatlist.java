package com.TAS.tas;

public class ModelChatlist
{
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public ModelChatlist() {

    }

    public ModelChatlist(String id) {
        this.id = id;
    }
}
