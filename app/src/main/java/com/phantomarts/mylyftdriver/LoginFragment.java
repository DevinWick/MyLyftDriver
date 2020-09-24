package com.phantomarts.mylyftdriver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phantomarts.mylyftdriver.Util.Util;
import com.phantomarts.mylyftdriver.model.Driver;

import java.util.HashSet;
import java.util.Set;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private EditText etusername, etpassword;
    private TextView noAcctxt;
    private Button loginBtn;
    private SharedPreferences mPref;
    private Set<View> persistFieldSet = new HashSet<>();

    private ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference rootRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_login, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootRef = firebaseDatabase.getReference();

        progressDialog = new ProgressDialog(getActivity());

        etusername = inflate.findViewById(R.id.editText);
        etpassword = inflate.findViewById(R.id.editText2);
        noAcctxt = inflate.findViewById(R.id.textViewNoAcc3);
        loginBtn = inflate.findViewById(R.id.button);
        //get shared preference for activity
        mPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        //set Fields and Persist Fields
        setPersistFields(inflate);

        //set listeners for components
        setListeners(inflate);

        return inflate;


    }

    private void setPersistFields(View inflate) {
        persistFieldSet.add(etusername);
    }

    private void setListeners(View view) {
        loginBtn.setOnClickListener(this);
        noAcctxt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == loginBtn) {
            String loginStr = etusername.getText().toString();
            String passwordStr = etpassword.getText().toString();

            //validate
            if (TextUtils.isEmpty(loginStr)) {
                Toast.makeText(getActivity(), "please enter your email", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(passwordStr)) {
                Toast.makeText(getActivity(), "please enter your password", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.setMessage("Loggin in...");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(loginStr, passwordStr)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //checks registration completion stage
                                isCustomer();
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_SHORT).show();
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                handleError(errorCode);
                            }
                            progressDialog.dismiss();
                        }

                        private void isCustomer() {
                            ValueEventListener vel=new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Driver user = dataSnapshot.getValue(Driver.class);
                                    if(user!=null){
                                        checkAccountStatusAndNavigate();
                                    }else{
                                        firebaseAuth.signOut();
                                        Toast.makeText(getContext(), "invalid account check username or password", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            };

                            firebaseDatabase.getReference("/users/drivers/"+firebaseAuth.getCurrentUser().getUid())
                                    .addListenerForSingleValueEvent(vel);

                        }
                    });

        } else if (v == noAcctxt) {
            System.out.println("onclick");
            RegisterFragment registerFragment = new RegisterFragment();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.loginAct_fragparent, registerFragment)
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .addToBackStack(null)
                    .commit();
            return;
        }
    }

    private void checkAccountStatusAndNavigate() {
        ValueEventListener userValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Driver driver = dataSnapshot.getValue(Driver.class);
                int regCompStage = driver.getRegCompleteStaus();
                System.out.println("regcompstage "+regCompStage);
                Intent intent=new Intent(getActivity(),CompleteRegActivity.class);
                switch (regCompStage) {
                    case Driver.ACCOUNT_COMPLETE_ALL:
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        break;
                    case Driver.ACCOUNT_COMPLETE_STAGE1:
                        intent.putExtra("regCompStatus",Driver.ACCOUNT_COMPLETE_STAGE1);
                        startActivity(intent);
                        break;
                    case Driver.ACCOUNT_COMPLETE_STAGE3:
                        intent.putExtra("regCompStatus",Driver.ACCOUNT_COMPLETE_STAGE3);
                        startActivity(intent);
                        break;
                    case Driver.ACCOUNT_COMPLETE_STAGE4:
                        intent.putExtra("regCompStatus",Driver.ACCOUNT_COMPLETE_STAGE4);
                        startActivity(intent);
                        break;
                    case Driver.ACCOUNT_COMPLETE_STAGE5:
                        intent.putExtra("regCompStatus",Driver.ACCOUNT_COMPLETE_STAGE5);
                        startActivity(intent);
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        DatabaseReference userRef = firebaseDatabase.getReference("users").child("drivers").child(firebaseAuth.getCurrentUser().getUid());
        userRef.addListenerForSingleValueEvent(userValueListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        //restore state of fields
        Util.restoreStatesOfFields(mPref, persistFieldSet);
    }

    @Override
    public void onPause() {
        super.onPause();

        //saving state of fields
        Util.saveStatesOfFields(mPref, persistFieldSet);
    }

    private void handleError(String errorCode) {
        switch (errorCode) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(getActivity(), "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(getActivity(), "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(getActivity(), "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(getActivity(), "The email address is badly formatted.", Toast.LENGTH_LONG).show();
                etusername.setError("Invalid Email Address");
                etusername.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(getActivity(), "The password is invalid or the user does not have a password.", Toast.LENGTH_LONG).show();
                etpassword.setError("password is incorrect ");
                etpassword.requestFocus();
                etpassword.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(getActivity(), "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(getActivity(), "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(getActivity(), "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(getActivity(), "The email address is already in use by another account.   ", Toast.LENGTH_LONG).show();
                // email.setError("User Account exists with same email");
                // email.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(getActivity(), "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(getActivity(), "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(getActivity(), "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(getActivity(), "There is no user record corresponding to this identifier. The user may have been deleted.", Toast.LENGTH_LONG).show();
                etusername.setError("No user account under given Username");
                etusername.requestFocus();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(getActivity(), "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(getActivity(), "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(getActivity(), "The given password is invalid.", Toast.LENGTH_LONG).show();
                // password.setError("Enter more than 6 characters");
                // password.requestFocus();
                break;

        }
    }
}
