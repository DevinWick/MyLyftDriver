package com.phantomarts.mylyftdriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.phantomarts.mylyftdriver.model.Driver;

public class RegisterFragment extends Fragment implements View.OnClickListener{
    final private static String TAG="REGISTER_AUTH_EXCEPTION";

    private EditText email,password;
    private Button regBtn;
    private TextView haveAccTxt,errorMsgTxt;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference rootRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_register, container, false);

        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        rootRef=firebaseDatabase.getReference();

        progressDialog=new ProgressDialog(getActivity());

        email=inflate.findViewById(R.id.editText);
        password=inflate.findViewById(R.id.editText2);
        regBtn=inflate.findViewById(R.id.button);
        haveAccTxt=inflate.findViewById(R.id.textViewHaveAcc);
        errorMsgTxt=inflate.findViewById(R.id.tvErrorMsg);

        //setup listeners
        setListeners(inflate);

        return inflate;
    }

    private void setListeners(View inflate) {
        regBtn.setOnClickListener(this);
        haveAccTxt.setOnClickListener(this);
    }

    private void registerUser(){
        final String emailStr=email.getText().toString().trim();
        final String passwordStr=password.getText().toString().trim();

        if(TextUtils.isEmpty(emailStr)){
            Toast.makeText(getActivity(), "please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(passwordStr)){
            Toast.makeText(getActivity(), "please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        //showing progress dialog
        progressDialog.setMessage("Registering User ...");
        progressDialog.show();

        //creating user account and handling result
        firebaseAuth.createUserWithEmailAndPassword(emailStr,passwordStr)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Driver driver=new Driver();
                            driver.setEmail(emailStr);
                            driver.setPassword(passwordStr);
                            driver.setRegCompleteStaus(Driver.ACCOUNT_COMPLETE_STAGE1);
                            rootRef.child("users").child("drivers").child(firebaseAuth.getCurrentUser().getUid()).setValue(driver);
                            getActivity().finish();
                            Intent intent=new Intent(getActivity(),CompleteRegActivity.class);
                            intent.putExtra("regCompStatus",Driver.ACCOUNT_COMPLETE_STAGE1);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getActivity(), "registration failed please try again!", Toast.LENGTH_SHORT).show();
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            handleError(errorCode);

                        }
                        progressDialog.dismiss();
                    }
                });

    }
    @Override
    public void onClick(View v) {
        if(v==regBtn){

            registerUser();

        }else if(v==haveAccTxt){
            //remove current fragment and go back
            getActivity().getSupportFragmentManager().popBackStackImmediate();
            return;
        }
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
                email.setError("Invalid Email Address");
                email.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(getActivity(), "The password is invalid or the user does not have a password.", Toast.LENGTH_LONG).show();
                //etPassword.setError("password is incorrect ");
                //etPassword.requestFocus();
                //etPassword.setText("");
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
                email.setError("User Account exists with same email");
                email.requestFocus();
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
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(getActivity(), "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(getActivity(), "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                Toast.makeText(getActivity(), "The given password is invalid.", Toast.LENGTH_LONG).show();
                password.setError("Enter more than 6 characters");
                password.requestFocus();
                break;

        }
    }

}
