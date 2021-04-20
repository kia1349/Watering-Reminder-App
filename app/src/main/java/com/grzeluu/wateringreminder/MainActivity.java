package com.grzeluu.wateringreminder;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.grzeluu.wateringreminder.databinding.ActivityMainBinding;
import com.grzeluu.wateringreminder.model.Plant;
import com.grzeluu.wateringreminder.model.PlantsAdapter;
import com.grzeluu.wateringreminder.model.ReminderBroadcast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final int NEW_PLANT_REQUEST_CODE = 1111;
    private static final String AUTO_PREF = "5555";
    private static final String DAYS_PREF = "4444";

    private ArrayList<Plant> plantsArrayList;
    public ArrayList<Integer> daysToNotifyArrayList;

    private RecyclerView.Adapter plantsAdapter;
    private RecyclerView.LayoutManager plantsLayoutManager;

    ActivityMainBinding binding;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        createNotificationChanel();

        plantsArrayList = new ArrayList<Plant>();
        daysToNotifyArrayList = new ArrayList<Integer>();

        initPlantsList();
        initDaysToNotifyList();

        binding.addNewPlantFab.setOnClickListener(goToAddNewPlantActivity());

        initRecyclerView();
    }

    private void initDaysToNotifyList() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String string = sharedPreferences.getString(DAYS_PREF, null);
        Gson gson = new Gson();

        daysToNotifyArrayList = gson.fromJson(string, new TypeToken<ArrayList<Integer>>() {

        }.getType());
    }

    private void initPlantsList() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String string = sharedPreferences.getString(AUTO_PREF, null);
        Gson gson = new Gson();

        plantsArrayList = gson.fromJson(string, new TypeToken<ArrayList<Plant>>() {

        }.getType());
    }

    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        editor.putString(AUTO_PREF, gson.toJson(plantsArrayList));
        editor.putString(DAYS_PREF, gson.toJson(daysToNotifyArrayList));
        editor.apply()
        ;
    }


    private void initRecyclerView() {
        if (plantsArrayList != null) {
            plantsLayoutManager = new LinearLayoutManager(this);
            binding.plants.setLayoutManager(plantsLayoutManager);
            binding.plants.setHasFixedSize(true);

            plantsAdapter = new PlantsAdapter(this, plantsArrayList);
            binding.plants.setAdapter(plantsAdapter);
        } else {
            plantsArrayList = new ArrayList<Plant>();
        }
    }

    private View.OnClickListener goToAddNewPlantActivity() {
        return v -> {
            intent = new Intent(MainActivity.this, AddNewPlantActivity.class);
            startActivityForResult(intent, NEW_PLANT_REQUEST_CODE);
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_PLANT_REQUEST_CODE) {
            if (data != null) {
                Plant newPlant = (Plant) data.getExtras().get(AddNewPlantActivity.NEW_PLANT);
                plantsArrayList.add(newPlant);
                plantsAdapter.notifyDataSetChanged();

                Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                daysToNotifyArrayList.add(newPlant.getFrequencyOfWatering());
                daysToNotifyArrayList.add(newPlant.getFrequencyOfFertilizing());
                daysToNotifyArrayList.add(newPlant.getFrequencyOfRotating());
                daysToNotifyArrayList.add(newPlant.getFrequencyOfSpraying());

                daysToNotifyArrayList.removeAll(Arrays.asList(0));
                Collections.sort(daysToNotifyArrayList);

                long timeAtAddNewPlant = System.currentTimeMillis();
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

                long millis = daysToMillis(daysToNotifyArrayList.get(0));
                daysToNotifyArrayList.remove(0);

                alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtAddNewPlant + millis, pendingIntent);
            }
        }
    }

    private long daysToMillis(int daysCount) {
        return daysCount * 24 * 60 * 60 * 1000;
    }

    private void createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NotificationChannel";
            String description = "Channel for care reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyCare", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}