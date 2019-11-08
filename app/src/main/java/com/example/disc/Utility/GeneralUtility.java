package com.example.disc.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class GeneralUtility {
    public static final int REQUEST_CODE = 45;

    public static void displaySnackBar(String message, View view) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static String getFormattedString(TextView view, Boolean capitalize) {
        String formattedString;
        if (capitalize) {
            formattedString = capitalise(view.getText().toString().trim());
            Log.d("General Utility", "getFormattedString: " + formattedString);
            return formattedString;
        } else {
            formattedString = view.getText().toString().trim();
            Log.d("General Utility", "getFormattedString: " + formattedString);
            return formattedString;
        }
    }

    public static boolean validForm(TextView... views) {
        boolean valid = false;
        TextView holderTextView;
        for (TextView view : views) {
            holderTextView = view;
            String value = holderTextView.getText().toString();
            if (TextUtils.isEmpty(value)) {
                Log.d("GeneralUtility", "validForm: this stuff is empty");
                holderTextView.setError("Required.");
            } else {
                holderTextView.setError(null);
                valid = true;
            }
        }
        Log.d("Valid Form", "validForm: " + valid);
        return valid;
    }

    public static void clearView(View view) {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeAllViews();
        }
    }

    static String capitalise(String word) {
        String upperString = word.substring(0, 1).toUpperCase() + word.substring(1);
        return upperString;
    }

    public static void chooseImage(Activity activity) {
        Intent getImages  = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        getImages.setType("image/*");
//        getImages.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        activity.startActivityForResult(getImages, REQUEST_CODE);
    }

}

