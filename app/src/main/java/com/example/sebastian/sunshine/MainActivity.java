package com.example.sebastian.sunshine;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sebastian.sunshine.Model.DailyWeatherReport;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    final String URL_Base = "http://api.openweathermap.org/data/2.5/forecast";
    final String URL_Coord_1lat = "?lat=";
    final String URL_Coord_2long = "&lon=";
    final String URL_Units = "&units=metric";
    final String URL_API_KEY = "&APPID=0126a0fb68646bdab5401e71af40ea04";

    final int PERMISION_LOCATION = 1;

    private GoogleApiClient mGoogleApiClient;

    private ImageView weatherImage;
    private TextView currentTemp;
    private TextView location;
    private TextView minTemp;
    private TextView weatherConditions;
    private TextView date;

    WeatherReportaAdaptor mAdaptor;

    private ArrayList<DailyWeatherReport> weatherReportList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        weatherImage = (ImageView)findViewById(R.id.weatherImg);
        currentTemp = (TextView)findViewById(R.id.temp);
        location = (TextView)findViewById(R.id.location);
        minTemp = (TextView)findViewById(R.id.minTemp);
        weatherConditions = (TextView)findViewById(R.id.weather);
        date = (TextView)findViewById(R.id.date);

        //create the RecyclerView, set the layoutmanager and the adaptor
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.content_recycler);
        mAdaptor = new WeatherReportaAdaptor(weatherReportList);
        recyclerView.setAdapter(mAdaptor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);



    }

    public void downloadWeatherData (Location location) {
        final String latitudine = URL_Coord_1lat + location.getLatitude();
        final String longitudine = URL_Coord_2long + location.getLongitude();

        final String url = URL_Base + latitudine + longitudine + URL_Units + URL_API_KEY;

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    // grab the JSON object and the name and country from it.
                    JSONObject city = response.getJSONObject("city");
                    String cityName = city.getString("name");
                    String country = city.getString("country");

                    // grab the JSON array list for the next 5 days
                    JSONArray list = response.getJSONArray("list");

                    for (int x = 0; x < 39;) {
                        JSONObject object = list.getJSONObject(x);
                        JSONObject main = object.getJSONObject("main");
                        Double current_temp = main.getDouble("temp");
                        Double max_temp = main.getDouble("temp_max");
                        Double min_temp = main.getDouble("temp_min");

                        JSONArray weatherArray = object.getJSONArray("weather");
                        JSONObject weatherType = weatherArray.getJSONObject(0);
                        String weather = weatherType.getString("main");

                        String rawDate = object.getString("dt_txt");

                        DailyWeatherReport report = new DailyWeatherReport(cityName, country, current_temp.intValue(), min_temp.intValue(), max_temp.intValue(), weather, rawDate);

                        Log.v("JSON", "Printing from: " + report.getCity());
                        Log.v("JSON", "Printing from: " + report.getCountry());
                        Log.v("JSON", "Printing from: " + report.getWeather());
                        Log.v("JSON", "Printing from: " + report.getTemp());

                        weatherReportList.add(report);

                        // grab the next day
                        x = x + 8;
                    }

                    Log.v("JSON", cityName + " " + country);

                } catch (JSONException e) {
                    // catch any errors from the JSON
                    Log.v("JSON", "EXEPTION: " + e.getLocalizedMessage());

                }

                updateUi();
                mAdaptor.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Sun", "Error:" + error.getLocalizedMessage());

            }
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

        // update the ui function for the current day
    public void updateUi (){
        if (weatherReportList.size() > 0) {
            DailyWeatherReport report = weatherReportList.get(0);

            switch (report.getWeather()) {
                case DailyWeatherReport.WEATHER_TYPE_CLOUDS:
                    weatherImage.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_RAIN:
                    weatherImage.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_SNOW:
                    weatherImage.setImageDrawable(getResources().getDrawable(R.drawable.snow));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_THUNDERSTORM:
                    weatherImage.setImageDrawable(getResources().getDrawable(R.drawable.thunder_lightning));
                    break;

                    default:
                    weatherImage.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
            }
            date.setText("Today, " + report.getDate());
            currentTemp.setText(Integer.toString(report.getTemp()));
            minTemp.setText(Integer.toString(report.getMin_temp()));
            location.setText(report.getCity() + " " + report.getCountry());
            weatherConditions.setText(report.getWeather());
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // start the location service function
        startLocationService();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        downloadWeatherData(location);

    }

    // asta si urmatoarea se pot reutiliza de fiecare data cand e nevoie de permisiune pentru GPS (de aflat cu ce s-a deprecat functia de mai jos
    public void startLocationService () {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISION_LOCATION);
        } else {
            try {
                LocationRequest request = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
            } catch (SecurityException exeption) {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService();
                } else {
                    // show something to let the user know they have not granted permission
                    Toast.makeText(this, "I can't run you idiot, you didn't grant me permision", Toast.LENGTH_SHORT);
                }
            }
        }
    }


    // Adaptor
    public class WeatherReportaAdaptor extends RecyclerView.Adapter<WeatherReportViewHolder> {

        private ArrayList<DailyWeatherReport> mDailyWeatherReport;

        public WeatherReportaAdaptor(ArrayList<DailyWeatherReport> mDailyWeatherReport) {
            this.mDailyWeatherReport = mDailyWeatherReport;
        }

        @Override
        public WeatherReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_daily_weather, parent, false);

            return new WeatherReportViewHolder(card);
        }

        @Override
        public void onBindViewHolder(WeatherReportViewHolder holder, int position) {
            DailyWeatherReport report = mDailyWeatherReport.get(position);
            holder.updateDailyUI(report);

        }

        @Override
        public int getItemCount() {
            return mDailyWeatherReport.size();
        }
    }

    // View Holder
    public class WeatherReportViewHolder extends RecyclerView.ViewHolder{

        private ImageView cardWeatherMini;
        private TextView cardDate;
        private TextView cardDescription;
        private TextView cardMinTemp;
        private TextView cardMaxTemp;

        public WeatherReportViewHolder(View itemView) {
            super(itemView);

            cardWeatherMini = (ImageView)itemView.findViewById(R.id.weather_mini_img);
            cardDate = (TextView) itemView.findViewById(R.id.weather_date);
            cardDescription = (TextView) itemView.findViewById(R.id.weather_description);
            cardMaxTemp = (TextView) itemView.findViewById(R.id.weather_temp_max);
            cardMinTemp = (TextView) itemView.findViewById(R.id.weather_temp_min);


        }

        // update ui function for the recycler view
        public void updateDailyUI (DailyWeatherReport report) {

            cardDate.setText(report.getDate());
            cardDescription.setText(report.getWeather());
            cardMinTemp.setText(Integer.toString(report.getMin_temp()));
            cardMaxTemp.setText(Integer.toString(report.getMax_temp()));

            switch (report.getWeather()) {
                case DailyWeatherReport.WEATHER_TYPE_CLOUDS:
                    cardWeatherMini.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_RAIN:
                    cardWeatherMini.setImageDrawable(getResources().getDrawable(R.drawable.rainy_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_SNOW:
                    cardWeatherMini.setImageDrawable(getResources().getDrawable(R.drawable.snow_mini));
                    break;
                case DailyWeatherReport.WEATHER_TYPE_THUNDERSTORM:
                    cardWeatherMini.setImageDrawable(getResources().getDrawable(R.drawable.thunder_lightning_mini));

                default:
                    cardWeatherMini.setImageDrawable(getResources().getDrawable(R.drawable.sunny_mini));
            }

        }
    }
}
