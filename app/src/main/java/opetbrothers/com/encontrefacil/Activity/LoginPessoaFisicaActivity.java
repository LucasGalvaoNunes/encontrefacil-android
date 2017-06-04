package opetbrothers.com.encontrefacil.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import opetbrothers.com.encontrefacil.Model.Pessoa;
import opetbrothers.com.encontrefacil.Model.PessoaFisica;
import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class LoginPessoaFisicaActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private Profile perfil;
    private Pessoa pessoa;
    private PessoaFisica pessoaFisica;

    //region ANDROID METODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pessoa_fisica);
        FacebookSdk.sdkInitialize(getApplicationContext());


        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                GraphRequest request = GraphRequest.newMeRequest( AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback(){

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.e("GraphResponse ", response.toString());
                                try {
                                    pessoa = new Pessoa();
                                    pessoaFisica = new PessoaFisica();
                                    pessoa.setNome(object.getString("first_name"));
                                    pessoa.setSobrenome(object.getString("last_name"));
                                    pessoa.setTelefone("123");
                                    pessoa.setEmail(object.getString("email"));
                                    long id = object.getLong("id");
                                    pessoaFisica.setId_facebook(String.valueOf(id));
                                    JSONObject picture = new JSONObject(object.getString("picture"));
                                     pessoa.setFoto(picture.getJSONObject("data").getString("url"));
                                    pessoaFisica.setFk_Pessoa(pessoa);
                                    new SalvarDados().execute(pessoaFisica);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "picture,first_name,last_name,email,id");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    public void toActivityMain(Pessoa pessoaFisica){

        Intent i = new Intent(this,MainPessoaFisicaActivity.class);
        i.putExtra("nome", pessoa.getNome());
        i.putExtra("sobrenome", pessoa.getSobrenome());
        i.putExtra("foto", pessoa.getFoto().toString());
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

    //region ASYNC
    private class SalvarDados extends AsyncTask<PessoaFisica, Void, String> {
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
                progress = new ProgressDialog(LoginPessoaFisicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(LoginPessoaFisicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(PessoaFisica... params) {
            Gson gson = new Gson();
            String buscarId = HttpMetods.GET("PessoaFisica/BuscaIdFacebook/" + params[0].getId_facebook());
            try{

                JSONObject objectId = new JSONObject(buscarId);
                objectId.remove("type");
                if(!objectId.getBoolean("ok"))
                {
                    InputStream inputStream = new URL(params[0].getFk_Pessoa().getFoto()).openStream();   // Download Image from URL
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteImage = stream.toByteArray();
                    String bytesEnconded = Base64.encodeToString(byteImage, Base64.DEFAULT);
                    params[0].getFk_Pessoa().setFoto(bytesEnconded);
                    inputStream.close();
                    String cadastrar = HttpMetods.POST("PessoaFisica/Cadastrar", gson.toJson(params[0]));
                    return cadastrar;
                }else{
                    return objectId.toString();
                }
            }
            catch (Exception e)
            {
                return null;
            }
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
                        String json = object.getJSONObject("pessoaFisicaEntity").toString();
                        PessoaFisica pessoaFisica = gson.fromJson(json, PessoaFisica.class);

                        Intent i = new Intent(LoginPessoaFisicaActivity.this,MainPessoaFisicaActivity.class);
                        Util.SalvarDados("pessoaFisica",json, LoginPessoaFisicaActivity.this);
                        startActivity(i);
                        finish();
                    }else{
                        Toast.makeText(LoginPessoaFisicaActivity.this,"Não foi possivel cadastrar",Toast.LENGTH_LONG).show();
                    }


                }catch (Exception e)
                {
                    Toast.makeText(LoginPessoaFisicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();


            }

        }
    }
    //endregion

}