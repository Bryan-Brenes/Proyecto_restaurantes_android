package com.example.restaurantes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;

public class Process extends AsyncTask<Void, Void, ArrayList<Bitmap>> {

  ArrayList<JsonArray> photos;
  ArrayList<Bitmap> images = new ArrayList<>();

  public Process(ArrayList<JsonArray> photos) {
    this.photos = photos;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    if(photos.size() == 0) {
      cancel(true);
    }
  }

  @Override
  protected ArrayList<Bitmap> doInBackground(Void... voids) {
    for(int i = 0; i < photos.size(); i++) {
      JsonArray data = photos.get(i);
      Bitmap image = processImage(data);
      images.add(image);
    }
    return images;
  }

  private Bitmap processImage(JsonArray data) {
    byte[] values = new byte[data.size()];
    for(int i = 0; i < data.size(); i++) {
      JsonElement element = data.get(i);
      values[i] = element.getAsByte();
    }
    Bitmap bitmap = BitmapFactory.decodeByteArray(values, 0, values.length);
    return bitmap;
  }

}