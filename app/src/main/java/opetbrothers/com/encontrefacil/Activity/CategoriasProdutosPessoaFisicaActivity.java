package opetbrothers.com.encontrefacil.Activity;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import opetbrothers.com.encontrefacil.Adapters.CategoriaProdutoAdapter;
import opetbrothers.com.encontrefacil.Adapters.ProdutosPessoaFisicaAdapter;
import opetbrothers.com.encontrefacil.Model.Categoria_Produto;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;

public class CategoriasProdutosPessoaFisicaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias_produtos_pessoa_fisica);

        List<Categoria_Produto> categorias = new ArrayList<Categoria_Produto>();
        Categoria_Produto  cat = new Categoria_Produto();
        cat.setNome("Eletronicos");
        categorias.add(cat);
        cat.setNome("Roupas");
        categorias.add(cat);
        cat.setNome("Tenis");
        categorias.add(cat);
        cat.setNome("Eltrodomesticos");
        categorias.add(cat);
        cat.setNome("Video Games");
        categorias.add(cat);
        cat.setNome("Bicicletas");
        categorias.add(cat);
        cat.setNome("Computadores");
        categorias.add(cat);

        ListView listView = (ListView) findViewById(R.id.listViewCategoriasProdutos);
        CategoriaProdutoAdapter categoriaProdutoAdapter = new CategoriaProdutoAdapter(this,R.layout.list_categoria_produto, categorias);
        listView.setAdapter(categoriaProdutoAdapter);
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
}
