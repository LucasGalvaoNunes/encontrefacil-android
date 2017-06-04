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
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        FacebookSdk.sdkInitialize(getApplicationContext());

        PermissionUtils.validate(this,0, permissoes);

        String jsonPrefe = Util.RecuperarUsuario("pessoaJuridica", LoginActivity.this);
        Gson gson = new Gson();
        pessoaJuridica = gson.fromJson(jsonPrefe, PessoaJuridica.class);
        if(pessoaJuridica != null){
            Intent mainJuridica = new Intent(this,MainPessoaJuridicaActivity.class);
            startActivity(mainJuridica);
            finish();
        }else if (AccessToken.getCurrentAccessToken() != null){
            Intent mainFisica = new Intent(this,MainPessoaFisicaActivity.class);
            startActivity(mainFisica);
            finish();
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
       // finish();
    }

    public void ClickButtonPessoaJuridica(View v)
    {
        Intent i = new Intent(this,LoginPessoaJuridicaActivity.class);
        startActivity(i);
       // finish();
    }

}
