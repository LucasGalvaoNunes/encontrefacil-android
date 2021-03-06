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
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import opetbrothers.com.encontrefacil.Adapters.CategoriaProdutoAdapter;
import opetbrothers.com.encontrefacil.Adapters.ProdutosPessoaFisicaAdapter;
import opetbrothers.com.encontrefacil.Model.Categoria_Produto;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.Util;

public class CategoriasProdutosPessoaFisicaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias_produtos_pessoa_fisica);

        new listaCategorias().execute();

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

    public class listaCategorias extends AsyncTask<Void, Void, String>{

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
                progress = new ProgressDialog(CategoriasProdutosPessoaFisicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(CategoriasProdutosPessoaFisicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                String result = HttpMetods.GET("CategoriaProduto/Todas");
                return result;
            }catch (Exception e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isConnected) {
                    ArrayList<Categoria_Produto> categorias = new ArrayList<Categoria_Produto>();

                    try {
                        JSONObject object = new JSONObject(s);
                        JSONArray result_categorias = object.getJSONArray("lista");

                        for (int i = 0; i < result_categorias.length(); i++) {
                            categorias.add(new Categoria_Produto(result_categorias.getJSONObject(i).getInt("id_Categoria_Produto"), result_categorias.getJSONObject(i).getString("nome")));
                        }

                        ListView listView = (ListView) findViewById(R.id.listViewCategoriasProdutos);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            //            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Categoria_Produto categoria_produto = (Categoria_Produto) parent.getItemAtPosition(position);
                                Intent i = new Intent(CategoriasProdutosPessoaFisicaActivity.this, ProdutosPessoaFisicaActivity.class);
                                i.putExtra("idCategoria", categoria_produto.getId_Categoria_Produto());
                                startActivity(i);

                            }
                        });
                        CategoriaProdutoAdapter produtosPessoaJuridicaAdapter = new CategoriaProdutoAdapter(CategoriasProdutosPessoaFisicaActivity.this, R.layout.list_categoria_produto, categorias);
                        listView.setAdapter(produtosPessoaJuridicaAdapter);

                        progress.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(CategoriasProdutosPessoaFisicaActivity.this, "Ocorreu um erro no servidor", Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }
            }else{
                Toast.makeText(CategoriasProdutosPessoaFisicaActivity.this,"Verifique a sua conexão...",Toast.LENGTH_LONG).show();
            }
        }
    }
}
