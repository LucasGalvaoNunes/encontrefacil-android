package opetbrothers.com.encontrefacil.Activity;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.MapsInitializer;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import opetbrothers.com.encontrefacil.Adapters.ProdutosPessoaFisicaAdapter;
import opetbrothers.com.encontrefacil.Model.Localizacao;
import opetbrothers.com.encontrefacil.Model.PessoaFisica;
import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class MainPessoaFisicaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pesssoa_fisica);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LoginActivity login = new LoginActivity();
        login.finish();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView username = (TextView) header.findViewById(R.id.textViewNomePessoaFisicaLogada);
        ImageView imagePerfil = (ImageView) header.findViewById(R.id.imageView7);

        PessoaFisica pessoaFisica;

        String jsonPrefe = Util.RecuperarUsuario("pessoaFisica", MainPessoaFisicaActivity.this);
        Gson gson = new Gson();
        pessoaFisica = gson.fromJson(jsonPrefe, PessoaFisica.class);
        byte[] foto = Base64.decode(pessoaFisica.getFk_Pessoa().getFoto(), Base64.DEFAULT);
        username.setText(pessoaFisica.getFk_Pessoa().getNome() + " " + pessoaFisica.getFk_Pessoa().getSobrenome());
        Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
        imagePerfil.setImageBitmap(bitmap);

        loadList();
}

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void loadList(){

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
            List<Address> addresses;
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            Localizacao local = new Localizacao();

            local.setCidade(addresses.get(0).getSubLocality());
            local.setEstado(addresses.get(0).getAdminArea());
            local.setCidade(addresses.get(0).getLocality());

            Location dois = new Location("");
            dois.setLatitude(-25.4437563);
            dois.setLongitude(-49.2699716);

            //Float xxx = location.distanceTo(dois);

            new CarregaDestaques().execute(local);

        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(MainPessoaFisicaActivity.this, "Ocorreu um erro na conexão. Tente novamente!", Toast.LENGTH_LONG).show();
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(getApplicationContext(), LoginPessoaFisicaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
  }

    public class CarregaDestaques extends AsyncTask<Localizacao, Void, String>{

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
                progress = new ProgressDialog(MainPessoaFisicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(MainPessoaFisicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Localizacao... params) {

            try {
                Gson gson = new Gson();
                String vv = gson.toJson(params[0]);
                String result = HttpMetods.POST("ProdutoDestaque/Buscar", gson.toJson(params[0]));
                return result;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s != null) {
                try {
                    JSONObject object = new JSONObject(s);

                    if(object.getBoolean("ok")) {

                        object.getJSONArray("");

                        List<Produto> listProdutos = new ArrayList<Produto>();
                        Produto produto = new Produto();

                        produto.setNome("Tenis Nike");
                        produto.setDescricao("Tenis da adidas preto");
                        produto.setPreco("R$:120,00");
                        listProdutos.add(produto);
                        produto.setNome("Tenis Nike");
                        produto.setDescricao("Tenis da Nike preto");
                        produto.setPreco("R$:120,00");
                        listProdutos.add(produto);

                        ListView listViewProdutos = (ListView) findViewById(R.id.listProdutosMainPessoaFisica);

                        ProdutosPessoaFisicaAdapter produtosPessoaFisicaAdapter = new ProdutosPessoaFisicaAdapter(MainPessoaFisicaActivity.this, R.layout.list_produto_pessoa_fisica, listProdutos);
                        listViewProdutos.setAdapter(produtosPessoaFisicaAdapter);
                    }else{
                        progress.dismiss();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(MainPessoaFisicaActivity.this, "Ocorreu um erro no servidor. Tente novamente!", Toast.LENGTH_LONG).show();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getApplicationContext(), LoginPessoaFisicaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }


        }


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

    private class produtosDestaque extends AsyncTask<Void, Void, String>{


        @Override
        protected String doInBackground(Void... params) {



            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_pesssoa_fisica, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.action_settings:
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_meusProdutos)
        {
            startActivity(new Intent(this,MeusProdutosPessoaFisicaActivity.class));
        }else if(id == R.id.nav_meusDados)
        {
            startActivity(new Intent(this,MeusDadosPessoaFisicaActivity.class));
        }else if(id == R.id.nav_categorias){
            startActivity(new Intent(this,CategoriasProdutosPessoaFisicaActivity.class));
        }else if(id == R.id.nav_Lojas){
            startActivity(new Intent(this, MapsLojasPertoActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void BuscarProduto(View v)
    {
        EditText editbuscaProduto;
        editbuscaProduto = (EditText) findViewById(R.id.editText9);

        Intent intent = new Intent(MainPessoaFisicaActivity.this, ProdutosPessoaFisicaActivity.class);
        startActivity(intent);
    }



    public void Sair(View v)
    {
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainPessoaFisicaActivity.this);
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

}
