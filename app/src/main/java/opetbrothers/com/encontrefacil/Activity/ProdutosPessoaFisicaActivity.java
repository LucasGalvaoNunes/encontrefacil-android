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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import opetbrothers.com.encontrefacil.Model.Localizacao;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class ProdutosPessoaFisicaActivity extends AppCompatActivity implements LocationListener{

    int idCategoria;
    ArrayList<Produto> listProdutos = new ArrayList<>();
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos_pessoa_fisica);

        Bundle extras = getIntent().getExtras();
        idCategoria = extras.getInt("idCategoria");

        listaProdutos();
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

            case R.id.filtro_PorAvaliacao:
                Toast.makeText(this, "Você clicou em avaliação!", Toast.LENGTH_LONG).show();


                break;

            case R.id.filtro_PorKm:
                Toast.makeText(this, "Você clicou em KM!", Toast.LENGTH_LONG).show();

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProdutosPessoaFisicaActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_filtro_distancia_pessoa_fisica, null);

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

                break;

            case R.id.filtro_Preco_Crescente:

                Collections.sort(listProdutos, new Comparator<Produto>() {
                    @Override
                    public int compare(Produto o1, Produto o2) {
                        return o1.getPreco().compareTo(o2.getPreco());
                    }
                });

                ListView listView = (ListView) findViewById(R.id.listViewProdutosPessoaFisica);

                ProdutosPessoaFisicaAdapter sortProdutosPessoaFisicaAdapter = new ProdutosPessoaFisicaAdapter(ProdutosPessoaFisicaActivity.this, R.layout.list_produtos_pessoa_juridica, listProdutos);
                listView.setAdapter(sortProdutosPessoaFisicaAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Produto produtoSelecionado = (Produto) parent.getItemAtPosition(position);
                        Util.SalvarDados("produtoSelecionado", gson.toJson(produtoSelecionado), ProdutosPessoaFisicaActivity.this);
                        Intent i = new Intent(ProdutosPessoaFisicaActivity.this, InfProdPessoaFisicaActivity.class);
                        startActivity(i);
                    }
                });

                break;

            case R.id.filtro_Preco_Decrescente:

                Collections.sort(listProdutos, new Comparator<Produto>() {
                    @Override
                    public int compare(Produto o1, Produto o2) {
                        return -o1.getPreco().compareTo(o2.getPreco());
                    }
                });

                ListView listViewDesc = (ListView) findViewById(R.id.listViewProdutosPessoaFisica);

                ProdutosPessoaFisicaAdapter DescProdutosPessoaFisicaAdapter = new ProdutosPessoaFisicaAdapter(ProdutosPessoaFisicaActivity.this, R.layout.list_produtos_pessoa_juridica, listProdutos);
                listViewDesc.setAdapter(DescProdutosPessoaFisicaAdapter);

                listViewDesc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Produto produtoSelecionado = (Produto) parent.getItemAtPosition(position);
                        Util.SalvarDados("produtoSelecionado", gson.toJson(produtoSelecionado), ProdutosPessoaFisicaActivity.this);
                        Intent i = new Intent(ProdutosPessoaFisicaActivity.this, InfProdPessoaFisicaActivity.class);
                        startActivity(i);
                    }
                });

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void confirmaFiltroKN(View v){

        EditText editDistancia = (EditText) v.findViewById(R.id.editDistanciaMaxima);

        ArrayList<Produto> produtosFiltroKM = new ArrayList<>();
        String distanciaDigitada = editDistancia.getText().toString();

        Location location = instanciaLocation();
        Location localLoja = new Location("");

        for(int i = 0; i == listProdutos.size(); i++){
            String distanciaAteProduto;

            localLoja.setLatitude(Double.parseDouble(listProdutos.get(i).getFk_Pessoa_Juridica().getFk_Localizacao().getLatitude()));
            localLoja.setLongitude(Double.parseDouble(listProdutos.get(i).getFk_Pessoa_Juridica().getFk_Localizacao().getLongitude()));

            Float returnDistancia = location.distanceTo(localLoja);

            DecimalFormat df = new DecimalFormat("0.000");
            distanciaAteProduto = df.format(returnDistancia);

            Double distDigitada = Double.parseDouble(distanciaDigitada);
            Double distAteProduto = Double.parseDouble(distanciaAteProduto);

            if(distAteProduto < distDigitada){

                produtosFiltroKM.add(listProdutos.get(i));
            }

        }

        ListView listView = (ListView) findViewById(R.id.listViewProdutosPessoaFisica);

        ProdutosPessoaFisicaAdapter kmProdutosPessoaFisicaAdapter = new ProdutosPessoaFisicaAdapter(ProdutosPessoaFisicaActivity.this, R.layout.list_produtos_pessoa_juridica, produtosFiltroKM);
        listView.setAdapter(kmProdutosPessoaFisicaAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Produto produtoSelecionado = (Produto) parent.getItemAtPosition(position);
                Util.SalvarDados("produtoSelecionado", gson.toJson(produtoSelecionado), ProdutosPessoaFisicaActivity.this);
                Intent i = new Intent(ProdutosPessoaFisicaActivity.this, InfProdPessoaFisicaActivity.class);
                startActivity(i);
            }
        });

    }

    public Location instanciaLocation(){
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

        return location;
    }

    public void listaProdutos(){

        Location location = instanciaLocation();

        try {

            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses;
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            Localizacao local = new Localizacao();

            local.setEstado(addresses.get(0).getAdminArea());
            local.setCidade(addresses.get(0).getLocality());

            new getProdutosbyCategoria().execute(local);

        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(ProdutosPessoaFisicaActivity.this, "Ocorreu um erro no servidor. Tente novamente!", Toast.LENGTH_LONG).show();
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(getApplicationContext(), LoginPessoaFisicaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public class getProdutosbyCategoria extends AsyncTask<Localizacao, Void, String>{

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

        @Override
        protected String doInBackground(Localizacao... params) {

            try {
                Gson gson = new Gson();
                String cc =  gson.toJson(params[0]);
                String result = HttpMetods.POST("Produto/BuscarPorCategoria/" + idCategoria, gson.toJson(params[0]));
                return result;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);

            if(isConnected) {
                if (s != null) {
                    try {
                        JSONObject object = new JSONObject(s);
                        JSONArray listProdutosJson = object.getJSONArray("lista");


                        for (int i = 0; i < listProdutosJson.length(); i++) {
                            JSONObject objProduto = listProdutosJson.getJSONObject(i);
                            objProduto.remove("type");
                            Produto produto = gson.fromJson(objProduto.toString(), Produto.class);

                            listProdutos.add(produto);
                        }

                        if (listProdutos.size() > 0) {
                            ListView listView = (ListView) findViewById(R.id.listViewProdutosPessoaFisica);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                //            @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Produto produtoSelecionado = (Produto) parent.getItemAtPosition(position);
                                    Util.SalvarDados("produtoSelecionado", gson.toJson(produtoSelecionado), ProdutosPessoaFisicaActivity.this);
                                    Intent i = new Intent(ProdutosPessoaFisicaActivity.this, InfProdPessoaFisicaActivity.class);
                                    startActivity(i);
                                }
                            });
                            ProdutosPessoaFisicaAdapter produtosPessoaFisicaAdapter = new ProdutosPessoaFisicaAdapter(ProdutosPessoaFisicaActivity.this, R.layout.list_produtos_pessoa_juridica, listProdutos);
                            listView.setAdapter(produtosPessoaFisicaAdapter);

                            progress.dismiss();
                        } else {
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

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProdutosPessoaFisicaActivity.this);
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
