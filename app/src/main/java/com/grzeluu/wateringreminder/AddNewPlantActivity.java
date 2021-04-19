package com.grzeluu.wateringreminder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.grzeluu.wateringreminder.databinding.ActivityAddNewPlantBinding;
import com.grzeluu.wateringreminder.model.Plant;

public class AddNewPlantActivity extends AppCompatActivity {

    public static final String NEW_PLANT = "1111";

    private static final int CAMERA_REQUEST_CODE = 9999;

    ActivityAddNewPlantBinding binding;

    private Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewPlantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageView.setOnClickListener(takePhoto());

        binding.wateringSeekBar.setOnSeekBarChangeListener(createChangeListener(binding.wateringDaysTextView));
        binding.fertilizingSeekBar.setOnSeekBarChangeListener(createChangeListener(binding.fertilizingDaysTextView));
        binding.sprayingSeekBar.setOnSeekBarChangeListener(createChangeListener(binding.sprayingDaysTextView));
        binding.rotationSeekBar.setOnSeekBarChangeListener(createChangeListener(binding.rotatingTextView));

        binding.addButton.setOnClickListener(v -> {
            tryToAddNewPlant();
        });

        binding.cancelButton.setOnClickListener(v -> {
            finish();
        });
    }

    private View.OnClickListener takePhoto() {
        return v -> {
            if (ContextCompat.checkSelfPermission(AddNewPlantActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddNewPlantActivity.this, new String[]{
                        Manifest.permission.CAMERA
                }, CAMERA_REQUEST_CODE);
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        };
    }

    private SeekBar.OnSeekBarChangeListener createChangeListener(TextView tv) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress == 0) {
                        tv.setText(getString(R.string.never));
                    } else if (progress == 1) {
                        tv.setText(getString(R.string._1_day));
                    } else if (progress > 1 && progress <= 30) {
                        String daysString = String.valueOf(progress) + " ";
                        tv.setText(daysString + getString(R.string.days));
                    } else if (progress > 30 && progress <= 35) {
                        String daysString = String.valueOf(30 + (progress - 30) * 5) + " ";
                        tv.setText(daysString + getString(R.string.days));
                    } else {
                        String monthsString = String.valueOf(progress - 34) + " ";
                        tv.setText(monthsString + getString(R.string.months));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    private void tryToAddNewPlant() {
        String plantName = binding.plantNameEditText.getText().toString();
        String plantSpecies = binding.plantSpeciesEditText.getText().toString();

        boolean hasErrors = false;

        if (plantName.isEmpty()) {
            binding.plantNameEditText.setError(getString(R.string.field_cannot_be_empty));
            hasErrors = true;
        }
        if (plantSpecies.isEmpty()) {
            binding.plantSpeciesEditText.setError(getString(R.string.field_cannot_be_empty));
            hasErrors = true;
        }
        if (photo == null) {
            Toast.makeText(AddNewPlantActivity.this, "Take a photo of your plant!", Toast.LENGTH_LONG).show();
            hasErrors = true;
        }
            else if (!hasErrors) {
            addNewPlant();
        }
    }

    private void addNewPlant() {
        String name = binding.plantNameEditText.getText().toString();
        String species = binding.plantNameEditText.getText().toString();
        int frequencyOfWatering = binding.wateringSeekBar.getProgress();
        int frequencyOfFertilizing = binding.fertilizingSeekBar.getProgress();
        int frequencyOfSpraying = binding.sprayingSeekBar.getProgress();
        int frequencyOfRotating = binding.rotationSeekBar.getProgress();

        Plant plant = new Plant(photo, name, species, toDays(frequencyOfWatering), toDays(frequencyOfFertilizing), toDays(frequencyOfSpraying), toDays(frequencyOfRotating));

        Intent intent = new Intent();
        intent.putExtra(NEW_PLANT, plant);
        setResult(Activity.RESULT_OK, intent);

        finish();
    }

    private int toDays(int frequencyOfWatering) {

        if (frequencyOfWatering >= 0 && frequencyOfWatering <= 30) {
            return frequencyOfWatering;
        } else if (frequencyOfWatering > 30 && frequencyOfWatering <= 35) {
            return (30 + (frequencyOfWatering - 30) * 5);
        } else {
            return (frequencyOfWatering - 34) * 30;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (data != null) {
            Bitmap newPhoto = (Bitmap) data.getExtras().get("data");
                if (newPhoto.getWidth() >= newPhoto.getHeight()) {

                    photo = Bitmap.createBitmap(
                            newPhoto,
                            newPhoto.getWidth() / 2 - newPhoto.getHeight() / 2,
                            0,
                            newPhoto.getHeight(),
                            newPhoto.getHeight()
                    );

                } else {

                    photo = Bitmap.createBitmap(
                            newPhoto,
                            0,
                            newPhoto.getHeight() / 2 - newPhoto.getWidth() / 2,
                            newPhoto.getWidth(),
                            newPhoto.getWidth()
                    );
                }
                binding.imageView.setImageBitmap(photo);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        binding = null;
    }
}