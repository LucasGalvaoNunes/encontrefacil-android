package opetbrothers.com.encontrefacil.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import opetbrothers.com.encontrefacil.Model.Categoria_Loja;
import opetbrothers.com.encontrefacil.Model.Localizacao;
import opetbrothers.com.encontrefacil.Model.Pessoa;
import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class LoginPessoaJuridicaActivity extends AppCompatActivity {

    //region ATRIBUTOS VIEW
    EditText editEmail;
    EditText editSenha;
    //endregion


    //region ANDROID METODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pessoa_juridica);

        editEmail = (EditText) findViewById(R.id.editEmailLogin);
        editSenha = (EditText) findViewById(R.id.editSenhaLogin);

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
    //endregion

    //region WEBSERVICE METODS
    private class Logar extends AsyncTask<PessoaJuridica, Void, String> {
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
                progress = new ProgressDialog(LoginPessoaJuridicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(LoginPessoaJuridicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(PessoaJuridica... params) {
            Gson gson = new Gson();
            String json = gson.toJson(params[0]);
            String categorais = HttpMetods.POST("PessoaJuridica/Login",gson.toJson(params[0]));
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
                        PessoaJuridica pessoaJuridica = gson.fromJson(object.getJSONObject("pessoaJuridica").toString(),PessoaJuridica.class); ;
                        if(pessoaJuridica.getCodigo_verificacao() == "" || pessoaJuridica.getCodigo_verificacao() == null)
                        {
                            Intent main = new Intent(LoginPessoaJuridicaActivity.this,MainPessoaJuridicaActivity.class);
                            String JsonToSave = object.getJSONObject("pessoaJuridica").toString();
                            Util.SalvarDados("pessoaJuridica",JsonToSave, LoginPessoaJuridicaActivity.this);
                            startActivity(main);
                            finish();
                        }else{
                            Intent main = new Intent(LoginPessoaJuridicaActivity.this,VerificacaoEmailActivity.class);
                            Util.SalvarDados("pessoaJuridica",object.getJSONObject("pessoaJuridica").toString(), LoginPessoaJuridicaActivity.this);
                            startActivity(main);
                            finish();
                        }

                    }else{
                        editEmail.setError(object.getString("mensagem"));
                    }


                }catch (Exception e)
                {
                    Toast.makeText(LoginPessoaJuridicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }

        }
    }
    //endregion

    //region BUTTON METODS
    public void LogarPessoaJuridica(View v)
    {
        //region VALIDACAO
        if (editEmail.getText().toString().length() == 0) {
            editEmail.setError("Email é obrigatorio!");
            return;
        }
        if (editSenha.getText().toString().length() == 0) {
            editSenha.setError("Senha é obrigatorio!");
            return;
        }
        //endregion

        PessoaJuridica pessoaJuridica = new PessoaJuridica(new Pessoa(
                "",
                "",
                "",
                null,
                editEmail.getText().toString()
        ),
                new Categoria_Loja(),
                new Localizacao(),
                editSenha.getText().toString(),
                "",
                ""
        );
        new Logar().execute(pessoaJuridica);
    }

    public void ActivityCadastrarPessoaJuridica(View v)
    {
        startActivity(new Intent(this, CadastroPessoaJuridicaActivity.class));
    }
    //endregion


    //region OTHER METODS

    //endregion
}
