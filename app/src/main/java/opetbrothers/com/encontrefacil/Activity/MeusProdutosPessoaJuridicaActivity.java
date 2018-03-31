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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import opetbrothers.com.encontrefacil.Adapters.ProdutosPessoaFisicaAdapter;
import opetbrothers.com.encontrefacil.Adapters.ProdutosPessoaJuridicaAdapter;
import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.Model.ProdutoDestaque;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.PatternsUtil;
import opetbrothers.com.encontrefacil.Util.Util;

public class MeusProdutosPessoaJuridicaActivity extends AppCompatActivity {

    //region ATRIBUTOS
    AlertDialog dialogProdutos;
    PessoaJuridica pessoaJuridica;
    Produto produtoRecuperado;

    EditText editNomeAtualizacao;
    EditText editDescricaoAtualizacao;
    EditText editPrecoAtualizacao;
    SeekBar seekBar;
    //endregion

    //region ANDROID METODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_produtos_pessoa_juridica);

        //region INSTANCIAS
        pessoaJuridica = new PessoaJuridica();
        produtoRecuperado = new Produto();
        String jsonPrefe = Util.RecuperarUsuario("pessoaJuridica", MeusProdutosPessoaJuridicaActivity.this);
        Gson gson = new Gson();
        pessoaJuridica = gson.fromJson(jsonPrefe, PessoaJuridica.class);
        //endregion

        //Puxa os produtos da loja
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

        //region onPREEXECUTE
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
        //endregion

