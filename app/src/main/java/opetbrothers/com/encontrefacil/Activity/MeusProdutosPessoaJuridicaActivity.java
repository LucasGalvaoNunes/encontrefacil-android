package opetbrothers.com.encontrefacil.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import opetbrothers.com.encontrefacil.Adapters.ProdutosPessoaFisicaAdapter;
import opetbrothers.com.encontrefacil.Adapters.ProdutosPessoaJuridicaAdapter;
import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class MeusProdutosPessoaJuridicaActivity extends AppCompatActivity {


    //region ANDROID METODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_produtos_pessoa_juridica);

        String jsonPrefe = Util.RecuperarUsuario("pessoaJuridica", MeusProdutosPessoaJuridicaActivity.this);
        Gson gson = new Gson();
        PessoaJuridica pessoaJuridica = gson.fromJson(jsonPrefe, PessoaJuridica.class);
        new PuxarProdutos().execute(pessoaJuridica.getId_PessoaJuridica());
    }@Override
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

    //region  WEBSERVICE
    private class PuxarProdutos extends AsyncTask<Integer, Void, String> {
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
                progress = new ProgressDialog(MeusProdutosPessoaJuridicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(MeusProdutosPessoaJuridicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Integer... params) {
            String produtos = HttpMetods.GET("Produto/TodasPorLoja/" + params[0].toString());
            return produtos;
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
                        List<Produto> produtos = new ArrayList<Produto>();
                        for(int i =0; i < object.getJSONArray("lista").length();i++)
                        {
                            String json = object.getJSONArray("lista").get(i).toString();
                            Produto pessoaJuridica = gson.fromJson(json, Produto.class);
                            produtos.add(pessoaJuridica);
                        }
                        ListView listView = (ListView) findViewById(R.id.listViewMeusProdPessoaJuridica);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            //            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Produto produto = (Produto) parent.getItemAtPosition(position);
                                    new  PuxarProdutoEspecifico().execute(produto.getId_Produto());
                            }
                        });
                        ProdutosPessoaJuridicaAdapter produtosPessoaJuridicaAdapter = new ProdutosPessoaJuridicaAdapter(MeusProdutosPessoaJuridicaActivity.this,R.layout.list_produtos_pessoa_juridica, produtos);
                        listView.setAdapter(produtosPessoaJuridicaAdapter);
                    }else{
                        Toast.makeText(MeusProdutosPessoaJuridicaActivity.this,"Você não tem nada cadastrado ainda!",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e)
                {
                    Toast.makeText(MeusProdutosPessoaJuridicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }

        }
    }

    private class PuxarProdutoEspecifico extends AsyncTask<Integer, Void, String> {
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
                progress = new ProgressDialog(MeusProdutosPessoaJuridicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(MeusProdutosPessoaJuridicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Integer... params) {
            String produtos = HttpMetods.GET("Produto/Get/" + params[0].toString());
            return produtos;
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
                        String json = object.getJSONObject("produtoEntity").toString();
                        Produto prod = gson.fromJson(json, Produto.class);
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeusProdutosPessoaJuridicaActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialog_inf_prod_pessoa_juridica, null);
                        EditText editCategoriaProduto = (EditText) mView.findViewById(R.id.editDialogCateoriaProd);
                        EditText editMarca = (EditText) mView.findViewById(R.id.editDialogMarcaProd);
                        EditText editNome = (EditText) mView.findViewById(R.id.editDialogNomeProd);
                        EditText editDescricao = (EditText) mView.findViewById(R.id.editDialogDescricaoProd);
                        EditText editPreco = (EditText) mView.findViewById(R.id.editDialogPrecoProd);
                        ImageView fotoProd = (ImageView) mView.findViewById(R.id.imagemAtualizaProd);



                        editCategoriaProduto.setText(prod.getFk_Categoria_Produto().getNome());
                        editMarca.setText(prod.getFk_Marca_Produto().getNome());
                        editNome.setText(prod.getNome());
                        editDescricao.setText(prod.getDescricao());
                        editPreco.setText(prod.getPreco());
                        byte[] foto = Base64.decode(prod.getFoto(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
                        fotoProd.setImageBitmap(bitmap);

                        mBuilder.setView(mView);
                        AlertDialog dialog = mBuilder.create();
                        dialog.show();
                    }else{
                        Toast.makeText(MeusProdutosPessoaJuridicaActivity.this,"Você não tem nada cadastrado ainda!",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e)
                {
                    Toast.makeText(MeusProdutosPessoaJuridicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }

        }
    }
    //endregion


}
