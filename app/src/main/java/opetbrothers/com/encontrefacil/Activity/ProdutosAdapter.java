package opetbrothers.com.encontrefacil.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import opetbrothers.com.encontrefacil.Model.Categoria_Produto;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;


/**
 * Created by Lucas Galvao Nunes on 03/05/2017.
 */

public class ProdutosAdapter extends ArrayAdapter<Produto>
{
    private List<Produto> produtos;


    public ProdutosAdapter(Context context, int resource, List<Produto> objects) {
        super(context,resource,objects);
        produtos = objects;
    }

    @Override
    public View getView(int position, View current, ViewGroup parentes){
        View v = current;
        if(v == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_produto,null);
        }
        Produto p = produtos.get(position);
        if(p != null){
            TextView txtNome = (TextView) v.findViewById(R.id.textNomeProduto);
            TextView txtDescricao = (TextView) v.findViewById(R.id.textDescricaoProduto);
            TextView textPreco = (TextView) v.findViewById(R.id.textPrecoProduto);
            if(txtNome != null)
            {
                txtNome.setText(p.getNome());
            }
            if(txtDescricao != null)
            {
                txtDescricao.setText(p.getDescricao());
            }
            if(textPreco != null)
            {
                textPreco.setText(p.getPreco());
            }
        }
        return v;
    }
}
