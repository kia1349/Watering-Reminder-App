package com.grzeluu.wateringreminder.model;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.grzeluu.wateringreminder.R;
import com.grzeluu.wateringreminder.databinding.PlantItemBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ALARM_SERVICE;

public class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.ViewHolder> {
    private static final String EDIT_PLANT = "1111";
    private Context context;
    private List<Plant> plantList;

    public PlantsAdapter(Context context, ArrayList<Plant> plantsArrayList) {
        this.context = context;
        this.plantList = plantsArrayList;
    }

    @NonNull
    @Override
    public PlantsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(PlantItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlantsAdapter.ViewHolder holder, int position) {
        Plant plant = plantList.get(position);
        byte[] byteArray = plant.getPhoto();
        Bitmap photo = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        holder.binding.imageView.setImageBitmap(photo);
        holder.binding.name.setText(plant.getName());

        if (plant.getFrequencyOfWatering() > 0) {
            setItemLayout(holder.binding.waterLeftTv,
                    holder.binding.wateringProgressBar,
                    holder.binding.warning,
                    plant.getFrequencyOfWatering(),
                    plant.getLastWatering());
        } else {
            holder.binding.waterLayout.setVisibility(View.GONE);
        }

        if (plant.getFrequencyOfFertilizing() > 0) {
            setItemLayout(holder.binding.fertilizerLeftTv,
                    holder.binding.fertilizationProgressBar,
                    holder.binding.warning,
                    plant.getFrequencyOfFertilizing(),
                    plant.getLastFertilizing());
        } else {
            holder.binding.fertilizerLayout.setVisibility(View.GONE);
        }

        if (plant.getFrequencyOfSpraying() > 0) {
            setItemLayout(holder.binding.sprayLeftTv,
                    holder.binding.sprayingProgressbar,
                    holder.binding.warning,
                    plant.getFrequencyOfSpraying(),
                    plant.getLastSpraying());
        } else {
            holder.binding.sprayLayout.setVisibility(View.GONE);
        }

        if (plant.getFrequencyOfRotating() > 0) {
            setItemLayout(holder.binding.rotateLeftTv,
                    holder.binding.rotatingProgressbar,
                    holder.binding.warning,
                    plant.getFrequencyOfRotating(),
                    plant.getLastRotating());
        } else {
            holder.binding.rotateLayout.setVisibility(View.GONE);
        }

        holder.binding.edit.setOnClickListener(editRecord(position));
        holder.binding.takeCare.setOnClickListener(showPopup(plant, position));
    }

    public View.OnClickListener showPopup(Plant plant, int position) {
        return v -> {
            Dialog careDialog = new Dialog(context);
            careDialog.setContentView(R.layout.take_care_popup);

            ImageView image = careDialog.findViewById(R.id.imageView);
            TextView name = careDialog.findViewById(R.id.nameTv);

            Button confirmButton = careDialog.findViewById(R.id.confirmButton);
            CheckBox watering = careDialog.findViewById(R.id.wateringCheckBox);
            CheckBox fertilizing = careDialog.findViewById(R.id.fertilizingCheckBox);
            CheckBox spraying = careDialog.findViewById(R.id.sprayingCheckBox);
            CheckBox rotating = careDialog.findViewById(R.id.rotatingCheckBox);

            image.setImageBitmap(BitmapFactory.decodeByteArray(plant.getByteArray(), 0 , plant.getByteArray().length));
            name.setText(plant.getName());

            Intent intent = new Intent(context, ReminderBroadcast.class);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            long timeAtAddNewPlant = SystemClock.elapsedRealtime();

            confirmButton.setOnClickListener(v1 -> {
                Date currentDate = new Date();
                if (watering.isChecked()) {
                    plant.setLastWatering(currentDate);
                }

                if (fertilizing.isChecked()) {
                    plant.setLastFertilizing(currentDate);
                }

                if (spraying.isChecked()) {
                    plant.setLastSpraying(currentDate);
                }
                if (rotating.isChecked()) {
                    plant.setLastRotating(currentDate);
                }

                //TODO add new notification to notification channel

                notifyDataSetChanged();
                careDialog.dismiss();
            });

            careDialog.show();
        };
    }

    private long daysToMillis(int daysCount) {
        return daysCount * 24 * 60 * 60 * 1000;
    }

    private void setItemLayout(TextView text, ProgressBar progressBar, ImageView warning, int frequency, Date lastAction) {
        int daysFromLastAction = daysFromLastAction(lastAction);
        int fill = 100 - daysFromLastAction * 100 / frequency;
        int daysLeft = frequency - daysFromLastAction;

        progressBar.setProgress(fill);
        if (daysLeft > 1) {
            text.setText("In " + daysLeft + " days");
        }
        if (daysLeft == 1) {
            text.setText("Tomorrow");
        }
        if (daysLeft < 1) {
            text.setText("Today");
            warning.setVisibility(View.VISIBLE);
        }
    }

    private int daysFromLastAction(Date lastDate) {
        Date currentDate = new Date();
        long diff = currentDate.getTime() - lastDate.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private View.OnClickListener editRecord(int position) {
        return view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete plant");
            builder.setMessage("Are you sure to delete this plant?");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    plantList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeRemoved(position, 1);
                }
            });
            builder.setNeutralButton(R.string.cancel, null);

            AlertDialog dialog = builder.create();
            dialog.show();
        };
    }

    @Override
    public int getItemCount() {
        if (plantList == null) {
            return 0;
        } else {
            return plantList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private PlantItemBinding binding;

        public ViewHolder(PlantItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

