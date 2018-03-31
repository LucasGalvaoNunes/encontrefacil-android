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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import opetbrothers.com.encontrefacil.Adapters.ProdutosEmDestaqueAdapter;
import opetbrothers.com.encontrefacil.Adapters.ProdutosPessoaFisicaAdapter;
import opetbrothers.com.encontrefacil.Model.FavoritosPessoaFisica;
import opetbrothers.com.encontrefacil.Model.Localizacao;
import opetbrothers.com.encontrefacil.Model.PessoaFisica;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.Model.ProdutoDestaque;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.PatternsUtil;
import opetbrothers.com.encontrefacil.Util.SwipeToRefreshListView;
import opetbrothers.com.encontrefacil.Util.SwipeToRefreshListener;
import opetbrothers.com.encontrefacil.Util.Util;

public class MainPessoaFisicaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener
{
    //region ATRIBUTES
    Localizacao minhaLocalizacao;       // Localizacao do dispositivo
    LocationManager locationManager;    // Atributo para pegar a localizacao.
    String provider;                    // Nome do provider para o LocationListener
    private AlertDialog alerta;
    //endregion
    PessoaFisica pessoaFisica;
    //region ANDROID METODS
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
        navigationView.setItemIconTintList(null);
        minhaLocalizacao = new Localizacao();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        //region ADICIONA AS INFORMACOES DA PESSOA NO NAV
        View header = navigationView.getHeaderView(0);
        TextView username = (TextView) header.findViewById(R.id.textViewNomePessoaFisicaLogada);
        ImageView imagePerfil = (ImageView) header.findViewById(R.id.imageViewPerfilFisica);

        String jsonPrefe = Util.RecuperarUsuario("pessoaFisica", MainPessoaFisicaActivity.this);
        Gson gson = new Gson();
        pessoaFisica = gson.fromJson(jsonPrefe, PessoaFisica.class);
        byte[] foto = Base64.decode(pessoaFisica.getFk_Pessoa().getFoto(), Base64.DEFAULT);
        username.setText(pessoaFisica.getFk_Pessoa().getNome() + " " + pessoaFisica.getFk_Pessoa().getSobrenome());
        Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
        imagePerfil.setImageBitmap(bitmap);
        //endregion

        //region INICIA A FUNCAO PARA FICAR RECEBENDO A LOCALIZACAO
        // Se o usuario permitiu o app de pegar a localizacao, entao pega.
        if(ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }

        //endregion

