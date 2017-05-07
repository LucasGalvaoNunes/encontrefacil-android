package opetbrothers.com.encontrefacil.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import opetbrothers.com.encontrefacil.Model.Localizacao;
import opetbrothers.com.encontrefacil.Model.Usuario;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;

public class CadastrarActivity extends AppCompatActivity{

    EditText editNome;
    EditText editSobreNome;
    EditText editEmail;
    EditText editSenha;


    Usuario usuario = new Usuario();

    LocationManager locationManager;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        editNome = (EditText) findViewById(R.id.editNome);
        editSobreNome = (EditText) findViewById(R.id.editSobreNome);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editSenha = (EditText) findViewById(R.id.editSenha);
    }

    public void Cadastrar(View v)
    {
        usuario.setNome(editNome.getText().toString());
        usuario.setSobrenome(editSobreNome.getText().toString());
        usuario.setEmail(editEmail.getText().toString());
        usuario.setSenha(editSenha.getText().toString());

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if(location != null)
        {
            Localizacao localizacao = new Localizacao();
            localizacao.setLongitude(String.valueOf(location.getLongitude()));
            localizacao.setLatitude(String.valueOf(location.getLatitude()));
            usuario.setFk_localizacao(localizacao);
            new ConexaoWebService().execute();
        }else{
            Toast.makeText(CadastrarActivity.this, "Não foi possivel completar o cadastro, Verifique se seu GPS esta ligado...", Toast.LENGTH_SHORT).show();
        }

    }


    private class ConexaoWebService extends AsyncTask<Void, Void, String> {
        boolean isConnected = false;
        ProgressDialog progress;
        @Override
        protected void onPreExecute()
        {

            ConnectivityManager cm =
                    (ConnectivityManager)CadastrarActivity.this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if(isConnected) {
                progress = new ProgressDialog(CadastrarActivity.this);
                progress.setMessage("Efetuando o Cadastro!");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(CadastrarActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            Gson gson = new Gson();
            String jsonUsuario = gson.toJson(usuario);
            String json = HttpMetods.POST("Usuario/Cadastrar", jsonUsuario);
            return json;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isConnected)
            {
                try{
                    JSONObject object = new JSONObject(s);
                    if(object.getBoolean("ok"))
                    {
                        Toast.makeText(CadastrarActivity.this, object.getString("mensagem"), Toast.LENGTH_LONG).show();
                        Intent i = new Intent(CadastrarActivity.this, LoginActivity.class);
                        startActivity(i);
                    }else{
                        Toast.makeText(CadastrarActivity.this, object.getString("mensagem"), Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(CadastrarActivity.this, "Algo deu Errado!", Toast.LENGTH_LONG).show();
                }

                progress.dismiss();
            }

        }
    }
}
