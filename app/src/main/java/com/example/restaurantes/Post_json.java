package com.example.restaurantes;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.util.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Post_json extends AsyncTask<DatosConsulta, Void, JSONObject> {

    public static String REGISTRAR_USUARIO = "https://foodcourtec.herokuapp.com/createUser/";
    public static String LOGIN_USUARIO = "https://foodcourtec.herokuapp.com/loginUser/";
    public static String OBTENER_COMIDAS = "https://foodcourtec.herokuapp.com/getFoods";
    public static String CREAR_RESTAURANTE = "https://foodcourtec.herokuapp.com/createRestaurant";
    public static String REQUEST_CODE = "https://foodcourtec.herokuapp.com/requestCode";
    public static String RESET_PASSWORD = "https://foodcourtec.herokuapp.com/resetPassword";
    public static String OBTENER_RESTAURANTES = "https://foodcourtec.herokuapp.com/getRestaurants";
    public static String ACTUALIZAR_RESTAURANTES = "https://foodcourtec.herokuapp.com/updateRestaurant";
    public static String TEST_URL = "https://data.fixer.io/api/latest";

    private JSONObject post(String URL_STRING, JSONObject jsonRequest){
        JSONObject respuesta = null;

        try {
            URL url = new URL(URL_STRING);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            //conn.connect();

            //send request
            OutputStream os = conn.getOutputStream();
            os.write(jsonRequest.toString().getBytes());
            os.close();

            // read response
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            String resultado = convertInputStream(inputStream);

            respuesta = new JSONObject(resultado);
            inputStream.close();
            conn.disconnect();

        } catch (MalformedURLException e){
            Log.e("url", String.format("Url malformada: %s", URL_STRING));
            e.printStackTrace();
        } catch (IOException e){
            Log.e("url", "No se pudo abrir la conexión");
            e.printStackTrace();
        } catch (JSONException e){
            Log.e("url", "No se puedo generar el json de respuesta");
            e.printStackTrace();
        } catch (Exception e){
            Log.e("url", "otra excepcion");
            e.printStackTrace();
        }

        return respuesta;
    }

    private static String convertInputStream(InputStream inputStream){
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        Charset charset = StandardCharsets.UTF_8;

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    public static String verificarSiHuboError(JSONObject json){
        String error = null;

        try{
            error = json.getString("error");
        } catch (Exception e){
            e.printStackTrace();
        }

        return error;
    }

    public static String verificarSiTieneStatus(JSONObject json){
        String status = null;

        try{
            status = json.getString("status");
        } catch (Exception e){
            e.printStackTrace();
        }

        return status;
    }

    /*@Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
    }*/

    @Override
    protected JSONObject doInBackground(DatosConsulta... datosConsultas) {
        return post(datosConsultas[0].getURL(), datosConsultas[0].getJson());
    }
}
