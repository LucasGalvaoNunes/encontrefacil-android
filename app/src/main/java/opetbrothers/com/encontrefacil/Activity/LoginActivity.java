package opetbrothers.com.encontrefacil.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import opetbrothers.com.encontrefacil.Model.PessoaFisica;
import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.PermissionUtils;
import opetbrothers.com.encontrefacil.Util.Util;

public class LoginActivity extends AppCompatActivity {


    EditText editUsuario;
    EditText editSenha;

    static String usuario;
    static String senha;

    PessoaJuridica pessoaJuridica;

    String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PermissionUtils.validate(this,0, permissoes);
        new GetPDF().execute(1);
        String jsonPrefe = Util.RecuperarUsuario("pessoaJuridica", LoginActivity.this);
        Gson gson = new Gson();
        pessoaJuridica = gson.fromJson(jsonPrefe, PessoaJuridica.class);
        if(pessoaJuridica != null){
            Intent mainJuridica = new Intent(this,MainPessoaJuridicaActivity.class);
            startActivity(mainJuridica);
            finish();
        }
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                // Alguma permissão foi negada
                alertAndFinish();
                return;
            }
        }
        // Se chegou aqui está OK
    }

    private void alertAndFinish() {
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name).setMessage("Para utilizar este aplicativo, você precisa aceitar as permissões.");
            // Add the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void ClickButtonPessoaFisica(View v)
    {
        Intent i = new Intent(this,LoginPessoaFisicaActivity.class);
        startActivity(i);
        finish();
    }

    public void ClickButtonPessoaJuridica(View v)
    {
        Intent i = new Intent(this,LoginPessoaJuridicaActivity.class);
        startActivity(i);
        finish();
    }



    private class GetPDF extends AsyncTask<Integer, Void, String> {
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
                progress = new ProgressDialog(LoginActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(LoginActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Integer... params) {
            Gson gson = new Gson();
           //http://localhost:8080/EncontreFacilWs/rest/Produto/Relatorio/1

            String relatorio = HttpMetods.GET("Produto/Relatorio/1");
            return relatorio;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isConnected)
            {

                try{
                    JSONObject object = new JSONObject(s);
                    byte[] pdf = Base64.decode(object.getString("pdf"), Base64.DEFAULT);


                }catch (Exception e)
                {
                    Toast.makeText(LoginActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();


            }

        }
    }
//    private class ConexaoWebService extends AsyncTask<Void, Void, String> {
//        boolean isConnected = false;
//        ProgressDialog progress;
//        @Override
//        protected void onPreExecute()
//        {
//
//            ConnectivityManager cm =
//                    (ConnectivityManager)LoginActivity.this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//
//            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//            isConnected = activeNetwork != null &&
//                    activeNetwork.isConnectedOrConnecting();
//
//            if(isConnected) {
//                progress = new ProgressDialog(LoginActivity.this);
//                progress.setMessage("Efetuando o Login!");
//                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                progress.setProgress(0);
//                progress.show();
//            }
//            else{
//                Toast.makeText(LoginActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            Usuario usuarioModel = new Usuario();
//            usuarioModel.setEmail(usuario);
//            usuarioModel.setSenha(senha);
//            Gson gson = new Gson();
//            String jsonConvert = gson.toJson(usuarioModel);
//            String json = HttpMetods.POST("Usuario/Logar", jsonConvert);
//            return json;
//
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            if(isConnected)
//            {
//                try{
//                    JSONObject object = new JSONObject(s);
//                    if(object.getBoolean("ok"))
//                    {
//                        Intent i = new Intent(LoginActivity.this, MainPessoaFisicaActivity.class);
//                        i.putExtra("Usuario", object.getJSONObject("usuarioEntity").getString("nome") + " " + object.getJSONObject("usuarioEntity").getString("sobrenome"));
//                        i.putExtra("Email", object.getJSONObject("usuarioEntity").getString("email"));
//                        startActivity(i);
//                    }else{
//                        Toast.makeText(LoginActivity.this,object.getString("mensagem"),Toast.LENGTH_LONG).show();
//                    }
//                }catch (Exception e)
//                {
//                    Toast.makeText(LoginActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
//                }
//                progress.dismiss();
//            }
//
//        }
//    }
}
