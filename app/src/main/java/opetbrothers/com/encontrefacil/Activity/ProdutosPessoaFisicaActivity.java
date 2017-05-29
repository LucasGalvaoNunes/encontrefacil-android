package opetbrothers.com.encontrefacil.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import opetbrothers.com.encontrefacil.Adapters.ProdutosPessoaFisicaAdapter;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;

public class ProdutosPessoaFisicaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos_pessoa_fisica);


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


        ListView listView = (ListView) findViewById(R.id.listViewProdutosPessoaFisica);
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
}
