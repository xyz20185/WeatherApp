package bd.com.infobox.weather.Fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bd.com.infobox.weather.Adapter.ForecastRVAdapter;
import bd.com.infobox.weather.Constants.Constant;
import bd.com.infobox.weather.Model.ForecastWeatherPick.ForecastWeatherResponse;
import bd.com.infobox.weather.R;
import bd.com.infobox.weather.Services.RetrofitClient;
import bd.com.infobox.weather.Services.WeatherServiceAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastWeatherFragment extends Fragment {

    private RecyclerView weatherListRV;
    private TextView cityFTV;
    private ForecastRVAdapter adapter;
    private String forecast_weather_url;


    public ForecastWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forecast_weather, container, false);


        weatherListRV = view.findViewById(R.id.weatherListRV);
        cityFTV = view.findViewById(R.id.cityFTV);



        WeatherServiceAPI weatherServiceAPI = RetrofitClient.getClient(Constant.baseUrl.WEATHER_BASE_URL).create(WeatherServiceAPI.class);
        double latitude = getArguments().getDouble("lat");
        double longitude = getArguments().getDouble("lng");
        forecast_weather_url = String.format("forecast?lat=%f&lon=%f&units=metric&appid=%s", latitude, longitude, Constant.apiKeys.WEATHER_API);

        weatherServiceAPI.getForecastWeatherResponse(forecast_weather_url)
                .enqueue(new Callback<ForecastWeatherResponse>() {
                    @Override
                    public void onResponse(Call<ForecastWeatherResponse> call, Response<ForecastWeatherResponse> response) {
                        if (response.isSuccessful()){
                            ForecastWeatherResponse forecastWeatherResponse = response.body();

                            List<bd.com.infobox.weather.Model.ForecastWeatherPick.List> weatherLists = forecastWeatherResponse.getList();

                            String userCity = forecastWeatherResponse.getCity().getName()+ ", ";
                            String userCountry = forecastWeatherResponse.getCity().getCountry();
                            if (userCity.contains(getString(R.string.bangshal))){
                                String bangshal_country = "Weather in " + getString(R.string.dhaka) + ", " + userCountry;
                                cityFTV.setText(bangshal_country);
                            } else {
                                String city_country = "Weather in " + userCity + userCountry;
                                cityFTV.setText(city_country);
                            }

                            if (adapter == null){
                                adapter = new ForecastRVAdapter(weatherLists, getContext());

                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                weatherListRV.setLayoutManager(linearLayoutManager);
                                weatherListRV.setAdapter(adapter);
                            } else {
                                adapter.updateCollection(weatherLists);

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastWeatherResponse> call, Throwable t) {
                        Toast.makeText(getContext(), " Failed to load"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ForecastFailed: ", t.getMessage() + "\n"+t.getLocalizedMessage());
                    }
                });


        return view;
    } // ending onCreateView

}
