package com.example.restaurantes;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FiltersActivity extends AppCompatActivity {

  public static final String PREFS_NAME = "FoodCourTec";

  private final int MIN_CAF = 0;
  private final int MAX_CAF = 5;
  private final int MIN_PRICE = 0;
  private final int MAX_PRICE = 3;

  private ArrayList<String> foods = new ArrayList<>();

  private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
  private LinearLayout layoutFoods;

  private EditText minPrice;
  private EditText maxPrice;
  private EditText minCaf;
  private EditText maxCaf;
  private EditText distance;

  private Button save;

  private MixpanelAPI mixpanel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_filters);

    mixpanel = MixpanelAPI.getInstance(getApplicationContext(), LoginActivity.MIXPANEL_TOKEN);

    JSONObject props = new JSONObject();
    try {
      props.put("Usuario", SessionManager.getEmail());
      props.put("Actividad", "Filtros");
      mixpanel.track("Movimiento", props);
    }
    catch (JSONException e) {
      e.printStackTrace();
    }

    layoutFoods = findViewById(R.id.layoutFoods);

    minPrice = findViewById(R.id.minPrice);
    maxPrice = findViewById(R.id.maxPrice);
    minCaf = findViewById(R.id.minCaf);
    maxCaf = findViewById(R.id.maxCaf);
    distance = findViewById(R.id.distance);

    save = findViewById(R.id.save);

    getFoods();

    minPrice.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        String text = minPrice.getText().toString();
        if(text.equals("")) {
          return;
        }
        int number = Integer.valueOf(minPrice.getText().toString());
        if(number < MIN_PRICE) {
          minPrice.setText(String.valueOf(MIN_PRICE));
        }
      }
    });

    maxPrice.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        String text = maxPrice.getText().toString();
        if(text.equals("")) {
          return;
        }
        int number = Integer.valueOf(maxPrice.getText().toString());
        if(number > MAX_PRICE) {
          maxPrice.setText(String.valueOf(MAX_PRICE));
        }
      }
    });

    minCaf.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        String text = minCaf.getText().toString();
        if(text.equals("")) {
          return;
        }
        int number = Integer.valueOf(minCaf.getText().toString());
        if(number < MIN_CAF) {
          minCaf.setText(String.valueOf(MIN_CAF));
        }
      }
    });

    maxCaf.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        String text = maxCaf.getText().toString();
        if(text.equals("")) {
          return;
        }
        int number = Integer.valueOf(maxCaf.getText().toString());
        if(number > MAX_CAF) {
          maxCaf.setText(String.valueOf(MAX_CAF));
        }
      }
    });

  }

  @Override
  protected void onDestroy() {
    if(mixpanel != null) {
      mixpanel.flush();
    }
    super.onDestroy();
  }

  public static JSONObject getFilters(int lat, int log, Context context) {
    SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    JSONObject filters = new JSONObject();
    try {
      if(settings.contains("price")) {
        JSONObject price = new JSONObject(settings.getString("price", ""));
        filters.put("price", price);
      }
      if(settings.contains("calification")) {
        JSONObject calification = new JSONObject(settings.getString("calification", ""));
        filters.put("calification", calification);
      }
      if(settings.contains("foods")) {
        JSONArray array = new JSONArray(settings.getString("foods", ""));
        filters.put("foods", array);
      }
      if(settings.contains("distance")) {
        int distance = settings.getInt("distance", 100);
        JSONObject ubication = new JSONObject();
        ubication.put("lat", lat);
        ubication.put("long", log);
        ubication.put("distance", distance);
        filters.put("ubication", ubication);
      }
    }
    catch(JSONException e) {
      e.printStackTrace();
    }
    return filters;
  }

  private void getFoods() {
    JsonObject info = new JsonObject();
    info.addProperty("token", SessionManager.getToken());
    info.addProperty("email", SessionManager.getEmail());
    Future uploading = Ion.with(getApplicationContext())
      .load("POST", Post_json.OBTENER_COMIDAS)
      .setJsonObjectBody(info)
      .asJsonObject()
      .setCallback(new FutureCallback<JsonObject>() {
        @Override
        public void onCompleted(Exception e, JsonObject result) {
          if(e != null) {
            Toast.makeText(getApplicationContext(), "Fallo con el servidor", Toast.LENGTH_SHORT).show();
          }
          else {
            String status = result.get("status").getAsString();
            switch(status) {
              case "Successful":
                String token = result.get("token").getAsString();
                SessionManager.setToken(token);
                JsonArray array = result.getAsJsonArray("foods");
                saveFoods(array);
                createCheckBoxes();
                break;
              default:
                Toast.makeText(getApplicationContext(), "Fallo al procesar las comidas", Toast.LENGTH_SHORT).show();
                break;
            }
          }
        }
      });
  }

  private void saveFoods(JsonArray array) {
    for(int i = 0; i < array.size(); i++) {
      JsonElement element = array.get(i);
      String food = element.getAsString();
      foods.add(food);
    }
  }

  private void createCheckBoxes() {
    if(foods.size() > 0) {
      for(int i = 0; i < foods.size(); i++) {
        String food = foods.get(i);
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(food);
        layoutFoods.addView(checkBox);
        checkBoxes.add(checkBox);
      }
    }
  }

  public void saveFilters(View view) {
    try {
      JSONArray array = new JSONArray();
      for(int i = 0; i < checkBoxes.size(); i++) {
        CheckBox checkBox = checkBoxes.get(i);
        if(checkBox.isChecked()) {
          String food = checkBox.getText().toString();
          array.put(food);
        }
      }
      JSONObject price = new JSONObject();
      JSONObject caf = new JSONObject();
      int min = minPrice.getText().toString().equals("") ? MIN_PRICE : Integer.valueOf(minPrice.getText().toString());
      int max = maxPrice.getText().toString().equals("") ? MAX_PRICE : Integer.valueOf(maxPrice.getText().toString());
      if(min > max) {
        Toast.makeText(getApplicationContext(), "Datos invalidos", Toast.LENGTH_SHORT).show();
        return;
      }
      price.put("min", min);
      price.put("max", max);
      min = minCaf.getText().toString().equals("") ? MIN_CAF : Integer.valueOf(minCaf.getText().toString());
      max = maxCaf.getText().toString().equals("") ? MAX_CAF : Integer.valueOf(maxCaf.getText().toString());
      caf.put("min", min);
      caf.put("max", max);
      if(min > max) {
        Toast.makeText(getApplicationContext(), "Datos invalidos", Toast.LENGTH_SHORT).show();
        return;
      }
      int distance = this.distance.getText().toString().equals("") ? -1 : Math.abs(Integer.valueOf(this.distance.getText().toString()));
      SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
      editor.putString("price", price.toString());
      editor.putString("calification", caf.toString());
      if(distance != -1) editor.putInt("distance", distance);
      if(array.length() > 0) editor.putString("foods", array.toString());
      editor.apply();
      Toast.makeText(getApplicationContext(), "Filtros guardados", Toast.LENGTH_SHORT).show();
    }
    catch (JSONException e) {
      Toast.makeText(getApplicationContext(), "Fallo al guardar los filtros", Toast.LENGTH_SHORT).show();
    }
  }

}
