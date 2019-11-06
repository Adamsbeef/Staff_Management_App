package com.example.disc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.disc.FirebaseUtil.mDatabase;
import static com.example.disc.GeneralUtility.displaySnackBar;
import static com.example.disc.GeneralUtility.getFormattedString;
import static com.example.disc.GeneralUtility.validForm;
import static com.example.disc.R.string.*;

public class Login_Activity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private static final int REQUEST_CODE = 45;
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
    private String mFirstName, mLastName, mEmailAddress,mPhoneNumber,mPassword,mConfirmedPassword,mStateOfOrigin;
    private Uri pictureUri;
    public ProgressDialog progressDialog;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            pictureUri = data.getData();
            try {
                imageView.setImageURI(pictureUri);
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
                chooseImage();
            }
        });

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                progressDialog.setTitle("Signing Up...");
                progressDialog.setMessage("Creating Account...");
                progressDialog.show();
                alert.setView(view);
                if (validForm(firstNameField, lastNameField, emailAddressField, phoneNumberField, passwordField, confirmPasswordField) && passwordsMatch()){
                    extractInputField();
                    //writeNewUserToDb();
                    if(getUser().getmId() == null) {
                        FirebaseUtil.createAccount(mEmailAddress, mConfirmedPassword,Login_Activity.this,Login_Activity.this);
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
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        firstNameField = view.findViewById(R.id.createFirstName);
        lastNameField = view.findViewById(R.id.createLastName);
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
    private boolean passwordsMatch() {
        return getFormattedString(passwordField,false).equals(getFormattedString(confirmPasswordField,false));
    }
    private void extractInputField() {
        mFirstName = getFormattedString(firstNameField, true);
        mLastName = getFormattedString(lastNameField,true);
        mEmailAddress = getFormattedString(emailAddressField,false);
        mPhoneNumber = getFormattedString(phoneNumberField,false);
        mPassword = getFormattedString(passwordField,false);
        mConfirmedPassword = getFormattedString(confirmPasswordField,false);
        mStateOfOrigin = getFormattedString(stateOfOriginField,true);

        Log.d(TAG, "onClick: "+
                mFirstName + " " +
                mLastName + " " +
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
        newUsers.setmFirstName(mFirstName);
        newUsers.setmLastName(mLastName);
        newUsers.setmPhoneNumber(mPhoneNumber);
        newUsers.setmEmail(mEmailAddress);
        newUsers.setmStateOfOrigin(mStateOfOrigin);
        return newUsers;

    }

    public void writeTheUserToDb(Users newUsers) {
        mDatabase.child(getString(R.string.user_details)).push().setValue(newUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Login_Activity.this, "Created a profile for you", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
            }
        });
    }
    private void chooseImage() {
        Intent getImages  = new Intent(Intent.ACTION_GET_CONTENT);
        getImages.setType("image/*");
        getImages.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(Intent.createChooser(getImages,"Insert A Picture"), REQUEST_CODE);
    }


}