        //region DO IN BACKGROUND
        @Override
        protected String doInBackground(Integer... params) {
            String produtos = HttpMetods.GET("Produto/PorLoja/" + params[0].toString());
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
                        //region RECEBE OS DADOS DO JSON
                        Gson gson = new Gson();
                        List<Produto> produtos = new ArrayList<Produto>();
                        for(int i =0; i < object.getJSONArray("lista").length();i++)
                        {
                            String json = object.getJSONArray("lista").get(i).toString();
                            Produto pessoaJuridica = gson.fromJson(json, Produto.class);
                            produtos.add(pessoaJuridica);
                        }
                        //endregion

                        //region SETA A LISTVIEW
                        ListView listView = (ListView) findViewById(R.id.listViewMeusProdPessoaJuridica);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            //            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Produto produto = (Produto) parent.getItemAtPosition(position);
                                    new  PuxarProdutoEspecifico().execute(produto.getId_Produto());
                            }
                        });
                        ProdutosPessoaJuridicaAdapter produtosPessoaJuridicaAdapter = new ProdutosPessoaJuridicaAdapter(
                                MeusProdutosPessoaJuridicaActivity.this,R.layout.list_produtos_pessoa_juridica, produtos);
                        listView.setAdapter(produtosPessoaJuridicaAdapter);
                        //endregion
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
        //endregion
    }

    private class PuxarProdutoEspecifico extends AsyncTask<Integer, Void, String> {

        //region ON PRE EXECUTE
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
        //endregion

        //region DO IN BACKGROUND
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
                        produtoRecuperado = gson.fromJson(json, Produto.class);

                        //region CRIA O ALERT DIALOG
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MeusProdutosPessoaJuridicaActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialog_inf_prod_pessoa_juridica, null);
                        //region SETA AS VARIAVEIS
                        editNomeAtualizacao = (EditText) mView.findViewById(R.id.editDialogNomeProd);
                        editDescricaoAtualizacao = (EditText) mView.findViewById(R.id.editDialogDescricaoProd);
                        editPrecoAtualizacao = (EditText) mView.findViewById(R.id.editDialogPrecoProd);
                        editPrecoAtualizacao.addTextChangedListener(new PatternsUtil(editPrecoAtualizacao).getpPatternPreco());
                        ImageView fotoProd = (ImageView) mView.findViewById(R.id.imagemAtualizaProd);
                        seekBar = (SeekBar) mView.findViewById(R.id.seekBarExposicao);
                        //endregion


                        //region ADCIONA OS VALORES
                        editNomeAtualizacao.setText(produtoRecuperado.getNome());
                        editDescricaoAtualizacao.setText(produtoRecuperado.getDescricao());
                        editPrecoAtualizacao.setText(produtoRecuperado.getPreco());
                        byte[] foto = Base64.decode(produtoRecuperado.getFoto(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
                        fotoProd.setImageBitmap(bitmap);
                        //endregion

                        //region ADICIONA OS METODOS DOS BOTOES
                        ImageButton btnAtualiza = (ImageButton) mView.findViewById(R.id.btnSalvarAtualizacao);
                        btnAtualiza.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                //region VALIDACAO
                                if (editNomeAtualizacao.getText().toString().length() == 0) {
                                    editNomeAtualizacao.setError("É obrigatorio!");
                                    return;
                                }
                                if (editDescricaoAtualizacao.getText().toString().length() == 0) {
                                    editDescricaoAtualizacao.setError("É obrigatorio!");
                                    return;
                                }
                                if (editPrecoAtualizacao.getText().toString().length() == 0) {
                                    editPrecoAtualizacao.setError("É obrigatorio!");
                                    return;
                                }
                                //endregion

                                produtoRecuperado.setData_Publicacao(new Timestamp(System.currentTimeMillis()));
                                produtoRecuperado.setNome(editNomeAtualizacao.getText().toString());
                                produtoRecuperado.setDescricao(editDescricaoAtualizacao.getText().toString());
                                produtoRecuperado.setPreco(editPrecoAtualizacao.getText().toString());

                                new Atualizar().execute(produtoRecuperado);
                            }
                        });
                        ImageButton btnExcluir = (ImageButton) mView.findViewById(R.id.btnExcluirAtualizacao);
                        btnExcluir.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                new Excluir().execute(produtoRecuperado);
                            }
                        });
                        ImageButton btnImpulsionar = (ImageButton) mView.findViewById(R.id.btnImpulsionar);
                        btnImpulsionar.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Calendar cal = Calendar.getInstance();

                                cal.add(Calendar.DATE, + 20);
                                ProdutoDestaque produtoDestaque = new ProdutoDestaque();

                                produtoDestaque.setFk_produto(produtoRecuperado);
                                produtoDestaque.setData_entrada(new Timestamp(System.currentTimeMillis()));
                                produtoDestaque.setData_saida(new Timestamp(cal.getTimeInMillis()));
                                produtoDestaque.setExposicao(seekBar.getProgress());
                                new Impulsionar().execute(produtoDestaque);
                            }
                        });
                        //endregion

                        mBuilder.setView(mView);
                        dialogProdutos = mBuilder.create();
                        dialogProdutos.show();
                        //endregion
                    }
                }catch (Exception e)
                {
                    Toast.makeText(MeusProdutosPessoaJuridicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }

        }
        //endregion
    }

    private class Atualizar extends AsyncTask<Produto, Void, String> {
        //region ON PRE EXECUTE
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
        //endregion
        //region DO IN BACKGROUND
        @Override
        protected String doInBackground(Produto... params) {
            Gson gson = new Gson();
            String produtos = HttpMetods.PUT("Produto/Atualizar", gson.toJson(params[0]));
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
                        new PuxarProdutos().execute(pessoaJuridica.getId_PessoaJuridica());
                        dialogProdutos.cancel();
                        Toast.makeText(MeusProdutosPessoaJuridicaActivity.this,"Alterado com sucesso!",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MeusProdutosPessoaJuridicaActivity.this,"Não foi possivel atualizar os dados, sintimos muito!",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e)
                {
                    Toast.makeText(MeusProdutosPessoaJuridicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }

        }
        //endregion
    }

    private class Excluir extends AsyncTask<Produto, Void, String> {
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
        protected String doInBackground(Produto... params) {
            Gson gson = new Gson();
            String produtos = HttpMetods.DELETE("Produto/Excluir", gson.toJson(params[0]));
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
                        new PuxarProdutos().execute(pessoaJuridica.getId_PessoaJuridica());
                        dialogProdutos.cancel();
                        Toast.makeText(MeusProdutosPessoaJuridicaActivity.this,"Excluido com sucesso!",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MeusProdutosPessoaJuridicaActivity.this,"Não foi possivel atualizar os dados, sintimos muito!",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e)
                {
                    Toast.makeText(MeusProdutosPessoaJuridicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }

        }
    }

    private class Impulsionar extends AsyncTask<ProdutoDestaque, Void, String> {
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
        protected String doInBackground(ProdutoDestaque... params) {
            Gson gson = new Gson();
            String produtos = HttpMetods.POST("ProdutoDestaque/Cadastrar", gson.toJson(params[0]));
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
                        new PuxarProdutos().execute(pessoaJuridica.getId_PessoaJuridica());
                        dialogProdutos.cancel();
                        Toast.makeText(MeusProdutosPessoaJuridicaActivity.this,"Impulsionado com sucesso!",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MeusProdutosPessoaJuridicaActivity.this,"Voce ja impulsionou este produto!",Toast.LENGTH_LONG).show();
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
