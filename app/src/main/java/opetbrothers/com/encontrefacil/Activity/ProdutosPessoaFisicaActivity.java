package opetbrothers.com.encontrefacil.Activity;

import android.Manifest;
import android.app.Activity;
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
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import opetbrothers.com.encontrefacil.Adapters.ProdutosPessoaFisicaAdapter;
import opetbrothers.com.encontrefacil.Model.FavoritosPessoaFisica;
import opetbrothers.com.encontrefacil.Model.Localizacao;
import opetbrothers.com.encontrefacil.Model.PessoaFisica;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.Model.ProdutoDestaque;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.DirectionFinder;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class ProdutosPessoaFisicaActivity extends AppCompatActivity implements LocationListener{
    Location location;
    Localizacao minhaLocalizacao;       // Localizacao do dispositivo
    LocationManager locationManager;    // Atributo para pegar a localizacao.
    String provider;                    // Nome do provider para o LocationListener
    int idCategoria;
    String nomeProduto;
    List<Produto> listProdutos = new ArrayList<Produto>();
    Gson gson = new Gson();
    private AlertDialog alerta;
    ListView listViewProdutos;
    EditText editDistancia;
    PessoaFisica pessoaFisica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos_pessoa_fisica);
        minhaLocalizacao = new Localizacao();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        listViewProdutos = (ListView) findViewById(R.id.listViewProdutosPessoaFisica);
        listViewProdutos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Produto produtoSelecionado = (Produto) parent.getItemAtPosition(position);
                Util.SalvarDados("produtoSelecionado", gson.toJson(produtoSelecionado), ProdutosPessoaFisicaActivity.this);
                Intent i = new Intent(ProdutosPessoaFisicaActivity.this, InfProdPessoaFisicaActivity.class);
                startActivity(i);
            }
        });
        listViewProdutos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                Produto produto = (Produto) parent.getItemAtPosition(position);
                FavoritosPessoaFisica fav = new FavoritosPessoaFisica(pessoaFisica,produto);
                new SalvarNosFavoritos().execute(fav);
                return true;
            }
        });
        Bundle extras = getIntent().getExtras();
        idCategoria = extras.getInt("idCategoria");
        nomeProduto = extras.getString("nomeProduto");
        String jsonPrefe = Util.RecuperarUsuario("pessoaFisica", ProdutosPessoaFisicaActivity.this);
        Gson gson = new Gson();
        pessoaFisica = gson.fromJson(jsonPrefe, PessoaFisica.class);
        PegarLocalizacao(nomeProduto,idCategoria);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.meus_produtos_pessoa_fisica,menu);
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

            case R.id.filtro_PorKm:
                //region POR KM
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProdutosPessoaFisicaActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_filtro_distancia_pessoa_fisica, null);
                ImageButton btConfirmaKM = (ImageButton) mView.findViewById((R.id.btConfirmaDistancia));

                editDistancia = (EditText) mView.findViewById(R.id.editDistanciaMaxima);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                String listProd = Util.RecuperarUsuario("listaProdutosCategoria", ProdutosPessoaFisicaActivity.this);
                if(listProd != null)
                {
                    try {
                        JSONObject object = new JSONObject(listProd);
                        JSONArray listProdutosJson = object.getJSONArray("lista");

                        listProdutos.clear();

                        for (int i = 0; i < listProdutosJson.length(); i++) {
                            JSONObject objProduto = listProdutosJson.getJSONObject(i);
                            objProduto.remove("type");
                            Produto produto = gson.fromJson(objProduto.toString(), Produto.class);

                            listProdutos.add(produto);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                btConfirmaKM.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        String distInserida = editDistancia.getText().toString();
                        distInserida = distInserida.replace(",",".");

                        if(distInserida.substring(0,1).equals(".") || distInserida.contains("-") || distInserida.contains(" ")){
                            editDistancia.setError("Digite um valor válido!");
                            return;
                        }

                        distInserida = distInserida.replace("-","");
                        Location localLoja = new Location("");
                        double radius = Double.parseDouble(distInserida); // Raio em metros.
                        List<Produto> novaLista = new ArrayList<Produto>();
                        for(Produto p : listProdutos)
                        {
                            localLoja.setLatitude(Double.parseDouble(p.getFk_Pessoa_Juridica().getFk_Localizacao().getLatitude()));
                            localLoja.setLongitude(Double.parseDouble(p.getFk_Pessoa_Juridica().getFk_Localizacao().getLongitude()));

                            float distance = location.distanceTo(localLoja)/1000; // recebe distância entre os dois pontos.
                            if (distance < radius)
                            {
                                novaLista.add(p);
                            }
                        }
                        listProdutos = novaLista;
                        if(listProdutos.size() == 0)
                        {
                            Toast.makeText(ProdutosPessoaFisicaActivity.this, "Nenhum produto localizado no KM digitado", Toast.LENGTH_SHORT).show();
                        }
                        ProdutosPessoaFisicaAdapter kmProdutosPessoaFisicaAdapter = new ProdutosPessoaFisicaAdapter(ProdutosPessoaFisicaActivity.this, R.layout.list_produtos_pessoa_juridica, listProdutos);
                        listViewProdutos.setAdapter(kmProdutosPessoaFisicaAdapter);

                        dialog.dismiss();
                    }
                });
                //endregion
                break;

            case R.id.filtro_Preco_Crescente:
                //region CRESCENTE
                Collections.sort(listProdutos, new Comparator<Produto>() {
                    @Override
                    public int compare(Produto o1, Produto o2) {
                        return o1.getPreco().compareTo(o2.getPreco());
                    }
                });
                ProdutosPessoaFisicaAdapter sortProdutosPessoaFisicaAdapter = new ProdutosPessoaFisicaAdapter(ProdutosPessoaFisicaActivity.this, R.layout.list_produtos_pessoa_juridica, listProdutos);
                listViewProdutos.setAdapter(sortProdutosPessoaFisicaAdapter);


                //endregion
                break;

            case R.id.filtro_Preco_Decrescente:
                //region DECRESCENTE
                Collections.sort(listProdutos, new Comparator<Produto>() {
                    @Override
                    public int compare(Produto o1, Produto o2) {
                        return -o1.getPreco().compareTo(o2.getPreco());
                    }
                });



                ProdutosPessoaFisicaAdapter DescProdutosPessoaFisicaAdapter = new ProdutosPessoaFisicaAdapter(ProdutosPessoaFisicaActivity.this, R.layout.list_produtos_pessoa_juridica, listProdutos);
                listViewProdutos.setAdapter(DescProdutosPessoaFisicaAdapter);


                //endregion
                break;

            case R.id.filtro_nenhum:
                //region TIRAR FILTROS
                String listJ = Util.RecuperarUsuario("listaProdutosCategoria", ProdutosPessoaFisicaActivity.this);
                if(listJ != null)
                {
                    try {
                        JSONObject object = new JSONObject(listJ);
                        JSONArray listProdutosJson = object.getJSONArray("lista");

                        listProdutos.clear();

                        for (int i = 0; i < listProdutosJson.length(); i++) {
                            JSONObject objProduto = listProdutosJson.getJSONObject(i);
                            objProduto.remove("type");
                            Produto produto = gson.fromJson(objProduto.toString(), Produto.class);

                            listProdutos.add(produto);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                ProdutosPessoaFisicaAdapter NenhumProdutosPessoaFisicaAdapter = new ProdutosPessoaFisicaAdapter(ProdutosPessoaFisicaActivity.this, R.layout.list_produtos_pessoa_juridica, listProdutos);
                listViewProdutos.setAdapter(NenhumProdutosPessoaFisicaAdapter);


                //endregion
                break;

        }
        return super.onOptionsItemSelected(item);
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

    //region WEB SERVICE
    public class BuscarProdutoPelaCategoria extends AsyncTask<Localizacao, Void, String>{

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
                progress = new ProgressDialog(ProdutosPessoaFisicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(ProdutosPessoaFisicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }
        //endregion

        //region DO IN BACKGROUN
        @Override
        protected String doInBackground(Localizacao... params) {
            Gson gson = new Gson();
            String result = HttpMetods.POST("Produto/BuscarPorCategoria/" + idCategoria, gson.toJson(params[0]));
            return result;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);

            if(isConnected) {
                if (s != null) {
                    try {
                        JSONObject object = new JSONObject(s);

                        if(object.getBoolean("ok"))
                        {
                            Util.SalvarDados("listaProdutosCategoria",object.toString(), ProdutosPessoaFisicaActivity.this);
                            JSONArray listProdutosJson = object.getJSONArray("lista");
                            for (int i = 0; i < listProdutosJson.length(); i++) {
                                JSONObject objProduto = listProdutosJson.getJSONObject(i);
                                objProduto.remove("type");
                                Produto produto = gson.fromJson(objProduto.toString(), Produto.class);
                                listProdutos.add(produto);
                            }
                            if (listProdutos.size() > 0) {


                                ProdutosPessoaFisicaAdapter produtosPessoaFisicaAdapter = new ProdutosPessoaFisicaAdapter(ProdutosPessoaFisicaActivity.this, R.layout.list_produto_pessoa_fisica, listProdutos);
                                listViewProdutos.setAdapter(produtosPessoaFisicaAdapter);

                            }
                        }
                        else {
                            Util.SalvarDados("listaProdutosCategoria",null, ProdutosPessoaFisicaActivity.this);
                            Toast.makeText(ProdutosPessoaFisicaActivity.this, "Nenhum produto nessa categoria.", Toast.LENGTH_LONG).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(ProdutosPessoaFisicaActivity.this, "Ocorreu um erro no servidor.", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(ProdutosPessoaFisicaActivity.this, "Verifique a sua conexão...", Toast.LENGTH_LONG).show();
            }
            progress.dismiss();
        }
        //endregion
    }
    public class BuscarProdutoPorNome extends AsyncTask<Localizacao, Void, String>{

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
                progress = new ProgressDialog(ProdutosPessoaFisicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(ProdutosPessoaFisicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }
        //endregion

        //region DO IN BACKGROUN
        @Override
        protected String doInBackground(Localizacao... params) {
            Gson gson = new Gson();
            String result = HttpMetods.POST("Produto/BuscarPorNome/" + nomeProduto.replace(" ", "_"), gson.toJson(params[0]));
            return result;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);

            if(isConnected) {
                if (s != null) {
                    try {
                        JSONObject object = new JSONObject(s);
                        if(object.getBoolean("ok"))
                        {
                            Util.SalvarDados("listaProdutosCategoria",object.toString(), ProdutosPessoaFisicaActivity.this);
                            JSONArray listProdutosJson = object.getJSONArray("lista");
                            for (int i = 0; i < listProdutosJson.length(); i++) {
                                JSONObject objProduto = listProdutosJson.getJSONObject(i);
                                objProduto.remove("type");
                                Produto produto = gson.fromJson(objProduto.toString(), Produto.class);
                                listProdutos.add(produto);
                            }

                            if (listProdutos.size() > 0) {

                                ProdutosPessoaFisicaAdapter produtosPessoaFisicaAdapter = new ProdutosPessoaFisicaAdapter(ProdutosPessoaFisicaActivity.this, R.layout.list_produto_pessoa_fisica, listProdutos);
                                listViewProdutos.setAdapter(produtosPessoaFisicaAdapter);

                            }
                        }
                        else {
                            Toast.makeText(ProdutosPessoaFisicaActivity.this, "Nenhum produto encontrado para este nome!.", Toast.LENGTH_LONG).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(ProdutosPessoaFisicaActivity.this, "Ocorreu um erro no servidor.", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(ProdutosPessoaFisicaActivity.this, "Verifique a sua conexão...", Toast.LENGTH_LONG).show();
            }
            progress.dismiss();
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
                progress = new ProgressDialog(ProdutosPessoaFisicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(ProdutosPessoaFisicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProdutosPessoaFisicaActivity.this,"Salvo nos seus Favoritos!",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(ProdutosPessoaFisicaActivity.this,"Este produto já esta em seus favoritos!",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e)
                {
                    Toast.makeText(ProdutosPessoaFisicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }

        }
        //endregion
    }
    //endregion


    public void PegarLocalizacao(String pNome, int pCategoria){
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
            if(pCategoria != 0 && pNome == null)
            {
                new BuscarProdutoPelaCategoria().execute(minhaLocalizacao);
            }
            if(pNome != null && pCategoria == 0)
            {
                new BuscarProdutoPorNome().execute(minhaLocalizacao);
            }

            //endregion
        }else{
            //region ALERT DIALOG BUILDER
            //Cria o gerador do AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(ProdutosPessoaFisicaActivity.this);
            //define o titulo
            builder.setTitle("Aviso!");
            //define a mensagem
            builder.setMessage("Caso não tenha ativado seu GPS clique em ativar, se já clique em tentar novamente");
            //define um botão como positivo
            builder.setPositiveButton("Tente Novamente", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    PegarLocalizacao(nomeProduto,idCategoria);
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
}
