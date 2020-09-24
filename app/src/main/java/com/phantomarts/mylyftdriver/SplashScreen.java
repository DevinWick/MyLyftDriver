package com.phantomarts.mylyftdriver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.phantomarts.mylyftdriver.model.Driver;

public class SplashScreen extends AppCompatActivity {


    private Handler mHandler;
    private Runnable mRunnable;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        mRunnable=new Runnable() {
            @Override
            public void run() {
                //check for user detials
                if(firebaseAuth.getCurrentUser()==null){
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    return;
                }else{
                    //check for registration complete status
                    ValueEventListener veListener=new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           int regCompstatus= dataSnapshot.getValue(int.class);
                           if(regCompstatus== Driver.ACCOUNT_COMPLETE_ALL){
                               startActivity(new Intent(getApplicationContext(), MainActivity.class));
                           }else{
                               Intent intent=new Intent(getApplicationContext(),CompleteRegActivity.class);
                               intent.putExtra("regCompStatus",regCompstatus);
                               startActivity(intent);
                           }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    firebaseDatabase.getReference("/users/drivers/"+firebaseAuth.getCurrentUser().getUid()+"/regCompleteStaus")
                    .addListenerForSingleValueEvent(veListener);

                }

            }
        };

        mHandler=new Handler();
        mHandler.postDelayed(mRunnable,500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHandler!=null&&mRunnable!=null){
            mHandler.removeCallbacks(mRunnable);
        }
    }
}
