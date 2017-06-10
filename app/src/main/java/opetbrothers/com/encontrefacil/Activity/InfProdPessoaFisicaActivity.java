package opetbrothers.com.encontrefacil.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import opetbrothers.com.encontrefacil.Model.Localizacao;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.Util;

public class InfProdPessoaFisicaActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    MapView mMapView;
    Gson gson = new Gson();
    String distanciaUserLoja;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inf_prod_pessoa_fisica);


        Produto produtoSelecionado;
        String returnProduto = Util.RecuperarUsuario("produtoSelecionado", InfProdPessoaFisicaActivity.this);

        produtoSelecionado = gson.fromJson(returnProduto, Produto.class);

        TextView txNomeProduto = (TextView) findViewById(R.id.textViewNomeProduto);
        TextView txDescricao = (TextView) findViewById(R.id.textViewDescricao);
        TextView txLoja = (TextView) findViewById(R.id.textViewLoja);

        txNomeProduto.setText(produtoSelecionado.getNome());
        txDescricao.setText(produtoSelecionado.getDescricao());
        txLoja.setText(produtoSelecionado.getFk_Pessoa_Juridica().getRazao_Social());

        medeDistancia();

        mMapView = (MapView) findViewById(R.id.mapViewInfProd);

        if(mMapView != null)
        {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-33.852, 151.211);
        mMap.addMarker(new MarkerOptions().position(sydney)
                .title("Loja"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 25));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_infprod_pessoafisica,menu);
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
            case R.id.infAvaliacoes:
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(InfProdPessoaFisicaActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_avaliacao_produto, null);
                // ADD HERE ALL USABLE ITEMS
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void medeDistancia(){

        LocationManager locationManager;
        String provider;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        if(ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        try {

            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());

            Produto produtoSelecionado;
            String returnProduto = Util.RecuperarUsuario("produtoSelecionado", InfProdPessoaFisicaActivity.this);

            produtoSelecionado = gson.fromJson(returnProduto, Produto.class);

            Location meuLocal = new Location("");
            meuLocal.setLatitude(location.getLatitude());
            meuLocal.setLongitude(location.getLongitude());

            Location loja = new Location("");
            loja.setLatitude(Double.parseDouble(produtoSelecionado.getFk_Pessoa_Juridica().getFk_Localizacao().getLatitude()));
            loja.setLongitude(Double.parseDouble(produtoSelecionado.getFk_Pessoa_Juridica().getFk_Localizacao().getLongitude()));

            Float distancia = location.distanceTo(loja)/1000;

            DecimalFormat df = new DecimalFormat("0.000");
            distanciaUserLoja = df.format(distancia);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(InfProdPessoaFisicaActivity.this, "Ocorreu um erro no servidor. Tente novamente!", Toast.LENGTH_LONG).show();

        }

    }


    public void infosLojas (View v){

        Gson gson = new Gson();
        String returnProduto = Util.RecuperarUsuario("produtoSelecionado", InfProdPessoaFisicaActivity.this);
        Produto lojaProduto;
        lojaProduto = gson.fromJson(returnProduto, Produto.class);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(InfProdPessoaFisicaActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_inf_loja_produto, null);
        TextView editNomeLoja = (TextView) mView.findViewById(R.id.editNomeLoja);
        TextView editCategoriaLoja = (TextView) mView.findViewById(R.id.editCategoriaLoja);
        TextView distanciaLoja = (TextView) mView.findViewById(R.id.editDistancia);
        ImageView fotoLoja = (ImageView) mView.findViewById(R.id.imagemLoja);

        editNomeLoja.setText(lojaProduto.getFk_Pessoa_Juridica().getRazao_Social());
        editCategoriaLoja.setText(lojaProduto.getFk_Pessoa_Juridica().getFk_Categoria_Loja().getNome());
        distanciaLoja.setText(distanciaUserLoja + " Km de distância");


        byte[] foto = Base64.decode(lojaProduto.getFk_Pessoa_Juridica().getFk_Pessoa().getFoto(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
        fotoLoja.setImageBitmap(bitmap);

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(InfProdPessoaFisicaActivity.this);
        builder.setMessage("Seu GPS esta desativado, por favor ative!")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {

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
}

