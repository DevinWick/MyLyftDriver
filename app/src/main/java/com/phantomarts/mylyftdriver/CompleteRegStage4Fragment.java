package com.phantomarts.mylyftdriver;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.phantomarts.mylyftdriver.model.Driver;
import com.phantomarts.mylyftdriver.model.Vehicle;
import com.shuhart.stepview.StepView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class CompleteRegStage4Fragment extends Fragment implements View.OnClickListener {

    private Spinner spinner;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private EditText etOwner,etRegNo,etBrand,etModel,etVehicleColor;
    private CheckBox cbOwner;
    private Button btnDone;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate=inflater.inflate(R.layout.fragment_compreg_stage4,container,false);

        StepView stepView=((CompleteRegActivity)getActivity()).stepView;
        stepView.go(1,true);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        progressDialog=new ProgressDialog(getContext());
        etOwner=inflate.findViewById(R.id.etowner);
        etRegNo=inflate.findViewById(R.id.etregno);
        etBrand=inflate.findViewById(R.id.etbrand);
        etModel=inflate.findViewById(R.id.etmodel);
        etVehicleColor=inflate.findViewById(R.id.etvehiclecolor);
        cbOwner=inflate.findViewById(R.id.checkBox);
        btnDone=inflate.findViewById(R.id.button7done);

        btnDone.setOnClickListener(this);

        //initialize spinner
        spinner=inflate.findViewById(R.id.spinner);
        ArrayAdapter<String> myadapter=new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.vehicle_types));
        myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myadapter);
        //

        return inflate;
    }



    @Override
    public void onClick(View v) {
        if(v==btnDone){
            btnDoneOnclick();
            return;
        }
    }

    private void btnDoneOnclick() {
        String owner=etOwner.getText().toString();
        String regno=etRegNo.getText().toString();
        String brand=etBrand.getText().toString();
        String model=etModel.getText().toString();
        String vehicleColor=etVehicleColor.getText().toString();
        String type=spinner.getSelectedItem().toString();
        boolean isOwner=cbOwner.isChecked();
        System.out.println("isowner: "+isOwner);

        //validate
        if(TextUtils.isEmpty(owner)){
            etOwner.setError("Empty Field");
            return;
        }else if(TextUtils.isEmpty(regno)){
            etRegNo.setError("Empty Field");
            return;
        }else if(TextUtils.isEmpty(brand)){
            etBrand.setError("Empty Field");
            return;
        }else if(TextUtils.isEmpty(model)){
            etModel.setError("Empty Field");
            return;
        }else if(TextUtils.isEmpty(vehicleColor)){
            etVehicleColor.setError("Empty Field");
            return;
        }

        progressDialog.setMessage("Uploading Vehicle Details ...");
        progressDialog.show();
        Vehicle v=new Vehicle(owner,regno,brand,model,vehicleColor,type,isOwner,firebaseAuth.getCurrentUser().getUid());
        //update firebase db
        Map<String,Object> childUpdates=new HashMap<>();
        childUpdates.put("vehicles/"+firebaseAuth.getCurrentUser().getUid(),v);
        childUpdates.put("/users/drivers/"+firebaseAuth.getCurrentUser().getUid()+"/regCompleteStaus",Driver.ACCOUNT_COMPLETE_STAGE5);
        firebaseDatabase.getReference()
                .updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        System.out.println("data update failed");
                        Toast.makeText(getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });

        //change fragment
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.parentLayoutCompReg, new CompleteRegStage5Fragment(),"compreg_stage5")
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack(null)
                .commit();
    }


}
