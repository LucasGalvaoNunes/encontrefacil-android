package opetbrothers.com.encontrefacil.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import opetbrothers.com.encontrefacil.Model.Categoria_Loja;
import opetbrothers.com.encontrefacil.Model.Pessoa;
import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class MeusDadosPessoaJuridicaActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    //region VARIAVEIS
    ImageView imagemLoja;
    Spinner spinnerCategoriasLoja;
    EditText editRazaoSocial;
    EditText editEmail;
    EditText editNome;
    EditText editSobrenome;
    EditText editNovaSenha;
    EditText editConfirmaSenha;
    EditText editTelefone;
    EditText editCnpj;
    //endregion

    PessoaJuridica pessoaJuridica;
    private List<Categoria_Loja> categorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_dados_pessoa_juridica);
        categorias = new ArrayList<Categoria_Loja>();
        new PegarCategorias().execute();

        imagemLoja = (ImageView) findViewById(R.id.imageLojaAtualiza);
        spinnerCategoriasLoja = (Spinner) findViewById(R.id.spinnerCategoriaAtualiza);
        editRazaoSocial = (EditText) findViewById(R.id.editRazaoSocialAtualiza);
        editEmail =(EditText) findViewById(R.id.editTextEmailAtualiza);
        editNome = (EditText) findViewById(R.id.editNomeAtualiza);
        editSobrenome = (EditText) findViewById(R.id.editSobrenomeAtualiza);
        editNovaSenha = (EditText) findViewById(R.id.editTextnovaSenha);
        editConfirmaSenha = (EditText) findViewById(R.id.editTextConfirmaSenha);
        editTelefone = (EditText) findViewById(R.id.editTextTelefoneAtualiza);
        editCnpj = (EditText) findViewById(R.id.editCnpjAtualiza);
        pessoaJuridica = new PessoaJuridica();

        String jsonPrefe = Util.RecuperarUsuario("pessoaJuridica", MeusDadosPessoaJuridicaActivity.this);
        Gson gson = new Gson();
        pessoaJuridica = gson.fromJson(jsonPrefe, PessoaJuridica.class);
        if(pessoaJuridica.getFk_Pessoa().getFoto() != null)
        {
            byte[] foto = Base64.decode(pessoaJuridica.getFk_Pessoa().getFoto(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
            imagemLoja.setImageBitmap(bitmap);
        }
        //Categoria//

        editRazaoSocial.setText(pessoaJuridica.getRazao_Social());
        editEmail.setText(pessoaJuridica.getFk_Pessoa().getEmail());
        editNome.setText(pessoaJuridica.getFk_Pessoa().getNome());
        editSobrenome.setText(pessoaJuridica.getFk_Pessoa().getSobrenome());
        editTelefone.setText(pessoaJuridica.getFk_Pessoa().getTelefone());
        editCnpj.setText(pessoaJuridica.getCnpj());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            // Recorta a imagem em 125x125
            Bitmap imageBitmap = ThumbnailUtils.extractThumbnail((Bitmap) extras.get("data"),125,125,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            imagemLoja.setImageBitmap(imageBitmap);
        }
    }



    public void AtualizaButton(View v)
    {
        boolean isOk = true;
        //region VALIDAÇÃO
        if (editRazaoSocial.getText().toString().length() == 0) {
            editRazaoSocial.setError("Razão Social é obrigatorio!");
            return;
        }
        if (editCnpj.getText().toString().length() == 0) {
            editCnpj.setError("CNPJ é obrigatorio!");
            return;
        }
        if (editEmail.getText().toString().length() == 0) {
            editEmail.setError("Email é obrigatorio!");
            return;
        }
        if (editTelefone.getText().toString().length() == 0) {
            editTelefone.setError("Telefone é obrigatorio!");
            return;
        }
        if (editNome.getText().toString().length() == 0) {
            editNome.setError("Nome é obrigatorio!");
            return;
        }
        if (editSobrenome.getText().toString().length() == 0) {
            editSobrenome.setError("SobreNome é obrigatorio!");
            return;
        }
        if (editNovaSenha.getText().toString().length() > 0) {
            if (editConfirmaSenha.getText().toString().length() == 0) {
                isOk = false;
                editConfirmaSenha.setError("Confirma Senha é obrigatorio!");
                return;
            }else{
                String novaSenha = editNovaSenha.getText().toString();
                if(!novaSenha.equals(pessoaJuridica.getSenha()))
                {
                    String confirmaSenha = editConfirmaSenha.getText().toString();
                    if(novaSenha.equals(confirmaSenha))
                    {
                        pessoaJuridica.setSenha(editNovaSenha.getText().toString());
                    }else{
                        isOk = false;
                        editConfirmaSenha.setError("Confirmação da senha incorreta!");
                    }
                }else{
                    isOk = false;
                    editNovaSenha.setError("A senha nova não pode ser igual a antiga!");
                }
            }
        }
        //endregion
        if(isOk)
        {
            imagemLoja.buildDrawingCache();
            Bitmap bmap = imagemLoja.getDrawingCache();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteImage = stream.toByteArray();
            String bytesEnconded = Base64.encodeToString(byteImage, Base64.DEFAULT);

            pessoaJuridica.setFk_Categoria_Loja((Categoria_Loja) spinnerCategoriasLoja.getSelectedItem());
            if(bytesEnconded != null)
                pessoaJuridica.getFk_Pessoa().setFoto(bytesEnconded);
            pessoaJuridica.getFk_Pessoa().setNome(editNome.getText().toString());
            pessoaJuridica.getFk_Pessoa().setSobrenome(editSobrenome.getText().toString());
            pessoaJuridica.getFk_Pessoa().setEmail(editEmail.getText().toString());
            pessoaJuridica.getFk_Pessoa().setTelefone(editTelefone.getText().toString());
            pessoaJuridica.setCnpj(editCnpj.getText().toString());
            pessoaJuridica.setRazao_Social(editRazaoSocial.getText().toString());

            new SalvarDados().execute(pessoaJuridica);
        }
    }


    //region WEBSERVICE
    private class SalvarDados extends AsyncTask<PessoaJuridica, Void, String> {
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
                progress = new ProgressDialog(MeusDadosPessoaJuridicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(MeusDadosPessoaJuridicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(PessoaJuridica... params) {
            Gson gson = new Gson();
            String jsonAtualizarPessoa = gson.toJson(params[0].getFk_Pessoa());
            String pessoa = HttpMetods.PUT("Pessoa/Atualizar",jsonAtualizarPessoa);
            try{
                JSONObject object = new JSONObject(pessoa);
                object.remove("type");
                if(object.getBoolean("ok"))
                {
                    String juridica = HttpMetods.PUT("PessoaJuridica/Atualizar",gson.toJson(params[0]));
                    return juridica;
                }else{
                    return null;
                }

            }catch (Exception e){
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
                        String json = object.getJSONObject("pessoaJuridica").toString();
                        PessoaJuridica pessoaJuridica = gson.fromJson(json, PessoaJuridica.class);

                        Intent Main = new Intent(MeusDadosPessoaJuridicaActivity.this, MainPessoaJuridicaActivity.class);
                        Util.SalvarDados("pessoaJuridica",object.getJSONObject("pessoaJuridica").toString(), MeusDadosPessoaJuridicaActivity.this);
                        Toast.makeText(MeusDadosPessoaJuridicaActivity.this,"Atualizado com sucesso!",Toast.LENGTH_LONG).show();
                        startActivity(Main);
                        finish();

                    }else{
                        editEmail.setError(object.getString("mensagem"));
                    }


                }catch (Exception e)
                {
                    Toast.makeText(MeusDadosPessoaJuridicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();


            }

        }
    }

    private class PegarCategorias extends AsyncTask<Void, Void, String> {
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
                progress = new ProgressDialog(MeusDadosPessoaJuridicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(MeusDadosPessoaJuridicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String categorais = HttpMetods.GET("CategoriaLoja/Todas");
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
                        JSONArray categoriasArray = object.getJSONArray("lista");
                        for(int i =0; i < categoriasArray.length(); i++)
                        {
                            categoriasArray.getJSONObject(i).remove("type");
                            String json = categoriasArray.getJSONObject(i).toString();
                            Categoria_Loja categoriaLoja = gson.fromJson(json,Categoria_Loja.class);
                            categorias.add(categoriaLoja);
                        }
                        ArrayAdapter<Categoria_Loja> arrayAdapter = new ArrayAdapter<Categoria_Loja>(MeusDadosPessoaJuridicaActivity.this,R.layout.support_simple_spinner_dropdown_item,categorias);
                        ArrayAdapter<Categoria_Loja> spinnerArrayAdapter = arrayAdapter;
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                        spinnerCategoriasLoja.setAdapter(spinnerArrayAdapter);
                    }


                }catch (Exception e)
                {
                    Toast.makeText(MeusDadosPessoaJuridicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }

        }
    }
    //endregion

    public void TirarFotoAtualiza(View v)
    {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intentCamera.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(intentCamera, REQUEST_IMAGE_CAPTURE);
        }
    }

}
