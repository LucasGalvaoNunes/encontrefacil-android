package opetbrothers.com.encontrefacil.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import opetbrothers.com.encontrefacil.Adapters.AvaliacoesAdapter;
import opetbrothers.com.encontrefacil.Adapters.CategoriaProdutoAdapter;
import opetbrothers.com.encontrefacil.Adapters.ProdutosEmDestaqueAdapter;
import opetbrothers.com.encontrefacil.Model.Avaliacao_Produto;
import opetbrothers.com.encontrefacil.Model.Categoria_Produto;
import opetbrothers.com.encontrefacil.Model.Localizacao;
import opetbrothers.com.encontrefacil.Model.PessoaFisica;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.Model.ProdutoDestaque;
import opetbrothers.com.encontrefacil.Model.Route;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.DirectionFinder;
import opetbrothers.com.encontrefacil.Util.DirectionFinderListener;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.SwipeToRefreshListView;
import opetbrothers.com.encontrefacil.Util.SwipeToRefreshListener;
import opetbrothers.com.encontrefacil.Util.Util;

public class InfProdPessoaFisicaActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener,DirectionFinderListener {

    //region ATRIBUTES
    Location location;
    Localizacao minhaLocalizacao;       // Localizacao do dispositivo
    LocationManager locationManager;    // Atributo para pegar a localizacao.
    String provider;                    // Nome do provider para o LocationListener
    GoogleMap mMap;
    MapView mMapView;
    Gson gson = new Gson();
    String distanciaUserLoja;
    Produto produtoSelecionado;
    List<Marker> originMarkers = new ArrayList<>();
    List<Marker> destinationMarkers = new ArrayList<>();
    List<Polyline> polylinePaths = new ArrayList<>();
    ProgressDialog progressDialog;
    private AlertDialog alerta;
    //endregion
    EditText comentario;
    ListView Listavaliacoes;
    TextView notaMedia;
    RatingBar notaAvaliacao;
    AlertDialog dialogAvaliacoes;
    PessoaFisica pessoaFisica;
    //region ANDROID METODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inf_prod_pessoa_fisica);
        minhaLocalizacao = new Localizacao();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        //region PEGA O PRODUTO SELECIONADO
        produtoSelecionado = new Produto();
        String returnProduto = Util.RecuperarUsuario("produtoSelecionado", InfProdPessoaFisicaActivity.this);
        produtoSelecionado = gson.fromJson(returnProduto, Produto.class);
        //endregion

        //region ATRIBUTOS DA VIEW
        TextView txNomeProduto = (TextView) findViewById(R.id.textViewNomeProduto);
        TextView txDescricao = (TextView) findViewById(R.id.textViewDescricao);
        TextView txLoja = (TextView) findViewById(R.id.textViewLoja);
        pessoaFisica = new PessoaFisica();
        String jsonPrefe = Util.RecuperarUsuario("pessoaFisica", InfProdPessoaFisicaActivity.this);
        Gson gson = new Gson();
        pessoaFisica = gson.fromJson(jsonPrefe, PessoaFisica.class);
        //endregion

        //region SETA ATRIBUTOS DA VIEW
        txNomeProduto.setText(produtoSelecionado.getNome());
        txDescricao.setText(produtoSelecionado.getDescricao());
        txLoja.setText(produtoSelecionado.getFk_Pessoa_Juridica().getRazao_Social());
        //endregion

        //region INICIA MAPA
        mMapView = (MapView) findViewById(R.id.mapViewInfProd);

