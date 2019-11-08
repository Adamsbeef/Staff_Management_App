package com.example.disc.Utility;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.disc.activities.Login_Activity;
import com.example.disc.activities.UserActivity;
import com.example.disc.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class FirebaseUtil {
    private static FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private static String TAG  = "FirebaseUtil";
    public static FirebaseAuth mAuth  = FirebaseAuth.getInstance();
    static boolean sentEmail = false;
    static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public FirebaseUtil(){}
    public static  void createAccount(String email, final String password, final Context context, final Login_Activity activity){
        try {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(context, "Created Account Successfully", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user,context);
                                writeTheUserToDb(activity.getUser(),activity);
                                activity.progressDialog.setMessage("Now Writing Details to Database...");
                                verifyUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(context, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null,context);
                            }

                            // ...
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(context, "This Email Prolly Already Exists", Toast.LENGTH_SHORT).show();
            activity.progressDialog.cancel();
        }


    }
    public static void signIn(final String email, final String password, final Context context){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "**********" + email + "  " + password + "**********");
                            Toast.makeText(context, "WTF " + password, Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, "Authentication Successful.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user,context);
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null,context);
                        }

                        // ...
                    }
                });
    }
    public static void updateUI(FirebaseUser user, Context context) {

        if (user != null) {
            Intent intent = new Intent(context, UserActivity.class);
            context.startActivity(intent);
        }

        else {
            //TODO: do some work here.
        }
    }
    public static void resetEmailPassword(final String emailAddress, final View view){
        if (emailAddress == null || emailAddress.isEmpty()){
            GeneralUtility.displaySnackBar("Please Enter Your Email Address"+ emailAddress,view);
        }
        else {
            mAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                //Toast.makeText(context, "Sent a reset email to " + emailAddress, Toast.LENGTH_SHORT).show();
                                GeneralUtility.displaySnackBar("Sent An Email to "+ emailAddress,view);
                                sentEmail = true;
                            }
                            else{
                                //String reasonForFailure = Objects.requireNonNull(task.getException()).toString();
                                Log.d(TAG, "Reason for failure " + task.getException().toString());
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthInvalidUserException e) {
                                    GeneralUtility.displaySnackBar("You're not an existing user",view);
                                } catch(FirebaseException e) {
                                GeneralUtility.displaySnackBar("Please  Make sure you have an active internet connection",view);}
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }
    }
    public static void verifyUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }
    public static void writeTheUserToDb(Users newUsers, final Login_Activity activity) {
        mDatabase.child("user_details").push().setValue(newUsers).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(activity, "Created a profile for you", Toast.LENGTH_SHORT).show();
                activity.progressDialog.cancel();
            }
        });
    }

}
