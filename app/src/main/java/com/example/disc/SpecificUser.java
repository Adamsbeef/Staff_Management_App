package com.example.disc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SpecificUser extends AppCompatActivity {
    private static final String TAG = "SpecificUser Activity";
    private TextView txtFullName,txtPhone,txtEmail,txtOrigin;
    private String mFullName,mPhone,mEmail,mOrigin;
    private Users user;
    public static DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private Intent  usersActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_user);
        Intent intent = getIntent();

        txtFullName = findViewById(R.id.txt_name);
        txtPhone = findViewById(R.id.txt_phone);
        txtEmail = findViewById(R.id.txt_email);
        txtOrigin = findViewById(R.id.txt_state);

        user = (Users) intent.getSerializableExtra("users");
        mFullName = user.getmFirstName() + " " + user.getmLastName();
        mPhone = user.getmPhoneNumber();
        mEmail = user.getmEmail();
        mOrigin = user.getmStateOfOrigin();

        txtFullName.setText(mFullName);
        txtPhone.setText(mPhone);
        txtOrigin.setText(mOrigin);
        txtEmail.setText(mEmail);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void deleteUser() {
        if ( user != null) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseReference = mFirebaseDatabase.getReference().child("user_details");
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
}