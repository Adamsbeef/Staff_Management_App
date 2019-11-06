package com.example.disc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;



public class UserActivity extends AppCompatActivity {
    private static final String TAG = "UserActivity";
    private GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;
    final static ArrayList<Users> mArrayOfUsers = new ArrayList<>();
    private static Users users;
    private static FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private TextView txtName,txtLastName,txtPhone,txtEmail,txtOrigin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
//        txtName = findViewById(R.id.txt_first_name);
//        txtLastName = findViewById(R.id.txt_last_name);
//        txtEmail = findViewById(R.id.txt_email);
//        txtPhone = findViewById(R.id.txt_phone);
//        txtOrigin = findViewById(R.id.txt_origin);
        Log.d(TAG,"Started fine");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        initRecyclerView();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signout, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.sign_out_menu_item:
                FirebaseUtil.mAuth.signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UserActivity.this, "SignedOut Successfully", Toast.LENGTH_LONG).show();
                    }
                });
                Intent  mainActivityIntent = new Intent(this,Login_Activity.class);
                startActivity(mainActivityIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void initRecyclerView() {
        Log.d(TAG,"****************************  initRecyclerView Called********************************");
        RecyclerView recyclerView = findViewById(R.id.rvUsers);
        UsersAdapter adapter = new UsersAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        Log.d(TAG,"****************************  initRecyclerView ended ********************************");

    }
}