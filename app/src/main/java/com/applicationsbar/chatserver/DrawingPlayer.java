package com.applicationsbar.chatserver;

public class DrawingPlayer {
    String name; // the name of the drawing player
    String keyword; // the current keyword that needs to be guessed
    int chatRoom; // chat room number

    public DrawingPlayer(String name,int chatRoom)
    {
        this.name = name;
        this.keyword = new RandomWord().GetWord();
        this.chatRoom = chatRoom;
    }

    public DrawingPlayer(int chatRoom)
    {
        this.chatRoom = chatRoom;
        this.name = null;
        this.keyword =new RandomWord().GetWord();
    }


    public void SetName(String name)
    {
        this.name = name;
    }

    public void SetKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public void SetChatRoom(int chatRoom){this.chatRoom = chatRoom;}


    public  String GetName() { return this.name; }

    public String GetKeyword(){ return this.keyword; }

    public int GetChatRoom(){ return this.chatRoom; }


}


