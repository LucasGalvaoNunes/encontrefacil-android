package opetbrothers.com.encontrefacil.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import opetbrothers.com.encontrefacil.Model.Pessoa;
import opetbrothers.com.encontrefacil.Model.PessoaFisica;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class MeusDadosPessoaFisicaActivity extends AppCompatActivity {

    //region INTANCIAS
    ImageView imagePerfilFisica;
    EditText editTextNome;
    EditText editTextSobrenome;
    EditText editTextTelefone;
    EditText editTextEmail;
    EditText editTextCPF;
    PessoaFisica pessoaFisica;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_dados_pessoa_fisica);

        //region INICIALIZE EDITTEXTS
        imagePerfilFisica = (ImageView) findViewById(R.id.imagePerfilFisica);
        editTextNome = (EditText) findViewById(R.id.editTextNome);
        editTextSobrenome = (EditText) findViewById(R.id.editTextSobrenome);
        editTextTelefone = (EditText) findViewById(R.id.editTextTelefone);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextCPF = (EditText) findViewById(R.id.editTextCPF);
        //endregion

        String jsonPrefe = Util.RecuperarUsuario("pessoaFisica", MeusDadosPessoaFisicaActivity.this);
        Gson gson = new Gson();
        pessoaFisica = gson.fromJson(jsonPrefe, PessoaFisica.class);

        byte[] foto = Base64.decode(pessoaFisica.getFk_Pessoa().getFoto(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
        imagePerfilFisica.setImageBitmap(bitmap);

        //region SET-EDITTEXTS
        editTextNome.setText(pessoaFisica.getFk_Pessoa().getNome());
        editTextSobrenome.setText(pessoaFisica.getFk_Pessoa().getSobrenome());
        editTextTelefone.setText(pessoaFisica.getFk_Pessoa().getTelefone());
        editTextEmail.setText(pessoaFisica.getFk_Pessoa().getEmail());
        editTextCPF.setText(pessoaFisica.getCpf());
        //endregion

    }

    public void clickButtonSalvarAlteracoes (View v){

        //region VALIDAÇÃO
        if (editTextNome.getText().toString().length() == 0) {
            editTextNome.setError("Nome é obrigatório!");
            return;
        }
        if (editTextSobrenome.getText().toString().length() == 0) {
            editTextSobrenome.setError("Sobrenome é obrigatório!");
            return;
        }
        if (editTextTelefone.getText().toString().length() == 0) {
            editTextTelefone.setError("Telefone é obrigatório!");
            return;
        }
        if (editTextEmail.getText().toString().length() == 0) {
            editTextEmail.setError("Email é obrigatório!");
            return;
        }
        if (editTextCPF.getText().toString().length() == 0) {
            editTextCPF.setError("CPF é obrigatório!");
            return;
        }


        //endregion

        pessoaFisica.getFk_Pessoa().setNome(editTextNome.getText().toString());
        pessoaFisica.getFk_Pessoa().setSobrenome(editTextSobrenome.getText().toString());
        pessoaFisica.getFk_Pessoa().setTelefone(editTextTelefone.getText().toString());
        pessoaFisica.getFk_Pessoa().setEmail(editTextEmail.getText().toString());
        pessoaFisica.setCpf(editTextCPF.getText().toString());

        new SalvarClassePessoa().execute(pessoaFisica.getFk_Pessoa());
        new SalvarAlteracoesFisica().execute(pessoaFisica);

    }

    //region ASYNC
    private class SalvarClassePessoa extends AsyncTask<Pessoa, Void, String>{

        boolean isConnected = false;
        ProgressDialog progress;

        @Override
        protected void onPreExecute(){

            ConnectivityManager cm =
                    (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if(isConnected) {
                progress = new ProgressDialog(MeusDadosPessoaFisicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(MeusDadosPessoaFisicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Pessoa... params){

            Gson gson = new Gson();
            String xx = gson.toJson(params[0]);
            try{
                String salvar = HttpMetods.PUT("Pessoa/Atualizar/", gson.toJson(params[0]));

                JSONObject result = new JSONObject(salvar);
                result.remove("type");
                return result.getString("ok");

            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
    private class SalvarAlteracoesFisica extends AsyncTask<PessoaFisica, Void, String>{

        boolean isConnected = false;
        ProgressDialog progress;

        @Override
        protected void onPreExecute(){

            ConnectivityManager cm =
                    (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if(isConnected) {
                progress = new ProgressDialog(MeusDadosPessoaFisicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(MeusDadosPessoaFisicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(PessoaFisica... params) {

            Gson gson = new Gson();

            try{

                String alteracao = HttpMetods.PUT("PessoaFisica/Atualizar/", gson.toJson(params[0]));
                return alteracao;

            }catch (Exception e){
                return null;
            }
        }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if(isConnected) {
                    try {

                        JSONObject result = new JSONObject(s);
                        result.remove("type");

                        if (result.getBoolean("ok")) {
                            Gson gson = new Gson();

                            String json = result.getJSONObject("pessoaFisicaEntity").toString();

                            Intent i = new Intent(MeusDadosPessoaFisicaActivity.this, MainPessoaFisicaActivity.class);
                            Util.SalvarDados("pessoaFisica", json, MeusDadosPessoaFisicaActivity.this);

                            startActivity(i);
                            Toast.makeText(MeusDadosPessoaFisicaActivity.this, "Dados Atualizados com Sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(MeusDadosPessoaFisicaActivity.this, "Ocorreu um erro ao atualizar os seus dados. Tente novamente.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {

                    }
                }else{
                    Toast.makeText(MeusDadosPessoaFisicaActivity.this,"Verifique a sua conexão...",Toast.LENGTH_LONG).show();
                }
            }
        }
    //endregion

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

}
