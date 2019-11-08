package com.example.disc.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.disc.R;
import com.example.disc.Utility.FirebaseUtil;
import com.example.disc.Utility.GeneralUtility;
import com.example.disc.api.Apiservice;
import com.example.disc.models.ApiResponse;
import com.example.disc.models.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.disc.Utility.GeneralUtility.displaySnackBar;
import static com.example.disc.Utility.GeneralUtility.getFormattedString;
import static com.example.disc.Utility.GeneralUtility.validForm;
import static com.example.disc.R.string.*;

public class Login_Activity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private static final int REQUEST_CODE = 45;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 101;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 102;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public FirebaseAuth mAuth;
    private String TAG = "Login_Activity";
    private String userEmail;
    private String userPassword;
    private EditText mUserEmailField;
    private EditText mUserPasswordField;
    private Button mSignIn;
    private Button mCreateAccount;
    private ImageView imageView;
    private SignInButton googleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView mTextViewForgotPassword;
    private EditText firstNameField, lastNameField, passwordField, confirmPasswordField,stateOfOriginField,
            phoneNumberField, emailAddressField;
    private String mFullName, mDateOfBirth, mEmailAddress,mPhoneNumber,mPassword,mConfirmedPassword,mStateOfOrigin;
    private Uri pictureUri;
    public ProgressDialog progressDialog;
    public static final  String BASE_URL = "https://api.cloudinary.com/v1_1/mike12/";
    private String FilePathStr;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        if(requestCode == GeneralUtility.REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            FilePathStr = null;
            pictureUri = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(pictureUri, filePath,
                    null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            FilePathStr = c.getString(columnIndex);
            c.close();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);

        //>>>>>>>>>>>>>> Variable Initialization >>>>>>>>>>>>
        mAuth = FirebaseAuth.getInstance();
        mUserEmailField = findViewById(R.id.fieldEmail);
        mUserPasswordField = findViewById(R.id.fieldPassword);
        mSignIn = findViewById(R.id.emailSignInButton);
        googleSignInButton = findViewById(R.id.sign_in_button);
        mCreateAccount = findViewById(R.id.emailCreateAccountButton);
        mTextViewForgotPassword =  findViewById(R.id.forgot_password);
        //>>>>>>>>>>>>>> Variable Initialization >>>>>>>>>>>>

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.register,null);
        imageView = view.findViewById(R.id.imageView);
        alert.setView(view);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralUtility.chooseImage(Login_Activity.this);
            }
        });

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                progressDialog.setTitle("Signing Up...");
                progressDialog.setMessage("Creating Account...");
                progressDialog.show();
                alert.setView(view);
                createUser(view, alert);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        firstNameField = view.findViewById(R.id.createFullName);
        lastNameField = view.findViewById(R.id.txtDate);
        emailAddressField = view.findViewById(R.id.txtCreateEmail);
        phoneNumberField = view.findViewById(R.id.txtCreatePhoneNumber);
        passwordField = view.findViewById(R.id.txtCreatePassword);
        confirmPasswordField = view.findViewById(R.id.txtConfirmPassword);
        stateOfOriginField = view.findViewById(R.id.state_of_origin);


        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtility.clearView(view);
                alert.show();
                alert.setCancelable(true);
            }
        });

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check that the form is not blank and that the email is a valid one.
                String userEmail = mUserEmailField.getText().toString().trim();
                if (validForm(mUserEmailField,mUserPasswordField) && userEmail.matches(emailPattern)){
                    userPassword = mUserPasswordField.getText().toString().trim();
                    Log.d(TAG, "************" + Login_Activity.this.userEmail + " " + userPassword);
                    //remember to pass in a views context because this method is called in a non activity.
                    FirebaseUtil.signIn(userEmail,userPassword,v.getContext());
                }
                else if  (!userEmail.matches(emailPattern)){
                    displaySnackBar("Please Enter a Valid Email Address",v);
                }
            }
        });

        mTextViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtil.resetEmailPassword(mUserEmailField.getText().toString(),v);
                mTextViewForgotPassword.setTextColor(getResources().getColor(R.color.blueColorForIcons));
            }
        });

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();

            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void createUser(View view, AlertDialog.Builder alert) {
        if (validForm(firstNameField, lastNameField, emailAddressField, phoneNumberField, passwordField, confirmPasswordField) && passwordsMatch()){
            extractInputField();
            if(getUser().getmId() == null && pictureUri!= null) {
                FirebaseUtil.createAccount(mEmailAddress, mConfirmedPassword, Login_Activity.this,Login_Activity.this);
                uploadImage(FilePathStr);
            }
            else {
                Toast.makeText(Login_Activity.this, "Not saved", Toast.LENGTH_SHORT).show();
            }
        }
        else if (!passwordsMatch()){
            GeneralUtility.clearView(view);
            alert.show();
            passwordField.setError("Passwords do not Match");
            confirmPasswordField.setError("Passwords do not Match");
            Toast.makeText(Login_Activity.this, "Passwords Do not Match", Toast.LENGTH_SHORT).show();
        }
        else {
            GeneralUtility.clearView(view);
            alert.show();
            Toast.makeText(Login_Activity.this, empty_fields, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadImage(FilePathStr);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private boolean passwordsMatch() {
        return getFormattedString(passwordField,false).equals(getFormattedString(confirmPasswordField,false));
    }
    private void extractInputField() {
        mFullName = getFormattedString(firstNameField, true);
        mDateOfBirth = getFormattedString(lastNameField,true);
        mEmailAddress = getFormattedString(emailAddressField,false);
        mPhoneNumber = getFormattedString(phoneNumberField,false);
        mPassword = getFormattedString(passwordField,false);
        mConfirmedPassword = getFormattedString(confirmPasswordField,false);
        mStateOfOrigin = getFormattedString(stateOfOriginField,true);

        Log.d(TAG, "onClick: "+
                mFullName + " " +
                mDateOfBirth + " " +
                mEmailAddress + " " +
                mPassword + " " +
                mPassword + " " +
                mConfirmedPassword + " "+
                mStateOfOrigin);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    private void signInLastUser() {
        FirebaseUser emailAccount = mAuth.getCurrentUser();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        if ((emailAccount == null) &&  (googleAccount != null)){
            updateUIGoogle(googleAccount);
        }
        else if ((googleAccount ==  null) &&  (emailAccount != null)){
            updateUI(emailAccount);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        signInLastUser();
    }
    private void updateUI(FirebaseUser user) {

        if (user != null) {
            Intent intent = new Intent(this,UserActivity.class);
            startActivity(intent);
        }

        else {
            displaySnackBar("Please SignIn or Create An Account",mSignIn);
        }

        //Toast.makeText(this, "Your details are not correct", Toast.LENGTH_SHORT).show();
    }
    private void updateUIGoogle(GoogleSignInAccount user) {
        if (user != null) {
            Intent intent = new Intent(this,UserActivity.class);
            startActivity(intent);
        }

        else {
            displaySnackBar("Please SignIn or Create An Account",mSignIn);
        }

        //Toast.makeText(this, "Your details are not correct", Toast.LENGTH_SHORT).show();
    }
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            assert account != null;
            account.getId();

            // Signed in successfully, show authenticated UI.
            updateUIGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUIGoogle(null);
        }
    }
    public Users getUser() {
        Users newUsers = new Users();
        newUsers.setmFullName(mFullName);
        newUsers.setmDOB(mDateOfBirth);
        newUsers.setmPhoneNumber(mPhoneNumber);
        newUsers.setmEmail(mEmailAddress);
        newUsers.setmStateOfOrigin(mStateOfOrigin);
        newUsers.setmPictureUri(pictureUri.toString());
        return newUsers;

    }
    private  void uploadImage(String filePathStr){
        checkAndRequestPermissionsThenUpload(filePathStr);

    }
    private void checkAndRequestPermissionsThenUpload(String filePathStr) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)  {

            // Permission is not granted
            //request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

        }else{
            RequestBody file = null;
            if (FilePathStr != null){
                File imageFile = new File(FilePathStr);
                //query parameters..
                file = RequestBody.create(MediaType.parse("image/*"),imageFile);
            }
            RequestBody upload_preset = RequestBody.create(MultipartBody.FORM,"bvzdbmpr");
            assert file != null;
            MultipartBody.Part multiBodyFile =  MultipartBody.Part.createFormData("file","photo",file);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build());
            Retrofit retrofit = builder.build();

            Apiservice apiservice = retrofit.create(Apiservice.class);

            Call<ApiResponse> call = apiservice.uploadImage(multiBodyFile,upload_preset);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                    switch (response.code()){
                        case  200:
                            Toast.makeText(Login_Activity.this, "Saved Image too", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "************** onResponse:  working **********");
                            Log.d(TAG, "onResponse: " + response.headers());
                            Log.d(TAG, "onResponse: " + response);
                            return;

                        case 400:
                            Log.d(TAG, "onResponse:  something went wrong");
                            Toast.makeText(Login_Activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                         default:


                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    t.printStackTrace();
                    Log.d(TAG, "*****************onFailure: " + call.isExecuted() + t.getMessage());
                    Log.d(TAG, "*********** response: "+ call.request().toString());
                }
            });

        }

    }

}
