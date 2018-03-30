package com.chatapp.activities;


import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chatapp.R;
import com.chatapp.fragment.ChatFragment;
import com.chatapp.fragment.LoginFragment;
import com.chatapp.fragment.RegisterFragment;
import com.chatapp.utils.AppUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FragmentManager mFragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        stateCheck();


    }

    /*
    Loading Different Fragment
     */
    public void loadFragment(Fragment fragment){
         mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }


    /*
    Checking internet state
     */
    private void stateCheck(){
        if(!AppUtils.isNetworkAvailable(this)){
            showInternetBanner();
        }else {
            loadFragment(ChatFragment.newInstance());

            if (!isAnonymousLogin() && !isEmailPasswordLogin()) {
                anonymouslyLogin();
            }
        }
    }

    /*
    Checking for user login type is email and password
     */
    private boolean isEmailPasswordLogin(){

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            if(user.isAnonymous())
                return false;
            else
                return true;
        }

        return false;
    }

    /*
    No internet Banner
     */

    private void showInternetBanner(){
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "No Internet", Snackbar.LENGTH_INDEFINITE)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        finish();
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();
    }


    /*
     Anonymously Login user
     */
    private void anonymouslyLogin(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseMessaging.getInstance().subscribeToTopic("chat");

                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

/*
check for Anonymously Login
 */
    private boolean isAnonymousLogin(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            if (user.isAnonymous())
                return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        if(!isEmailPasswordLogin()){
            menu.findItem(R.id.logout).setVisible(false);
            menu.findItem(R.id.login).setVisible(true);
            menu.findItem(R.id.register).setVisible(true);
        }else{
            menu.findItem(R.id.logout).setVisible(true);
            menu.findItem(R.id.login).setVisible(false);
            menu.findItem(R.id.register).setVisible(false);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
               loadFragment(LoginFragment.newInstance());
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                anonymouslyLogin();
                invalidateOptionsMenu();
                return true;
            case R.id.register:
                loadFragment(RegisterFragment.newInstance());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
}
