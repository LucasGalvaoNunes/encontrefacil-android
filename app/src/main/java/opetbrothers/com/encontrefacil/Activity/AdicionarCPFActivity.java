package opetbrothers.com.encontrefacil.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import org.json.JSONObject;

import opetbrothers.com.encontrefacil.Model.Pessoa;
import opetbrothers.com.encontrefacil.Model.PessoaFisica;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class AdicionarCPFActivity extends AppCompatActivity {

    EditText editTextCPF;
    PessoaFisica pessoaFisica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_cpf);

        editTextCPF = (EditText) findViewById(R.id.editCPF);

        String jsonPrefe = Util.RecuperarUsuario("pessoaFisica", AdicionarCPFActivity.this);
        Gson gson = new Gson();
        pessoaFisica = gson.fromJson(jsonPrefe, PessoaFisica.class);

    }

    public void SalvarCPF(View v){

        String cpfUsuario = editTextCPF.getText().toString();

        if (editTextCPF.getText().toString().length() == 0) {
            editTextCPF.setError("CPF é obrigatório!");
            return;
        }

        pessoaFisica.setCpf(cpfUsuario);

        new SalvarCPF().execute(pessoaFisica);

    }

    //region ASYNC
    private class SalvarCPF extends AsyncTask<PessoaFisica, Void, String>{
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
                progress = new ProgressDialog(AdicionarCPFActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(AdicionarCPFActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(PessoaFisica... params) {

            Gson gson = new Gson();
            String xx = gson.toJson(params[0], PessoaFisica.class);

            try{
                String adicionaCPF = HttpMetods.PUT("PessoaFisica/Atualizar/", gson.toJson(params[0]));
                return adicionaCPF;
            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PessoaFisica pessoaFisica;

            if(isConnected) {
                try {
                    JSONObject result = new JSONObject(s);
                    result.remove("type");

                    if (result.getBoolean("ok")) {
                        Gson gson = new Gson();

                        String json = result.getJSONObject("pessoaFisicaEntity").toString();
                        pessoaFisica = gson.fromJson(json, PessoaFisica.class);

                        if(pessoaFisica.getCpf() != null) {
                            Util.SalvarDados("pessoaFisica", json, AdicionarCPFActivity.this);
                            Intent i = new Intent(AdicionarCPFActivity.this, MainPessoaFisicaActivity.class);
                            startActivity(i);
                            Toast.makeText(AdicionarCPFActivity.this, "Seja bem-vindo ao Encontre Fácil!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(AdicionarCPFActivity.this, "Ocorreu um erro no registro do seu CPF. Tente novamente.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    LoginManager.getInstance().logOut();
                    Intent i = new Intent(AdicionarCPFActivity.this, LoginActivity.class);
                    startActivity(i);
                    Toast.makeText(AdicionarCPFActivity.this, "Ocorreu um erro no registro do seu CPF.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }else{
                Toast.makeText(AdicionarCPFActivity.this,"Verifique a sua conexão...",Toast.LENGTH_LONG).show();
            }
        }
    }
    //endregion

}
