package opetbrothers.com.encontrefacil.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;


/**
 * Created by Lucas Galvao Nunes on 03/05/2017.
 */

public class ProdutosPessoaFisicaAdapter extends ArrayAdapter<Produto>
{
    private List<Produto> produtos;


    public ProdutosPessoaFisicaAdapter(Context context, int resource, List<Produto> objects) {
        super(context,resource,objects);
        produtos = objects;
    }

    @Override
    public View getView(int position, View current, ViewGroup parentes){
        View v = current;
        if(v == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_produto_pessoa_fisica,null);
        }
        Produto p = produtos.get(position);
        if(p != null){
            TextView txtNome = (TextView) v.findViewById(R.id.textNomeProduto);
            TextView textPreco = (TextView) v.findViewById(R.id.textPrecoProduto);
            ImageView imageProd = (ImageView) v.findViewById(R.id.imagemProduto);
            if(txtNome != null)
            {
                txtNome.setText(p.getNome());
            }
            if(textPreco != null)
            {
                textPreco.setText(p.getPreco());
            }
            if(imageProd != null)
            {
                byte[] foto = Base64.decode(p.getFoto(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
                imageProd.setImageBitmap(bitmap);
            }
        }
        return v;
    }
}
