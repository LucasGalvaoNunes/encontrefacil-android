package opetbrothers.com.encontrefacil.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import opetbrothers.com.encontrefacil.Model.Usuario;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.PermissionUtils;

public class LoginActivity extends AppCompatActivity {


    EditText editUsuario;
    EditText editSenha;

    static String usuario;
    static String senha;

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

        editUsuario = (EditText) findViewById(R.id.editEmail);
        editSenha = (EditText) findViewById(R.id.editSenha);
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

    public void Logar(View v)
    {
        usuario = editUsuario.getText().toString();
        senha = editSenha.getText().toString();

        if(!usuario.isEmpty() &&  !senha.isEmpty())
        {
            new ConexaoWebService().execute();

        }else{
            Toast.makeText(this, "Digite seu email e senha!", Toast.LENGTH_LONG).show();
        }
    }

    public void ActivityCadastrar(View v)
    {
        Intent i = new Intent(LoginActivity.this, CadastrarActivity.class);
        startActivity(i);
    }

    private class ConexaoWebService extends AsyncTask<Void, Void, String> {
        boolean isConnected = false;
        ProgressDialog progress;
        @Override
        protected void onPreExecute()
        {

            ConnectivityManager cm =
                    (ConnectivityManager)LoginActivity.this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if(isConnected) {
                progress = new ProgressDialog(LoginActivity.this);
                progress.setMessage("Efetuando o Login!");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(LoginActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            Usuario usuarioModel = new Usuario();
            usuarioModel.setEmail(usuario);
            usuarioModel.setSenha(senha);
            Gson gson = new Gson();
            String jsonConvert = gson.toJson(usuarioModel);
            String json = HttpMetods.POST("Usuario/Logar", jsonConvert);
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
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra("Usuario", object.getJSONObject("usuarioEntity").getString("nome") + " " + object.getJSONObject("usuarioEntity").getString("sobrenome"));
                        i.putExtra("Email", object.getJSONObject("usuarioEntity").getString("email"));
                        startActivity(i);
                    }else{
                        Toast.makeText(LoginActivity.this,object.getString("mensagem"),Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e)
                {
                    Toast.makeText(LoginActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }

        }
    }
}
