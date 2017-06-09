package opetbrothers.com.encontrefacil.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import opetbrothers.com.encontrefacil.Model.Categoria_Loja;
import opetbrothers.com.encontrefacil.Model.Localizacao;
import opetbrothers.com.encontrefacil.Model.Pessoa;
import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class CadastroPessoaJuridicaActivity extends AppCompatActivity implements LocationListener {

    //region ATRIBUTOS DA VIEW
    ImageView imagemLoja;
    ImageButton btnTirarFoto;
    Spinner spinnerCategoriasLoja;
    EditText editRazaoSocial;
    EditText editEmail;
    EditText editNome;
    EditText editSobrenome;
    EditText editSenha;
    EditText editTelefone;
    EditText editCnpj;
    Button btnSalvar;
    //endregion
    LocationManager locationManager;
    String provider;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private List<Categoria_Loja> categorias;
    PessoaJuridica juridica;

    //region Android Metods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_pessoa_juridica);
        juridica = new PessoaJuridica();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        if(ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
        imagemLoja = (ImageView) findViewById(R.id.imageLojaCadastro);
        spinnerCategoriasLoja = (Spinner) findViewById(R.id.spinnerCategoriasLoja);
        editRazaoSocial = (EditText) findViewById(R.id.editRazaoSocial);
        editEmail =(EditText) findViewById(R.id.editEmail);
        editNome = (EditText) findViewById(R.id.editNome);
        editSobrenome = (EditText) findViewById(R.id.editSobrenome);
        editSenha = (EditText) findViewById(R.id.editSenha);
        editTelefone = (EditText) findViewById(R.id.editTelefone);
        editCnpj = (EditText) findViewById(R.id.editCnpj);
        categorias = new ArrayList<Categoria_Loja>();

        new PegarCategoriaLojaServidor().execute();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            // Recorta a imagem em 125x125
            Bitmap imageBitmap = ThumbnailUtils.extractThumbnail((Bitmap) extras.get("data"),125,125,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            imagemLoja.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Localizacao loca = new Localizacao();
        loca.setLatitude(String.valueOf(location.getLatitude()));
        loca.setLongitude(String.valueOf(location.getLongitude()));
        juridica.setFk_Localizacao(loca);

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


    //region CONEXOES COM O SERVIDOR
    private class PegarCategoriaLojaServidor extends AsyncTask<Void, Void, String> {
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
                progress = new ProgressDialog(CadastroPessoaJuridicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(CadastroPessoaJuridicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String categorais = HttpMetods.GET("CategoriaLoja/Todas");
            return categorais;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isConnected)
            {
                try{
                    JSONObject object = new JSONObject(s);
                    object.remove("type");
                    Gson gson = new Gson();
                    if(object.getBoolean("ok"))
                    {
                        JSONArray categoriasArray = object.getJSONArray("lista");
                        for(int i =0; i < categoriasArray.length(); i++)
                        {
                            categoriasArray.getJSONObject(i).remove("type");
                            String json = categoriasArray.getJSONObject(i).toString();
                            Categoria_Loja categoriaLoja = gson.fromJson(json,Categoria_Loja.class);
                            categorias.add(categoriaLoja);
                        }
                        ArrayAdapter<Categoria_Loja> arrayAdapter = new ArrayAdapter<Categoria_Loja>(CadastroPessoaJuridicaActivity.this,R.layout.support_simple_spinner_dropdown_item,categorias);
                        ArrayAdapter<Categoria_Loja> spinnerArrayAdapter = arrayAdapter;
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                        spinnerCategoriasLoja.setAdapter(spinnerArrayAdapter);
                    }


                }catch (Exception e)
                {
                    Toast.makeText(CadastroPessoaJuridicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }

        }
    }

    private class SalvarDados extends AsyncTask<PessoaJuridica, Void, String> {
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
                progress = new ProgressDialog(CadastroPessoaJuridicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(CadastroPessoaJuridicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(PessoaJuridica... params) {
            Gson gson = new Gson();
            String juridica = HttpMetods.POST("PessoaJuridica/Cadastrar",gson.toJson(params[0]));
            return juridica;
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
                        String json = object.getJSONObject("pessoaJuridica").toString();
                        PessoaJuridica pessoaJuridica = gson.fromJson(json, PessoaJuridica.class);

                        Intent verificarEmail = new Intent(CadastroPessoaJuridicaActivity.this, VerificacaoEmailActivity.class);
                        Util.SalvarDados("pessoaJuridica",object.getJSONObject("pessoaJuridica").toString(), CadastroPessoaJuridicaActivity.this);
                        startActivity(verificarEmail);
                        finish();
                    }else{
                        editEmail.setError(object.getString("mensagem"));
                    }


                }catch (Exception e)
                {
                    Toast.makeText(CadastroPessoaJuridicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();


            }

        }
    }
    //endregion


    //region BUTTON CLICKS
    public void Registrar(View v)
    {
        //region VALIDAÇÃO
        if (editRazaoSocial.getText().toString().length() == 0) {
            editRazaoSocial.setError("Razão Social é obrigatorio!");
            return;
        }
        if (editCnpj.getText().toString().length() == 0) {
            editCnpj.setError("CNPJ é obrigatorio!");
            return;
        }
        if (editEmail.getText().toString().length() == 0) {
            editEmail.setError("Email é obrigatorio!");
            return;
        }
        if (editTelefone.getText().toString().length() == 0) {
            editTelefone.setError("Telefone é obrigatorio!");
            return;
        }
        if (editNome.getText().toString().length() == 0) {
            editNome.setError("Nome é obrigatorio!");
            return;
        }
        if (editSobrenome.getText().toString().length() == 0) {
            editSobrenome.setError("SobreNome é obrigatorio!");
            return;
        }
        if (editSenha.getText().toString().length() == 0) {
            editSenha.setError("Senha é obrigatorio!");
            return;
        }


        //endregion
        imagemLoja.buildDrawingCache();
        Bitmap bmap = imagemLoja.getDrawingCache();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteImage = stream.toByteArray();
        String bytesEnconded = Base64.encodeToString(byteImage, Base64.DEFAULT);

        juridica.setFk_Pessoa(new Pessoa());
        juridica.setFk_Categoria_Loja((Categoria_Loja) spinnerCategoriasLoja.getSelectedItem());
        if(bytesEnconded != null)
            juridica.getFk_Pessoa().setFoto(bytesEnconded);
        juridica.getFk_Pessoa().setNome(editNome.getText().toString());
        juridica.getFk_Pessoa().setSobrenome(editSobrenome.getText().toString());
        juridica.getFk_Pessoa().setEmail(editEmail.getText().toString());
        juridica.getFk_Pessoa().setTelefone(editTelefone.getText().toString());
        juridica.setCnpj(editCnpj.getText().toString());
        juridica.setRazao_Social(editRazaoSocial.getText().toString());
        juridica.setSenha(editSenha.getText().toString());
        if(juridica.getFk_Localizacao() != null)
        {
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(Double.valueOf(juridica.getFk_Localizacao().getLatitude()),
                        Double.valueOf(juridica.getFk_Localizacao().getLongitude()), 1);
                if (addresses.size() > 0) {
                    Localizacao loca = juridica.getFk_Localizacao();
                    loca.setBairro(addresses.get(0).getSubLocality());
                    loca.setCidade(addresses.get(0).getLocality());
                    loca.setEstado(addresses.get(0).getAdminArea());
                    juridica.setFk_Localizacao(loca);
                    new SalvarDados().execute(juridica);
                }else{
                    Toast.makeText(CadastroPessoaJuridicaActivity.this,"Não foi possivel obter sua localizacao",Toast.LENGTH_LONG).show();
                }
            }catch (IOException e) {

                e.printStackTrace();
            }
        }else{
            Toast.makeText(CadastroPessoaJuridicaActivity.this,"Não foi possivel obter sua localizacao",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Metodo para abrir a camera do celular
     */
    public void TirarFotoCadastro(View v)
    {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intentCamera.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(intentCamera, REQUEST_IMAGE_CAPTURE);
        }
    }
    //endregion

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CadastroPessoaJuridicaActivity.this);
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
