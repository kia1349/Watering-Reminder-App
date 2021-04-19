package com.grzeluu.wateringreminder;

import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int NEW_PLANT_REQUEST_CODE = 1111;
    private static final String AUTO_PREF = "5555";

    private ArrayList<Plant> plantsArrayList;

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

        plantsArrayList = new ArrayList<Plant>();
        initPlantsList();

        binding.addNewPlantFab.setOnClickListener(goToAddNewPlantActivity());

        initRecyclerView();
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
        editor.apply();
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
            }
        }
    }
}