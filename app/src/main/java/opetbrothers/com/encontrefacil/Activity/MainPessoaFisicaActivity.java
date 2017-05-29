package opetbrothers.com.encontrefacil.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.MapsInitializer;

import java.util.ArrayList;
import java.util.List;

import opetbrothers.com.encontrefacil.Adapters.ProdutosPessoaFisicaAdapter;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;
public class MainPessoaFisicaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pesssoa_fisica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LoginActivity login = new LoginActivity();
        login.finish();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        List<Produto> produtos = new ArrayList<Produto>();
        Produto  prod = new Produto();
        prod.setNome("Tenis Adidas");
        prod.setDescricao("Tenis da adidas preto");
        prod.setPreco("R$:120,00");
        produtos.add(prod);
        prod.setNome("Tenis Nike");
        prod.setDescricao("Tenis da Nike preto");
        prod.setPreco("R$:120,00");
        produtos.add(prod);
        prod.setNome("Tenis Nike");
        prod.setDescricao("Tenis da Nike preto");
        prod.setPreco("R$:120,00");
        produtos.add(prod);
        prod.setNome("Tenis Nike");
        prod.setDescricao("Tenis da Nike preto");
        prod.setPreco("R$:120,00");
        produtos.add(prod);
        prod.setNome("Tenis Nike");
        prod.setDescricao("Tenis da Nike preto");
        prod.setPreco("R$:120,00");
        produtos.add(prod);
        prod.setNome("Tenis Nike");
        prod.setDescricao("Tenis da Nike preto");
        prod.setPreco("R$:120,00");
        produtos.add(prod);


        ListView listView = (ListView) findViewById(R.id.listProdutosMainPessoaFisica);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Produto produto = (Produto) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                //AQUI ENVIAR DADOS NECESSARIOS PARA A PROXIMA PAGINA
                startActivity(new Intent(getApplicationContext(),InfProdPessoaFisicaActivity.class));
            }
        });
        ProdutosPessoaFisicaAdapter produtosPessoaFisicaAdapter = new ProdutosPessoaFisicaAdapter(this,R.layout.list_produto_pessoa_fisica, produtos);
        listView.setAdapter(produtosPessoaFisicaAdapter);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_pesssoa_fisica, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.action_settings:
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_meusProdutos)
        {
            startActivity(new Intent(this,MeusProdutosPessoaFisicaActivity.class));
        }else if(id == R.id.nav_meusDados)
        {
            startActivity(new Intent(this,MeusDadosPessoaFisicaActivity.class));
        }else if(id == R.id.nav_categorias){
            startActivity(new Intent(this,CategoriasProdutosPessoaFisicaActivity.class));
        }else if(id == R.id.nav_Lojas){
            startActivity(new Intent(this, MapsLojasPertoActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    public void BuscarProduto(View v)
    {
        startActivity(new Intent(this,ProdutosPessoaFisicaActivity.class));
    }

    public void Sair(View v)
    {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