        //region PEGAR O LOCAL DO USUARIO E CHAMAR A FUNCAO DOS PRODUTOS EM DESTAQUE
        // Adiciona para o Location a ultima localizacao conhecida do dispositivo
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null)
        {
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
            new ProdutosEmDestaque().execute(minhaLocalizacao);
        }else{
            //region ALERT DIALOG BUILDER
            //Cria o gerador do AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainPessoaFisicaActivity.this);
            //define o titulo
            builder.setTitle("Aviso!");
            //define a mensagem
            builder.setMessage("Caso não tenha ativado seu GPS clique em ativar, se já clique em tentar novamente");
            //define um botão como positivo
            builder.setPositiveButton("Tente Novamente", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    new ProdutosEmDestaque().execute(minhaLocalizacao);
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


        //endregion

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

    @Override
    public void onLocationChanged(Location location) {
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
    //endregion

    //region WEB SERVICE
    private class ProdutosEmDestaque extends AsyncTask<Localizacao, Void, String> {

        //region ON PRE EXECUTE
        boolean isConnected = false;
        ProgressDialog progress;
        @Override
        protected void onPreExecute()
        {

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
        //endregion

        //region DO IN BACKGROUND E ON POST EXECUTE
        @Override
        protected String doInBackground(Localizacao... params) {
            Gson gson = new Gson();
            String produtos = HttpMetods.POST("ProdutoDestaque/Buscar",gson.toJson(params[0]));
            return produtos;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isConnected)
            {
                try{
                    JSONObject object = new JSONObject(s);
                    object.remove("type");
                    List<ProdutoDestaque> produtosEmDestaque = new ArrayList<ProdutoDestaque>();
                    if(object.getBoolean("ok"))
                    {
                        //region PEGA OS DADOS DO JSON
                        Gson gson = new Gson();
                        JSONArray produtos = object.getJSONArray("lista");

                        for(int i = 0; i < produtos.length(); i++)
                        {
                            produtos.getJSONObject(i).remove("type");
                            produtosEmDestaque.add(gson.fromJson(produtos.getJSONObject(i).toString(), ProdutoDestaque.class));
                        }
                        //endregion

                        //region SETA A LIST VIEW
                        Comparator decrescente = Collections.reverseOrder(new ComparatorProdutos());

                        Collections.sort(produtosEmDestaque,decrescente);

                        //endregion

                    }else{
                        Toast.makeText(MainPessoaFisicaActivity.this,"Não há produtos em destaque na sua região!",Toast.LENGTH_LONG).show();
                    }

                    SwipeToRefreshListView swipeListView = (SwipeToRefreshListView)findViewById(R.id.listProdutosMainPessoaFisica);
                    swipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        //            @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ProdutoDestaque produto = (ProdutoDestaque) parent.getItemAtPosition(position);
                            new PuxarProdutoEspecifico().execute(produto.getFk_produto().getId_Produto());
                        }
                    });
                    swipeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                                       int position, long id) {

                            ProdutoDestaque produto = (ProdutoDestaque) parent.getItemAtPosition(position);
                            FavoritosPessoaFisica fav = new FavoritosPessoaFisica(pessoaFisica,produto.getFk_produto());
                            new SalvarNosFavoritos().execute(fav);
                            return true;
                        }
                    });
                    ProdutosEmDestaqueAdapter destaques = new ProdutosEmDestaqueAdapter(
                            MainPessoaFisicaActivity.this,R.layout.list_produto_pessoa_fisica, produtosEmDestaque);

                    swipeListView.setAdapter(destaques);
                    swipeListView.setRefreshListener(new SwipeToRefreshListener() {
                        @Override
                        public void onRefresh() {
                            new ProdutosEmDestaque().execute(minhaLocalizacao);
                        }
                    });

                }catch (Exception e)
                {
                    Toast.makeText(MainPessoaFisicaActivity.this,"Não foi possivel se comunicar com o servidor, tente de novo mais tarde!",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }
        }
        //endregion

    }
    private class PuxarProdutoEspecifico extends AsyncTask<Integer, Void, String> {

        //region ON PRE EXECUTE
        boolean isConnected = false;
        ProgressDialog progress;
        @Override
        protected void onPreExecute()
        {

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
        //endregion

        //region DO IN BACKGROUND
        @Override
        protected String doInBackground(Integer... params) {
            String produtos = HttpMetods.GET("Produto/Get/" + params[0].toString());
            return produtos;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isConnected)
            {

                try{
                    JSONObject object = new JSONObject(s);
                    object.remove("type");
                    if(object.getBoolean("ok"))
                    {
                        Gson gson = new Gson();
                        String json = object.getJSONObject("produtoEntity").toString();
                        Produto produtoRecuperado = gson.fromJson(json, Produto.class);
                        Util.SalvarDados("produtoSelecionado", json, MainPessoaFisicaActivity.this);
                        Intent infProd = new Intent(MainPessoaFisicaActivity.this, InfProdPessoaFisicaActivity.class);
                        startActivity(infProd);
                    }
                }catch (Exception e)
                {
                    Toast.makeText(MainPessoaFisicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }

        }
        //endregion
    }

    private class SalvarNosFavoritos extends AsyncTask<FavoritosPessoaFisica, Void, String> {

        //region ON PRE EXECUTE
        boolean isConnected = false;
        ProgressDialog progress;
        @Override
        protected void onPreExecute()
        {

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
        //endregion

        //region DO IN BACKGROUND
        @Override
        protected String doInBackground(FavoritosPessoaFisica... params) {
            Gson gson = new Gson();
            String produtos = HttpMetods.POST("FavoritosPessoaFisica/Cadastrar",gson.toJson(params[0]));
            return produtos;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isConnected)
            {

                try{
                    JSONObject object = new JSONObject(s);
                    object.remove("type");
                    if(object.getBoolean("ok"))
                    {
                        Toast.makeText(MainPessoaFisicaActivity.this,"Salvo nos seus Favoritos!",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainPessoaFisicaActivity.this,"Este produto já esta em seus favoritos!",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e)
                {
                    Toast.makeText(MainPessoaFisicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }

        }
        //endregion
    }
    //endregion

    //region BUTTONS
    public void BuscarProduto(View v)
    {
        EditText editbuscaProduto = (EditText) findViewById(R.id.editTNomeProduto);

        Intent intent = new Intent(MainPessoaFisicaActivity.this, ProdutosPessoaFisicaActivity.class);
        intent.putExtra("nomeProduto", editbuscaProduto.getText().toString());
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
    //endregion
}
