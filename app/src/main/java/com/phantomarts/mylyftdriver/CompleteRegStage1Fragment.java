package com.phantomarts.mylyftdriver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.phantomarts.mylyftdriver.Util.RequestHandler;

import org.json.JSONObject;

import java.net.SocketTimeoutException;

public class CompleteRegStage1Fragment extends Fragment implements View.OnClickListener {
    private EditText etPhoneno;
    private Button btnDone;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate=inflater.inflate(R.layout.fragment_compreg_stage1,container,false);

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(getContext());

        etPhoneno=inflate.findViewById(R.id.etPhoneno);
        btnDone=inflate.findViewById(R.id.btnDone);

        btnDone.setOnClickListener(this);
        etPhoneno.addTextChangedListener(new TextWatcher() {
            CharSequence beforeTextChanged;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextChanged=s.toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(s+"\n"+start+"\n"+before+"\n"+count);
                if(s.length()<=10){
                    if(s.length()!=0){
                        if(s.charAt(0)=='0'){
                            if(s.length()==1){
                                etPhoneno.setText("");
                                return;
                            }
                            etPhoneno.setText(s.subSequence(1,count));
                        }else if(s.length()>9){
                            etPhoneno.setText(beforeTextChanged);
                            return;
                        }
                    }
                }else{
                    etPhoneno.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return inflate;
    }

    @Override
    public void onClick(View v) {
        if(v==btnDone){
            //validate phone no
            if(!validatePhoneNo()){
                etPhoneno.setError("Invalid Phone No");
                etPhoneno.requestFocus();
                return;
            }

            progressDialog.setMessage("Validating Phone No");
            progressDialog.show();
            //saving phone no
            SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("uPhoneNo","+940"+etPhoneno.getText().toString());
            editor.commit();

            new RequestAsync().execute();
            return;
        }
    }

    private boolean validatePhoneNo() {
        String phoneNo=etPhoneno.getText().toString();
        try {
            Double.parseDouble(phoneNo);
            if(phoneNo.length()>9){
                return false;
            }
            return true;
        }catch(NumberFormatException ex){
            return false;
        }
    }

    public class RequestAsync extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                // POST Request
                JSONObject postDataParams = new JSONObject();
                System.out.println(firebaseAuth.getCurrentUser());
                postDataParams.put("uid", firebaseAuth.getCurrentUser().getUid());
                postDataParams.put("phoneno", "+940"+etPhoneno.getText().toString());
                postDataParams.put("reqtype", "0");

                String sudoServeraddress="http://192.168.1.101:8080";
                String localServeraddress="http://10.0.2.2:8080";
                return RequestHandler.sendPost(localServeraddress+"/LyftMeServer/VerifyPhone",postDataParams);
            }
            catch(SocketTimeoutException e){
                System.out.println("socket timed out check your con");
                return "-4";
            }
            catch(Exception e){
                e.printStackTrace();
                return new String("Exception: " + e.getMessage());

            }
        }

        @Override
        protected void onPostExecute(String s) {
            if(s!=null){
                if(s.equals("1")){
                    progressDialog.dismiss();
                    CompleteRegStage2Fragment stage2Fragment=new CompleteRegStage2Fragment();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.parentLayoutCompReg, stage2Fragment,"compreg_stage2")
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .addToBackStack(null)
                            .commit();
                }else if(s.equals("-4")){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Connection to Server Failed. Check your connection!", Toast.LENGTH_LONG).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Validation Failed", Toast.LENGTH_LONG).show();
                }

            }

        }


    }

}
