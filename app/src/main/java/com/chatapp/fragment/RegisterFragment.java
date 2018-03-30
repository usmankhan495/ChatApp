package com.chatapp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.messaging.FirebaseMessaging;


public class RegisterFragment extends Fragment implements View.OnClickListener {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mNameEditText;
    private Button mSignUpButton;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog mProgressDialog;



    public RegisterFragment() {

    }


    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private void init(View view){
        mEmailEditText=(EditText)view.findViewById(R.id.email);
        mPasswordEditText=(EditText)view.findViewById(R.id.password);
        mNameEditText=(EditText)view.findViewById(R.id.name);
        mSignUpButton=(Button) view.findViewById(R.id.signup);
        mSignUpButton.setOnClickListener(this);
        mFirebaseAuth=FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }


    private void showProgress(){
        mProgressDialog=ProgressDialog.show(getActivity(),"","Register...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

    }
    private void hideProgress(){

        if(mProgressDialog!=null)
            mProgressDialog.dismiss();
    }

    private void createAccount(String email, String password, final String name){
        showProgress();

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            UserProfileChangeRequest setName = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();
                            user.updateProfile(setName);
                            FirebaseMessaging.getInstance().subscribeToTopic("chat");
                            MainActivity activity=(MainActivity)getActivity();
                            activity.loadFragment(ChatFragment.newInstance());
                            activity.invalidateOptionsMenu();
                            hideProgress();
                        } else {
                            Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            hideProgress();
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signup) {
            String email = mEmailEditText.getText().toString().trim();
            String password = mPasswordEditText.getText().toString();
            String name=mNameEditText.getText().toString().trim();
            if(!name.isEmpty()){
                if (AppUtils.isEmailAndPasswordValid(email, password)) {
                createAccount(email, password,name);

            } else {
                Toast.makeText(getActivity(), "Email or Password in not Valid,Please Enter Valid Email or Password!", Toast.LENGTH_SHORT).show();
            }

        }else{
                Toast.makeText(getActivity(), "Please Enter Name", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
