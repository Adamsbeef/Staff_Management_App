package com.example.disc.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.disc.R;
import com.example.disc.Utility.FirebaseUtil;
import com.example.disc.Utility.GeneralUtility;
import com.example.disc.models.Users;

import java.io.IOException;

import static com.example.disc.Utility.GeneralUtility.getFormattedString;
import static com.example.disc.Utility.GeneralUtility.validForm;

public class AddUser extends AppCompatActivity {

    private static final String TAG = "Add User Activity";
    private EditText fullNameField, dateField,stateOfOriginField,
            phoneNumberField, emailAddressField;
    private String mFullName, mDateOfBirth, mEmailAddress,mPhoneNumber,mStateOfOrigin;
    private Uri pictureUri;
    private ImageView imageView;
    private  Button btnCreateUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        fullNameField = findViewById(R.id.createFullName);
        dateField = findViewById(R.id.txtDate);
        emailAddressField = findViewById(R.id.txtCreateEmail);
        phoneNumberField = findViewById(R.id.txtCreatePhoneNumber);
        stateOfOriginField = findViewById(R.id.state_of_origin);
        btnCreateUser = findViewById(R.id.btnsave);
        imageView = findViewById(R.id.imageView);
        //TODO Assign button
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralUtility.chooseImage(AddUser.this);
            }
        });


        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validForm(fullNameField, dateField, emailAddressField, phoneNumberField)){
                    extractInputField();

                    if(getUser(mFullName,mDateOfBirth,mPhoneNumber,mEmailAddress,mStateOfOrigin).getmId() == null && pictureUri!= null) {
                        //uploadImage(FilePathStr);
                        FirebaseUtil.overloadWriteUserToDb(extractInputField(),AddUser.this);
                        Intent back = new Intent(AddUser.this,UserActivity.class);
                        startActivity(back);
                    }
                    else {
                        Toast.makeText(AddUser.this, "Not saved", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GeneralUtility.REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {

            pictureUri = data.getData();
            try {
                setPicture();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }
    private void setPicture() throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pictureUri);
        imageView.setImageBitmap(bitmap);
    }
    private Users extractInputField() {
        Users user = new Users();
        user.setmFullName(getFormattedString(fullNameField, true));
        user.setmEmail(getFormattedString(emailAddressField,false));
        user.setmPhoneNumber(getFormattedString(phoneNumberField,false));
        user.setmStateOfOrigin(getFormattedString(stateOfOriginField,true));
        user.setmDOB(getFormattedString(dateField,true));
        user.setmPictureUri(pictureUri.toString());
        Log.d(TAG, user.toString());
        return user;
    }
    public Users getUser( String fullName, String dob, String phone,String email,String state) {
        Users newUsers = new Users();
        newUsers.setmFullName(mFullName);
        newUsers.setmDOB(mDateOfBirth);
        newUsers.setmPhoneNumber(mPhoneNumber);
        newUsers.setmEmail(mEmailAddress);
        newUsers.setmStateOfOrigin(mStateOfOrigin);
        //newUsers.setmPictureUri(pictureUri.toString());
        return newUsers;

    }



}
