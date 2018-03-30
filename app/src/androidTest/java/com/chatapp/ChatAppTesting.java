package com.chatapp;

import android.support.annotation.NonNull;
import android.test.AndroidTestCase;

import com.chatapp.models.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import junit.framework.AssertionFailedError;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Usman on 3/30/18.
 */

public class ChatAppTesting extends AndroidTestCase {
    //private static Logger logger = LoggerFactory.getLogger(ChatAppTesting.class);

    private CountDownLatch authSignal = null;
    private FirebaseAuth auth;

    @Override
    public void setUp() throws InterruptedException {
       /* authSignal = new CountDownLatch(1);
       // Firebase.setAndroidContext(mContext); //initializeFireBase(context);

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null) {
            auth.signInWithEmailAndPassword("ali@gmail.com", "12345678").addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {

                            final AuthResult result = task.getResult();
                            final FirebaseUser user = result.getUser();
                            authSignal.countDown();
                        }
                    });
        } else {
            authSignal.countDown();
        }
        authSignal.await(10, TimeUnit.SECONDS);*/
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        if(auth != null) {
            auth.signOut();
            auth = null;
        }
    }
    @Test
    public void testLogin() throws InterruptedException{
        authSignal = new CountDownLatch(1);
        // Firebase.setAndroidContext(mContext); //initializeFireBase(context);

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null) {
            auth.signInWithEmailAndPassword("ali@gmail.com", "12345678").addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {

                            final AuthResult result = task.getResult();
                            final FirebaseUser user = result.getUser();
                               //assertEquals(null,user);
                            authSignal.countDown();


                        }
                    });
        } else {authSignal.countDown();
        }

        authSignal.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void testSignUp() throws InterruptedException{
        authSignal = new CountDownLatch(1);

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null) {
            auth.createUserWithEmailAndPassword("usmankhalid@gmail.com", "12345678").addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {

                            final AuthResult result = task.getResult();
                            final FirebaseUser user = result.getUser();


                        }
                    });
        } else {
            authSignal.countDown();
        }
       authSignal.await(10, TimeUnit.SECONDS);
    }

}