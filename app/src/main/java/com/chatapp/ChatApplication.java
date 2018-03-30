package com.chatapp;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * Created by Usman on 3/28/18.
 */

public class ChatApplication extends Application {

    private static boolean isOpen=false;

    public static boolean isChatOpen(){
        return isOpen;
    }

    public  static void setIsOpen(boolean open){
        isOpen=open;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
       // isOpen=true;
    }


}
