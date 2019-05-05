package com.example.rum8.controllers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.rum8.listeners.RegistrationControllerListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RegistrationController {

    private RegistrationControllerListener controllerListener;
    private Context context;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    // Access a Cloud Firestore instance from your Activity
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public RegistrationController(final RegistrationControllerListener controllerListener, final Context context) {

        this.controllerListener = controllerListener;
        this.context = context;

        // Listener to check the status of registration
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(final @NonNull FirebaseAuth firebaseAuth) {

                // Get the current user
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }
        };

        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(authStateListener);
    }

    public void onSubmit(final String email, final String password) {
        if (!isValidEmail(email)) {
            final String message = "Please use your UCSD email (i.e. abc@ucsd.edu)";
            controllerListener.showToast(message, Toast.LENGTH_SHORT);
        } else if (!isValidPassword(password)) {
            final String message = "Your password need to be more than 6 characters";
            controllerListener.showToast(message, Toast.LENGTH_SHORT);
        } else {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                    ((Activity) context, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(final @NonNull Task<AuthResult> task) {
                            final String message;
                            if (task.isSuccessful()) {
                                emailVerify(email);
                                controllerListener.onUserRegistered();
                                Log.d("Success", "createUserWithEmail:success");
                                // Create a new user with email when registration is complete

                                // set email feild with user email
                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("email", email);

                                // add doc to firestore
                                db.collection("users")
                                        // use user id as document reference
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .set(userInfo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });

                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    message = "An account with this email already exists";
                                } else {
                                    message = "Authentication failed";
                                }
                                controllerListener.showToast(message, Toast.LENGTH_SHORT);
                                Log.e("Error:", "createUserWithEmail:failure", task.getException());
                            }
                        }
                    });
        }
    }

    /*
     * Helper function
     */
    public void emailVerify(final String email) {

        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        final String message;
                        if (task.isSuccessful()) {
                            message = "Verification email sent to " + email;
                            controllerListener.showToast(message, Toast.LENGTH_SHORT);
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            message = "Failed to send verification email to";
                            controllerListener.showToast(message, Toast.LENGTH_SHORT);
                        }
                    }
                });
    }

    private static boolean isValidEmail(final String email) {
        if (email == null) {
            return false;
        }

        final int minimumEmailLength = 10;
        return email.length() >= minimumEmailLength && email.endsWith("@ucsd.edu");
    }

    private static boolean isValidPassword(final String password) {
        final int minimumPasswordLength = 6;
        return password != null && password.length() >= minimumPasswordLength;
    }

    public void destroy() {
        auth.removeAuthStateListener(authStateListener);
    }


    public void onGoBackToLoginButtonClicked() {
        controllerListener.goBackToLogin();
    }

}
