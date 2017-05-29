package opetbrothers.com.encontrefacil.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import opetbrothers.com.encontrefacil.Model.Categoria_Produto;
import opetbrothers.com.encontrefacil.R;

/**
 * Created by Lucas Galvao Nunes on 30/04/2017.
 */

public class CategoriaProdutoAdapter  extends ArrayAdapter<Categoria_Produto> {
    private List<Categoria_Produto> categoria_produtos;


    public CategoriaProdutoAdapter(Context context, int resource, List<Categoria_Produto> objects) {
        super(context,resource,objects);
        categoria_produtos = objects;
    }

    @Override
    public View getView(int position, View current, ViewGroup parentes){
        View v = current;
        if(v == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_categoria_produto,null);
        }
        Categoria_Produto f = categoria_produtos.get(position);
        if(f != null){
            TextView txtCategoria = (TextView) v.findViewById(R.id.textCategoria);
            if(txtCategoria != null)
            {
                txtCategoria.setText(f.getNome());
            }
        }
        return v;
    }
}
