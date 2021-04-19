package com.grzeluu.wateringreminder.model;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;

public class Plant implements Serializable {
    private byte[] byteArray;
    private String name;
    private String species;

    private int frequencyOfWatering;
    private int frequencyOfFertilizing;
    private int frequencyOfSpraying;
    private int frequencyOfRotating;

    private Date lastWatering;
    private Date lastFertilizing;
    private Date lastSpraying;
    private Date lastRotating;

    public Plant() {
    }

    public Plant(Bitmap photo, String name, String species, int frequencyOfWatering, int frequencyOfFertilizing, int frequencyOfSpraying, int frequencyOfRotating) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        this.byteArray = stream.toByteArray();

        this.name = name;
        this.species = species;
        this.frequencyOfWatering = frequencyOfWatering;
        this.frequencyOfFertilizing = frequencyOfFertilizing;
        this.frequencyOfSpraying = frequencyOfSpraying;
        this.frequencyOfRotating = frequencyOfRotating;

        this.lastWatering = new Date();
        this.lastFertilizing = new Date();
        this.lastSpraying = new Date();
        this.lastRotating = new Date();
    }

    public Date getLastWatering() {
        return lastWatering;
    }

    public void setLastWatering(Date lastWatering) {
        this.lastWatering = lastWatering;
    }

    public Date getLastFertilizing() {
        return lastFertilizing;
    }

    public void setLastFertilizing(Date lastFertilizing) {
        this.lastFertilizing = lastFertilizing;
    }

    public Date getLastSpraying() {
        return lastSpraying;
    }

    public void setLastSpraying(Date lastSpraying) {
        this.lastSpraying = lastSpraying;
    }

    public Date getLastRotating() {
        return lastRotating;
    }

    public void setLastRotating(Date lastRotating) {
        this.lastRotating = lastRotating;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public byte[] getPhoto() {
        return byteArray;
    }

    public void setPhoto(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public int getFrequencyOfWatering() {
        return frequencyOfWatering;
    }

    public void setFrequencyOfWatering(int frequencyOfWatering) {
        this.frequencyOfWatering = frequencyOfWatering;
    }

    public int getFrequencyOfFertilizing() {
        return frequencyOfFertilizing;
    }

    public void setFrequencyOfFertilizing(int frequencyOfFertilizing) {
        this.frequencyOfFertilizing = frequencyOfFertilizing;
    }

    public int getFrequencyOfSpraying() {
        return frequencyOfSpraying;
    }

    public void setFrequencyOfSpraying(int frequencyOfSpraying) {
        this.frequencyOfSpraying = frequencyOfSpraying;
    }

    public int getFrequencyOfRotating() {
        return frequencyOfRotating;
    }

    public void setFrequencyOfRotating(int frequencyOfRotating) {
        this.frequencyOfRotating = frequencyOfRotating;
    }
}

