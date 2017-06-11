package opetbrothers.com.encontrefacil.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import opetbrothers.com.encontrefacil.Adapters.FavoritosPessoaAdapter;
import opetbrothers.com.encontrefacil.Adapters.ProdutosPessoaFisicaAdapter;
import opetbrothers.com.encontrefacil.Model.FavoritosPessoaFisica;
import opetbrothers.com.encontrefacil.Model.PessoaFisica;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class MeusProdutosPessoaFisicaActivity extends AppCompatActivity {
    PessoaFisica pessoaFisica;
    ListView listViewProdutos;
    Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_produtos_pessoa_fisica);

        String jsonPrefe = Util.RecuperarUsuario("pessoaFisica", MeusProdutosPessoaFisicaActivity.this);
        pessoaFisica = gson.fromJson(jsonPrefe, PessoaFisica.class);

        listViewProdutos = (ListView) findViewById(R.id.listViewMeusProdutosPessoaFisica);
        listViewProdutos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FavoritosPessoaFisica produtoSelecionado = (FavoritosPessoaFisica) parent.getItemAtPosition(position);
                Util.SalvarDados("produtoSelecionado", gson.toJson(produtoSelecionado.getFk_Produto()), MeusProdutosPessoaFisicaActivity.this);
                Intent i = new Intent(MeusProdutosPessoaFisicaActivity.this, InfProdPessoaFisicaActivity.class);
                startActivity(i);
            }
        });
        listViewProdutos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                FavoritosPessoaFisica produto = (FavoritosPessoaFisica) parent.getItemAtPosition(position);
                produto.setFk_Pessoa_Fisica(null);
                produto.setFk_Produto(null);
                new RemoverDosFavoritos().execute(produto);
                return true;
            }
        });
        new PegarOsFavoritos().execute(pessoaFisica.getId_PessoaFisica());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.meus_produtos_pessoa_fisica,menu);
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


    //region WEB SERVICE
    private class PegarOsFavoritos extends AsyncTask<Integer, Void, String> {

        //region ON PRE EXECUTE
        boolean isConnected = false;
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {

            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (isConnected) {
                progress = new ProgressDialog(MeusProdutosPessoaFisicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            } else {
                Toast.makeText(MeusProdutosPessoaFisicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }
        //endregion

        //region DO IN BACKGROUND
        @Override
        protected String doInBackground(Integer... params) {
            Gson gson = new Gson();
            String produtos = HttpMetods.GET("FavoritosPessoaFisica/TodasPorPessoa/" + params[0]);
            return produtos;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isConnected) {

                try {
                    JSONObject object = new JSONObject(s);
                    object.remove("type");
                    List<FavoritosPessoaFisica> listProdutos = new ArrayList<FavoritosPessoaFisica>();
                    if (object.getBoolean("ok")) {
                        JSONArray listProdutosJson = object.getJSONArray("lista");
                        for (int i = 0; i < listProdutosJson.length(); i++) {
                            JSONObject objProduto = listProdutosJson.getJSONObject(i);
                            objProduto.remove("type");
                            FavoritosPessoaFisica fav = gson.fromJson(objProduto.toString(), FavoritosPessoaFisica.class);
                            listProdutos.add(fav);
                        }
                    } else {
                        Toast.makeText(MeusProdutosPessoaFisicaActivity.this, "Você não salvou nada nos favoritos!", Toast.LENGTH_LONG).show();
                    }
                    FavoritosPessoaAdapter produtosPessoaFisicaAdapter = new FavoritosPessoaAdapter(MeusProdutosPessoaFisicaActivity.this, R.layout.list_produto_pessoa_fisica, listProdutos);
                    listViewProdutos.setAdapter(produtosPessoaFisicaAdapter);
                } catch (Exception e) {
                    Toast.makeText(MeusProdutosPessoaFisicaActivity.this, "Não foi possivel se conectar", Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }

        }
        //endregion
    }
    private class RemoverDosFavoritos extends AsyncTask<FavoritosPessoaFisica, Void, String> {

        //region ON PRE EXECUTE
        boolean isConnected = false;
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {

            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (isConnected) {
                progress = new ProgressDialog(MeusProdutosPessoaFisicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            } else {
                Toast.makeText(MeusProdutosPessoaFisicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }
        //endregion

        //region DO IN BACKGROUND
        @Override
        protected String doInBackground(FavoritosPessoaFisica... params) {
            Gson gson = new Gson();
            String json = gson.toJson(params[0]);
            String produtos = HttpMetods.DELETE("FavoritosPessoaFisica/Excluir",gson.toJson(params[0]));
            return produtos;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isConnected) {

                try {
                    JSONObject object = new JSONObject(s);
                    object.remove("type");
                    List<FavoritosPessoaFisica> listProdutos = new ArrayList<FavoritosPessoaFisica>();
                    if (object.getBoolean("ok")) {
                        Toast.makeText(MeusProdutosPessoaFisicaActivity.this, "Removido com sucesso!", Toast.LENGTH_LONG).show();
                        new PegarOsFavoritos().execute(pessoaFisica.getId_PessoaFisica());
                    } else {
                    }
                } catch (Exception e) {
                    Toast.makeText(MeusProdutosPessoaFisicaActivity.this, "Não foi possivel se conectar", Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }

        }
        //endregion
    }
    //endregion
}
