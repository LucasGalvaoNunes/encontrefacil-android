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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.w3c.dom.Text;

import opetbrothers.com.encontrefacil.Model.Pessoa;
import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class VerificacaoEmailActivity extends AppCompatActivity {

    //region VIEW ATRIBUTES
    TextView email;
    EditText codigoVerificacao;
    //endregion

    PessoaJuridica pessoaJuridica;

    //region METODS ANDROID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacao_email);

        String jsonPrefe = Util.RecuperarUsuario("pessoaJuridica", VerificacaoEmailActivity.this);
        Gson gson = new Gson();

        pessoaJuridica = gson.fromJson(jsonPrefe, PessoaJuridica.class);

        email = (TextView) findViewById(R.id.textViewEmailCodigo);
        codigoVerificacao = (EditText) findViewById(R.id.editCodigoVerificacao);
        email.setText(pessoaJuridica.getFk_Pessoa().getEmail());

    }
    //endregion


    //region METODS WEB SERVICE
    private class VerificarEmail extends AsyncTask<PessoaJuridica, Void, String> {
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
                progress = new ProgressDialog(VerificacaoEmailActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(VerificacaoEmailActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(PessoaJuridica... params) {
            Gson gson = new Gson();
            String juridica = HttpMetods.PUT("PessoaJuridica/VerificarEmail",gson.toJson(params[0]));
            return juridica;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isConnected)
            {
                if(s != null)
                {
                    try{
                        JSONObject object = new JSONObject(s);
                        object.remove("type");
                        Gson gson = new Gson();
                        if(object.getBoolean("ok")){
                            String json = object.getJSONObject("pessoaJuridica").toString();
                            PessoaJuridica pessoaJuridica = gson.fromJson(json, PessoaJuridica.class);
                            Intent main = new Intent(VerificacaoEmailActivity.this, MainPessoaJuridicaActivity.class);
                            Util.SalvarDados("pessoaJuridica",object.getJSONObject("pessoaJuridica").toString(), VerificacaoEmailActivity.this);
                            startActivity(main);
                            finish();
                        }else{
                            codigoVerificacao.setError(object.getString("mensagem"));
                        }
                    }catch (Exception e)
                    {
                        Toast.makeText(VerificacaoEmailActivity.this,"Erro no aplicativo, desculpe!",Toast.LENGTH_LONG).show();
                    }
                    progress.dismiss();
                }
            }else{
                Toast.makeText(VerificacaoEmailActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
            }

        }
    }
    //endregion

    //region BUTTONS LISTENERS
    public void VerificarEmail(View v){

        if (codigoVerificacao.getText().toString().length() == 0) {
            codigoVerificacao.setError("Código é obrigatório!");
            return;
        }
        pessoaJuridica.setCodigo_verificacao(codigoVerificacao.getText().toString());
        new VerificarEmail().execute(pessoaJuridica);
    }
    //endregion

}
