package opetbrothers.com.encontrefacil.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.util.Maps;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import opetbrothers.com.encontrefacil.Adapters.ProdutosPessoaFisicaAdapter;
import opetbrothers.com.encontrefacil.Model.Localizacao;
import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class MapsLojasPertoActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    MapView mMapView;
    Localizacao minhaLocalizacao;
    Location location;
    LocationManager locationManager;
    String provider;
    List<Marker> originMarkers = new ArrayList<>();
    private android.support.v7.app.AlertDialog alerta;

    //region ANDROID METODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_lojas_perto);

        mMapView = (MapView) findViewById(R.id.mapViewLojas);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if(mMapView != null)
        {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        PegarLojasPerto();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Seu Gps esta desativado, por favor ative!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_padrao,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region WEBSERVICE
    public class BuscarLojas extends AsyncTask<Localizacao, Void, String> {

        //region ON PRE EXECUTE
        boolean isConnected = false;
        ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ConnectivityManager cm =
                    (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if(isConnected) {
                progress = new ProgressDialog(MapsLojasPertoActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(MapsLojasPertoActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }
        //endregion

        //region DO IN BACKGROUN
        @Override
        protected String doInBackground(Localizacao... params) {
            Gson gson = new Gson();
            String result = HttpMetods.POST("PessoaJuridica/LojasPerto", gson.toJson(params[0]));
            return result;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);

            if(isConnected) {
                if (s != null) {
                    try {
                        JSONObject object = new JSONObject(s);
                        Gson gson = new Gson();
                        if(object.getBoolean("ok"))
                        {
                            JSONArray listProdutosJson = object.getJSONArray("lista");
                            for (int i = 0; i < listProdutosJson.length(); i++) {
                                JSONObject objProduto = listProdutosJson.getJSONObject(i);
                                objProduto.remove("type");
                                PessoaJuridica pessoaJuridica = gson.fromJson(objProduto.toString(), PessoaJuridica.class);
                                LatLng local = new LatLng(Double.parseDouble(pessoaJuridica.getFk_Localizacao().getLatitude()),Double.parseDouble(pessoaJuridica.getFk_Localizacao().getLongitude()));
                                originMarkers.add(mMap.addMarker(new MarkerOptions()
                                        .title(pessoaJuridica.getRazao_Social())
                                        .position(local)));
                            }
                        }
                        else {
                            Toast.makeText(MapsLojasPertoActivity.this, "Nenhuma loja encontrada", Toast.LENGTH_LONG).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(MapsLojasPertoActivity.this, "Ocorreu um erro no servidor.", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(MapsLojasPertoActivity.this, "Verifique a sua conexão...", Toast.LENGTH_LONG).show();
            }
            progress.dismiss();
        }
        //endregion
    }
    //endregion

    //region OTHER METODS
    public void PegarLojasPerto(){
        if(ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null)
        {
            // Pega a localizacao do usuario nome da cidade, bairro e estado
            Localizacao loca = new Localizacao();
            loca.setLatitude(String.valueOf(location.getLatitude()));
            loca.setLongitude(String.valueOf(location.getLongitude()));
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    loca.setBairro(addresses.get(0).getSubLocality());
                    loca.setCidade(addresses.get(0).getLocality());
                    loca.setEstado(addresses.get(0).getAdminArea());
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            minhaLocalizacao = loca;
            new BuscarLojas().execute(loca);
            //endregion
        }else{
            //region ALERT DIALOG BUILDER
            //Cria o gerador do AlertDialog
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MapsLojasPertoActivity.this);
            //define o titulo
            builder.setTitle("Aviso!");
            //define a mensagem
            builder.setMessage("Caso não tenha ativado seu GPS clique em ativar, se já clique em tentar novamente");
            //define um botão como positivo
            builder.setPositiveButton("Tente Novamente", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    PegarLojasPerto();
                }
            });
            builder.setNegativeButton("Ativar GPS", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setCancelable(false);
            //cria o AlertDialog
            alerta = builder.create();
            //Exibe
            alerta.show();
            //endregion
        }
    }
    //endregion


}