        if(mMapView != null)
        {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        //endregion

        //region INICIA A FUNCAO PARA FICAR RECEBENDO A LOCALIZACAO
        // Se o usuario permitiu o app de pegar a localizacao, entao pega.
        PegarRota();

        //endregion

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

                notaMedia = (TextView) mView.findViewById(R.id.mediaNota);
                Listavaliacoes = (ListView) mView.findViewById(R.id.listViewAvaliacao);
                notaAvaliacao = (RatingBar) mView.findViewById(R.id.ratingBarAvaliacao);
                comentario = (EditText) mView.findViewById(R.id.editTextComentario);
                ImageButton salvar = (ImageButton) mView.findViewById(R.id.imageButtonSalvarNota);
                salvar.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (comentario.getText().toString().length() == 0) {
                            comentario.setError("É obrigatorio!");
                            return;
                        }
                        if (notaAvaliacao.getProgress() == 0) {
                            Toast.makeText(InfProdPessoaFisicaActivity.this,"Coloque sua nota!",Toast.LENGTH_LONG).show();
                            return;
                        }
                        Avaliacao_Produto ava = new Avaliacao_Produto(produtoSelecionado,pessoaFisica,comentario.getText().toString(),notaAvaliacao.getProgress());
                        new AdicionarComentarios().execute(ava);
                    }
                });
                new Comentarios().execute(produtoSelecionado.getId_Produto());

                mBuilder.setView(mView);
                dialogAvaliacoes = mBuilder.create();
                dialogAvaliacoes.show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng loja = new LatLng(Double.valueOf(produtoSelecionado.getFk_Pessoa_Juridica().getFk_Localizacao().getLatitude()),
                Double.valueOf(produtoSelecionado.getFk_Pessoa_Juridica().getFk_Localizacao().getLongitude()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loja,15));
        Marker mark =  mMap.addMarker(new MarkerOptions()
                .title(produtoSelecionado.getFk_Pessoa_Juridica().getRazao_Social())
                .position(loja));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String tag = marker.getId();
                if(tag.contains("m0")){
                    Location loja = new Location("");
                    loja.setLatitude(Double.parseDouble(produtoSelecionado.getFk_Pessoa_Juridica().getFk_Localizacao().getLatitude()));
                    loja.setLongitude(Double.parseDouble(produtoSelecionado.getFk_Pessoa_Juridica().getFk_Localizacao().getLongitude()));

                    Float distancia = location.distanceTo(loja)/1000;
                    DecimalFormat df = new DecimalFormat("0.000");
                    distanciaUserLoja = df.format(distancia);

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(InfProdPessoaFisicaActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_inf_loja_produto, null);
                    TextView editNomeLoja = (TextView) mView.findViewById(R.id.editNomeLoja);
                    TextView editCategoriaLoja = (TextView) mView.findViewById(R.id.editCategoriaLoja);
                    TextView distanciaLoja = (TextView) mView.findViewById(R.id.editDistancia);
                    ImageView fotoLoja = (ImageView) mView.findViewById(R.id.imagemLoja);

                    editNomeLoja.setText(produtoSelecionado.getFk_Pessoa_Juridica().getRazao_Social());
                    editCategoriaLoja.setText(produtoSelecionado.getFk_Pessoa_Juridica().getFk_Categoria_Loja().getNome());
                    distanciaLoja.setText(distanciaUserLoja + " Km de distância");


                    byte[] foto = Base64.decode(produtoSelecionado.getFk_Pessoa_Juridica().getFk_Pessoa().getFoto(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
                    fotoLoja.setImageBitmap(bitmap);

                    mBuilder.setView(mView);
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }
                return false;
            }
        });
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
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

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Espera um momento.!",
                "Estamos traçando uma rota..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title("Minha posição")
                    .position(route.startLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }
    //endregion

    //region WEB SERVICE
    private class Comentarios extends AsyncTask<Integer, Void, String> {

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
                progress = new ProgressDialog(InfProdPessoaFisicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(InfProdPessoaFisicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }
        //endregion

        //region DO IN BACKGROUND E ON POST EXECUTE
        @Override
        protected String doInBackground(Integer... params) {
            Gson gson = new Gson();
            String produtos = HttpMetods.GET("AvaliacaoProduto/AvaliacaoPorProduto/" + params[0]);
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
                        //region PEGA OS DADOS DO JSON
                        Gson gson = new Gson();
                        JSONArray avaliacoesJson = object.getJSONArray("lista");
                        List<Avaliacao_Produto> avaliacoes = new ArrayList<Avaliacao_Produto>();
                        for(int i = 0; i < avaliacoesJson.length(); i++)
                        {
                            avaliacoesJson.getJSONObject(i).remove("type");
                            avaliacoes.add(gson.fromJson(avaliacoesJson.getJSONObject(i).toString(), Avaliacao_Produto.class));
                        }
                        int nota = 0;
                        for(Avaliacao_Produto ava : avaliacoes)
                        {
                            nota += ava.getNota();
                        }
                        notaMedia.setText("Nota Média: " + nota / avaliacoes.size());
                        //endregion

                        //region SETA A LIST VIEW
                        AvaliacoesAdapter produtosPessoaJuridicaAdapter = new AvaliacoesAdapter(InfProdPessoaFisicaActivity.this, R.layout.list_avaliacoes_prod, avaliacoes);
                        Listavaliacoes.setAdapter(produtosPessoaJuridicaAdapter);
                        //endregion

                    }else{
                        Toast.makeText(InfProdPessoaFisicaActivity.this,"Nenhum comentario para este produto!",Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e)
                {
                    Toast.makeText(InfProdPessoaFisicaActivity.this,"Não foi possivel se comunicar com o servidor, tente de novo mais tarde!",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }
        }
        //endregion

    }
    private class AdicionarComentarios extends AsyncTask<Avaliacao_Produto, Void, String> {

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
                progress = new ProgressDialog(InfProdPessoaFisicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(InfProdPessoaFisicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }
        //endregion

        //region DO IN BACKGROUND E ON POST EXECUTE
        @Override
        protected String doInBackground(Avaliacao_Produto... params) {
            Gson gson = new Gson();
            String produtos = HttpMetods.POST("AvaliacaoProduto/Cadastrar", gson.toJson(params[0]));
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
                        Toast.makeText(InfProdPessoaFisicaActivity.this,"Comentario Adicionado!",Toast.LENGTH_LONG).show();
                        dialogAvaliacoes.cancel();

                    }else{
                        Toast.makeText(InfProdPessoaFisicaActivity.this,"Você já adicionou um comentario!",Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e)
                {
                    Toast.makeText(InfProdPessoaFisicaActivity.this,"Não foi possivel se comunicar com o servidor, tente de novo mais tarde!",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }
        }
        //endregion

    }
    //endregion

    //region OTHER METODS
    public void PegarRota(){
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
            ImageButton btnChamaNavegacao = (ImageButton) findViewById(R.id.btnChamaNav);
            btnChamaNavegacao.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    callExternalMap(minhaLocalizacao.getLatitude() + "+" + minhaLocalizacao.getLongitude(),
                            produtoSelecionado.getFk_Pessoa_Juridica().getFk_Localizacao().getLatitude() + "," +
                                    produtoSelecionado.getFk_Pessoa_Juridica().getFk_Localizacao().getLongitude());
                }
            });

            //region INICIA A ROTA
            String urlOrigin = minhaLocalizacao.getLatitude() + "+" + minhaLocalizacao.getLongitude();
            String urlDestination = produtoSelecionado.getFk_Pessoa_Juridica().getFk_Localizacao().getLatitude() + "+" +
                    produtoSelecionado.getFk_Pessoa_Juridica().getFk_Localizacao().getLongitude();
            try{
                new DirectionFinder(this,urlOrigin,urlDestination).execute();
            }catch (Exception e)
            {

            }
            //endregion
        }else{
            //region ALERT DIALOG BUILDER
            //Cria o gerador do AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(InfProdPessoaFisicaActivity.this);
            //define o titulo
            builder.setTitle("Aviso!");
            //define a mensagem
            builder.setMessage("Caso não tenha ativado seu GPS clique em ativar, se já clique em tentar novamente");
            //define um botão como positivo
            builder.setPositiveButton("Tente Novamente", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    PegarRota();
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
    private void callExternalMap(String origem, String destino) {
        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + origem + "&daddr=" + destino));

        /*
        * Se você quiser que o usuário vá direto para o aplicativo do Google Maps, use a linha abaixo.
        * Caso não queira (de opções para o usuário abrir em outros aplicativos de mapas no celular), apenas apague a linha abaixo.
        */
            //intent.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"));

            startActivity(intent);
        } catch (Exception ex) {
            Toast.makeText(this, "Verifique se o Google Maps está instalado em seu dispositivo", Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

}