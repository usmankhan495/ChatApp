package com.chatapp.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chatapp.ChatApplication;
import com.chatapp.viewHolder.ChatViewHolder;
import com.chatapp.R;
import com.chatapp.models.Chat;
import com.chatapp.notification.FcmNotificationBuilder;
import com.chatapp.utils.AppUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ChatFragment extends Fragment implements View.OnClickListener {


    private Query mChatDatabse;
    private RecyclerView mRecyclerView;
    private EditText mMessageField;
    private ImageView mSendMessage;
    private ImageView mSelectImage;
    private StorageReference mStorageRef;
    private int GALLERY = 1, CAMERA = 2;
    private String mName="";
    private String mUUID="";
    public ChatFragment() {
    }


    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap image= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    Uri uri=getImageUri(getActivity(),image);
                    storeImage(uri);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAMERA) {
            if (data != null) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                Uri uri=getImageUri(getActivity(),thumbnail);
                storeImage(uri);
            }
        }

    }



/*
Setting Firebase Adapter in RecycleView
 */
    private void setAdapter() {
        final RecyclerView.Adapter adapter = SetUpFirebaseAdapter();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        mRecyclerView.setAdapter(adapter);
    }


    /*
    Create Firebase Recycle Adapter
     */
    private RecyclerView.Adapter SetUpFirebaseAdapter() {
        FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(mChatDatabse, Chat.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(options) {
            @Override
            public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ChatViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_object, parent, false));
            }


            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull Chat model) {
                if (model.getUuid().equals(mUUID)) {
                    holder.mNameTextView.setText("me");
                } else {
                    holder.mNameTextView.setText(model.getName());
                }
                if(!model.getImageUrl().equals("")){
                    holder.mImageView.setVisibility(View.VISIBLE);
                    Picasso.get().load(model.getImageUrl()).into(holder.mImageView);
                    holder.mMessageTextView.setVisibility(View.GONE);

                }else {
                    holder.mImageView.setVisibility(View.GONE);
                    holder.mMessageTextView.setVisibility(View.VISIBLE);
                        holder.mMessageTextView.setText(model.getMessage());
                    }
            }

            @Override
            public void onDataChanged() {
            }
        };
    }

    /*
    Initializing Views
     */

    private void init(View view){

        mRecyclerView=(RecyclerView) view.findViewById(R.id.recycle_view);
        mSendMessage=(ImageView)view.findViewById(R.id.send);
        mSelectImage=(ImageView)view.findViewById(R.id.select_image);
        mMessageField=(EditText)view.findViewById(R.id.message);
        mSendMessage.setOnClickListener(this);
        mSelectImage.setOnClickListener(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mStorageRef = FirebaseStorage.getInstance().getReference();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                mUUID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mChatDatabse = FirebaseDatabase.getInstance().getReference().child("chats").limitToLast(100);
        setAdapter();
    }



    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.send) {

            if (!mMessageField.getText().toString().trim().isEmpty()) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                    if (!FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
                        mName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        sendMessage(new Chat(mName, mMessageField.getText().toString(), mUUID,""));
                    } else
                        sendMessage(new Chat("anonymous", mMessageField.getText().toString(), mUUID,""));
            }
        }

            if (view.getId() == R.id.select_image) {
                int PERMISSION_ALL = 1;
                String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

                if (!AppUtils.hasPermissions(getActivity(), PERMISSIONS)) {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                } else
                    selectImage();
            }

    }


/*
save message to chat node in Firebase
 */
    private void sendMessage(Chat chat) {
        mChatDatabse.getRef().push().setValue(chat, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference reference) {
                mMessageField.setText("");
                FcmNotificationBuilder.initialize().send();

            }
        });
    }


/*
Getting Uri from Image bitmap
 */
    public Uri getImageUri(Context inContext, Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), image, "image", null);
        return Uri.parse(path);
    }

    /*
    Dialog for Image Selection
     */
    private void selectImage(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        dialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        dialog.show();
    }


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    /**
     * Storing Image to Firebase Cloud Storage
     * @param uri
     */
    private void storeImage(Uri uri){
        StorageReference storageReference = mStorageRef.child("images/image_"+System.currentTimeMillis());

        storageReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        if(mName!=null)
                            sendMessage(new Chat(mName,"",mUUID,downloadUrl.toString()));
                        else
                            sendMessage(new Chat("anonymous","",mUUID,downloadUrl.toString()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        ChatApplication.setIsOpen(true);
    }
}
