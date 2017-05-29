package opetbrothers.com.encontrefacil.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;

import opetbrothers.com.encontrefacil.Model.Categoria_Produto;
import opetbrothers.com.encontrefacil.Model.Marca_Produto;
import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class CadastroProdutoPessoaJuridicaActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView imagemProduto;
    EditText editCategoriaProduto;
    EditText editMarca;
    EditText editNome;
    EditText editDescricao;
    EditText editPreco;

    //region ANDROID METODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto_pessoa_juridica);

        editCategoriaProduto = (EditText) findViewById(R.id.editCategoriaProduto);
        editMarca = (EditText) findViewById(R.id.editMarcaProduto);
        editNome = (EditText) findViewById(R.id.editNomeProduto);
        editDescricao = (EditText) findViewById(R.id.editDescricaoProduto);
        editPreco = (EditText) findViewById(R.id.editPrecoProduto);
        imagemProduto = (ImageView) findViewById(R.id.imageCadastroProduto);

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            // Recorta a imagem em 125x125
            Bitmap imageBitmap = ThumbnailUtils.extractThumbnail((Bitmap) extras.get("data"),125,125,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            imagemProduto.setImageBitmap(imageBitmap);
        }
    }
    //endregion

    //region WEBSERVICE
    private class SalvarDados extends AsyncTask<Produto, Void, String> {
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
                progress = new ProgressDialog(CadastroProdutoPessoaJuridicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(CadastroProdutoPessoaJuridicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Produto... params) {
            Gson gson = new Gson();
            String json = gson.toJson(params[0]);
            String produto = HttpMetods.POST("Produto/Cadastrar",gson.toJson(params[0]));
            return produto;
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
                        if(object.getBoolean("ok"))
                        {
                            String json = object.getJSONObject("produtoEntity").toString();
                            Produto produto = gson.fromJson(json, Produto.class);
                            ClearEdits();
                            startActivity(new Intent(CadastroProdutoPessoaJuridicaActivity.this,MeusProdutosPessoaJuridicaActivity.class));
                        }


                    }catch (Exception e)
                    {
                        Toast.makeText(CadastroProdutoPessoaJuridicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                    }
                    progress.dismiss();
                }

            }

        }
    }
    //endregion

    //region BUTTONS METODS
    public void CadastrarProduto(View v){
        //region VALIDACAO
        if (editCategoriaProduto.getText().toString().length() == 0) {
            editCategoriaProduto.setError("É obrigatorio!");
            return;
        }
        if (editMarca.getText().toString().length() == 0) {
            editMarca.setError("É obrigatorio!");
            return;
        }
        if (editNome.getText().toString().length() == 0) {
            editNome.setError("É obrigatorio!");
            return;
        }
        if (editDescricao.getText().toString().length() == 0) {
            editDescricao.setError("É obrigatorio!");
            return;
        }
        if (editPreco.getText().toString().length() == 0) {
            editPreco.setError("É obrigatorio!");
            return;
        }
        //endregion

        imagemProduto.buildDrawingCache();
        Bitmap bmap = imagemProduto.getDrawingCache();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteImage = stream.toByteArray();
        String bytesEnconded = Base64.encodeToString(byteImage,Base64.DEFAULT);

        String jsonPrefe = Util.RecuperarUsuario("pessoaJuridica", CadastroProdutoPessoaJuridicaActivity.this);
        Gson gson = new Gson();
        PessoaJuridica pessoaJuridica = gson.fromJson(jsonPrefe, PessoaJuridica.class);

        Produto produto = new Produto(new Categoria_Produto(editCategoriaProduto.getText().toString()),
                new Marca_Produto(editMarca.getText().toString()),
                pessoaJuridica,
                editNome.getText().toString(),
                editDescricao.getText().toString(),
                editPreco.getText().toString(),
                bytesEnconded,
                new Timestamp(System.currentTimeMillis()));
        new SalvarDados().execute(produto);

    }
    public void TirarFotoProduto(View v)
    {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intentCamera.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(intentCamera, REQUEST_IMAGE_CAPTURE);
        }
    }

    //endregion

    //region OTHER METODS

    public void ClearEdits()
    {
        editCategoriaProduto.getText().clear();
        editMarca.getText().clear();
        editNome.getText().clear();
        editDescricao.getText().clear();
        editPreco.getText().clear();
    }
    //endregion
}
