package opetbrothers.com.encontrefacil.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import opetbrothers.com.encontrefacil.Model.Pessoa;
import opetbrothers.com.encontrefacil.R;

public class LoginPessoaFisicaActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private Profile perfil;
    private Pessoa pessoaFisica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login_pessoa_fisica);


        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                perfil = Profile.getCurrentProfile();
                pessoaFisica = new Pessoa();
                pessoaFisica.setNome(perfil.getFirstName());
                pessoaFisica.setSobrenome(perfil.getLastName());
                pessoaFisica.setTelefone("123");
                pessoaFisica.setFoto(perfil.getProfilePictureUri(400,400).toString());

                GraphRequest request = GraphRequest.newMeRequest( AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback(){

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.e("GraphResponse ", response.toString());
                                try {

                                    pessoaFisica.setEmail(object.getString("email"));
                                    toActivityMain(pessoaFisica);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email");
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
        i.putExtra("nome", pessoaFisica.getNome());
        i.putExtra("sobrenome", pessoaFisica.getSobrenome());
        i.putExtra("foto", pessoaFisica.getFoto().toString());
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void teste(String usuario) {
        Toast.makeText(this, "Logou", Toast.LENGTH_LONG).show();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.menu_padrao,menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        switch (item.getItemId())
//        {
//            case R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//
//        }
//        return super.onOptionsItemSelected(item);
//    }
}