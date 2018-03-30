package com.chatapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chatapp.R;
import com.chatapp.activities.MainActivity;
import com.chatapp.utils.AppUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog mProgressDialog;
    private boolean isLoginTest=false;
    public LoginFragment() {
    }



    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void showProgress(){
        mProgressDialog=ProgressDialog.show(getActivity(),"",getString(R.string.login_));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

    }
    private void hideProgress(){

        if(mProgressDialog!=null)
            mProgressDialog.dismiss();
    }

    private  void init(View view){
        mEmailEditText=(EditText)view.findViewById(R.id.email);
        mPasswordEditText=(EditText)view.findViewById(R.id.password);
        mLoginButton=(Button) view.findViewById(R.id.login);
        mLoginButton.setOnClickListener(this);
        mFirebaseAuth= FirebaseAuth.getInstance();
    }


    private boolean TestLogin(){

        String ab="";
        return isLoginTest;
    }
    protected void login(String email,String password){

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Login Successfully ", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            FirebaseMessaging.getInstance().subscribeToTopic("chat");

                            isLoginTest=true;

                            MainActivity activity=(MainActivity)getActivity();
                            activity.loadFragment(ChatFragment.newInstance());
                            activity.invalidateOptionsMenu();
                            hideProgress();
                        } else {
                            hideProgress();
                            Log.d("Error",task.getException()+"");
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.login) {
            String email = mEmailEditText.getText().toString().trim();
            String password = mPasswordEditText.getText().toString();
            if (AppUtils.isEmailAndPasswordValid(email, password)) {
                showProgress();
                login(email, password);
                TestLogin();

            } else {
                Toast.makeText(getActivity(), "Email or Password in not Valid,Please Enter Valid Email or Password!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
