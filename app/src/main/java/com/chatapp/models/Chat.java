package com.chatapp.models;

/**
 * Created by Usman on 3/28/18.
 */

//@IgnoreExtraProperties
public class Chat {

    private String name;
    private String message;
    private String imageUrl;
    String uuid;

    public Chat(String name,String message,String uuid){
        this.name=name;
        this.message=message;
        this.uuid=uuid;
    }

    public Chat(String name,String message,String uuid,String imageUrl){
        this.name=name;
        this.message=message;
        this.uuid=uuid;
        this.imageUrl=imageUrl;
    }

    public Chat(){

    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
