package com.example.disc.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.disc.R;
import com.example.disc.Utility.GeneralUtility;
import com.example.disc.models.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class SpecificUser extends AppCompatActivity {
    private static final String TAG = "SpecificUser Activity";
    private TextView txtFirstName,txtPhone,txtEmail,txtOrigin, txtDOB;
    private String mFullName,mPhone,mEmail,mOrigin, mDob;
    private ImageView imageView;
    private Users user;
    public static DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private Intent  usersActivity;
    private Uri mPicturUri;
    private Uri pictureUri;

    @Override
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_user);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("user_details");

        Intent intent = getIntent();

        txtFirstName = findViewById(R.id.txt_name);
        txtDOB = findViewById(R.id.txt_date_of_birth);
        txtPhone = findViewById(R.id.txt_phone);
        txtEmail = findViewById(R.id.txt_email);
        txtOrigin = findViewById(R.id.txt_state);
        imageView = findViewById(R.id.imageView);

        user = (Users) intent.getSerializableExtra("users");
        mFullName = user.getmFullName();
        mDob = user.getmDOB();
        mPhone = user.getmPhoneNumber();
        mEmail = user.getmEmail();
        mOrigin = user.getmStateOfOrigin();
        mPicturUri = Uri.parse(user.getmPictureUri());

        txtFirstName.setText(mFullName);
        txtDOB.setText(user.getmDOB());
        txtPhone.setText(mPhone);
        txtOrigin.setText(mOrigin);
        txtEmail.setText(mEmail);
        if (mPicturUri != null){
            imageView.setImageURI(mPicturUri);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralUtility.chooseImage(SpecificUser.this);
            }
        });

        usersActivity = new Intent(getApplicationContext(),UserActivity.class);
    }
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.delete_menu_item:
                deleteUser();
                return true;
            case R.id.save_new_value:
                updateRecord();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void deleteUser() {
        if ( user != null) {
            //Log.d(TAG, "************** deleted Guess : "+ mGuesses.getId() + "**************");
            mDatabaseReference.child(user.getmId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    startActivity(usersActivity);
                    Log.d(TAG, "________________________Deleted Successfully +++++++++++++++++");
                    Toast.makeText(SpecificUser.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "_______----------- Here is why it failed: " + e.toString());
                }
            });
            //TODO: Move All the  database methods and variables  to the database util class
        }
    }
    public void updateRecord(){
        Users oldUser = user;
        
        user.setmFullName(txtFirstName.getText().toString());
        user.setmDOB(txtDOB.getText().toString());
        user.setmEmail(txtEmail.getText().toString());
        user.setmPhoneNumber(txtPhone.getText().toString());
        user.setmStateOfOrigin(txtOrigin.getText().toString());
        user.setmPictureUri(pictureUri.toString());

//        if (oldUser == user){
//            Toast.makeText(this, "**************True*************", Toast.LENGTH_SHORT).show();
//        }
        Log.d(TAG, "********** updateRecord: " + user.getmId());
        mDatabaseReference.child(user.getmId()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SpecificUser.this, "Updated This Stuff", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SpecificUser.this,UserActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SpecificUser.this, "could not push updates", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "************ onFailure: "+ e.toString());
        }
    });
    }

    private void setPicture() throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pictureUri);
        imageView.setImageBitmap(bitmap);
    }
}