package opetbrothers.com.encontrefacil.Activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import opetbrothers.com.encontrefacil.Adapters.ProdutosEmDestaqueAdapter;
import opetbrothers.com.encontrefacil.Model.Localizacao;
import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.Model.ProdutoDestaque;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.SwipeToRefreshListView;
import opetbrothers.com.encontrefacil.Util.SwipeToRefreshListener;
import opetbrothers.com.encontrefacil.Util.Util;

public class MainPessoaJuridicaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    //region ATRIBUTOS
    PessoaJuridica pessoaJuridica;      // Pessoa juridica logada
    Localizacao minhaLocalizacao;       // Localizacao do dispositivo
    LocationManager locationManager;    // Atributo para pegar a localizacao.
    String provider;                    // Nome do provider para o LocationListener
    private AlertDialog alerta;
    //endregion

    //region ANDROID METODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //region PADRAO DA VIEW
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pessoa_juridica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cadastraProduto = new Intent(MainPessoaJuridicaActivity.this,CadastroProdutoPessoaJuridicaActivity.class);
                startActivity(cadastraProduto);
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        //endregion

        //region INSTANCIAS
        TextView username = (TextView) header.findViewById(R.id.textViewLojaNome);
        ImageView imageLoja = (ImageView) header.findViewById(R.id.imageLojaMain);

        minhaLocalizacao = new Localizacao();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        //endregion

        //region SETA NOME E FOTO DO USUARIO LOGADO
        // Pega as informacoes da pessoa juridica vindas do cache do dispositivo.
        String jsonPrefe = Util.RecuperarUsuario("pessoaJuridica", MainPessoaJuridicaActivity.this);
        Gson gson = new Gson();
        // Transforma o json em objeto
        pessoaJuridica = gson.fromJson(jsonPrefe, PessoaJuridica.class);
        //  Verifica se tem foto para ser mostrada
        if(pessoaJuridica.getFk_Pessoa().getFoto() != null)
        {
            // Transforma o string Base64 em bytes
            byte[] foto = Base64.decode(pessoaJuridica.getFk_Pessoa().getFoto(), Base64.DEFAULT);
            // Seta a imagem para o perfil logado
            Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
            imageLoja.setImageBitmap(bitmap);
        }
        username.setText(pessoaJuridica.getRazao_Social());
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
            //Cria o gerador do AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainPessoaJuridicaActivity.this);
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
        getMenuInflater().inflate(R.menu.main_pessoa_juridica, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (item.getItemId())
        {
            case R.id.nav_meusProdutos:
                startActivity(new Intent(MainPessoaJuridicaActivity.this,MeusProdutosPessoaJuridicaActivity.class));
                return true;
            case R.id.nav_meusDados:
                startActivity(new Intent(MainPessoaJuridicaActivity.this,MeusDadosPessoaJuridicaActivity.class));
                return true;
            case R.id.nav_relatorio:
                startActivity(new Intent(MainPessoaJuridicaActivity.this,RelatorioPessoaJuridicaActivity.class));
                return true;
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
                progress = new ProgressDialog(MainPessoaJuridicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(MainPessoaJuridicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainPessoaJuridicaActivity.this,"Não há produtos em destaque na sua região!",Toast.LENGTH_LONG).show();

                    }
                    ProdutosEmDestaqueAdapter destaques = new ProdutosEmDestaqueAdapter(
                            MainPessoaJuridicaActivity.this,R.layout.list_produto_pessoa_fisica, produtosEmDestaque);

                    SwipeToRefreshListView swipeListView = (SwipeToRefreshListView)findViewById(R.id.listProdutosMainPessoaJuridica);
                    swipeListView.setAdapter(destaques);

                    swipeListView.setRefreshListener(new SwipeToRefreshListener() {
                        @Override
                        public void onRefresh() {
                            new ProdutosEmDestaque().execute(minhaLocalizacao);
                        }
                    });

                }catch (Exception e)
                {
                    Toast.makeText(MainPessoaJuridicaActivity.this,"Não foi possivel se comunicar com o servidor, tente de novo mais tarde!",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }
        }
        //endregion

    }
    //endregion

    //region BUTTON METODS
    public void LogoffPessoaJuridica(View v)
    {
        Util.SalvarDados("pessoaJuridica","", MainPessoaJuridicaActivity.this);
        Intent login = new Intent(MainPessoaJuridicaActivity.this,LoginActivity.class);
        startActivity(login);
        finish();
    }
    //endregion

}
class ComparatorProdutos implements Comparator<ProdutoDestaque> {
    public int compare(ProdutoDestaque c1,ProdutoDestaque c2) {
        return c1.getExposicao() < c2.getExposicao() ? -1 : (c1.getExposicao() > c2.getExposicao() ? +1 : 0);
    }
}
