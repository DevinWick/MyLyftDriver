package com.phantomarts.mylyftdriver;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.phantomarts.mylyftdriver.Util.Util;
import com.phantomarts.mylyftdriver.adapters.NotificationRecyclerViewAdapter;
import com.phantomarts.mylyftdriver.model.Notification;

import java.util.ArrayList;

public class NavigationNotificationActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_notification);
        mRecyclerView=findViewById(R.id.notification_recyclerview);

        initActionBar();

        initRecyclerView();

    }




    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.notification);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView() {
        Notification noti1=new Notification();
        noti1.setDate("18 Aug 2019");
        noti1.setSender("System");
        noti1.setTitle("PROMO CODE");
        noti1.setMsg("Enjoy LKR50 discount on 5 rides on 19 Aug 2019");

        Notification noti2=new Notification();
        noti2.setDate("06 Sep 2019");
        noti2.setSender("System");
        noti2.setTitle("Account Upgrade");
        noti2.setMsg("Congratulations!. Your Account Upgraded to BRONZE Level");

        ArrayList<Notification> notiList=new ArrayList<>();
        notiList.add(noti1);
        notiList.add(noti2);

        NotificationRecyclerViewAdapter adapter=new NotificationRecyclerViewAdapter(this,notiList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
