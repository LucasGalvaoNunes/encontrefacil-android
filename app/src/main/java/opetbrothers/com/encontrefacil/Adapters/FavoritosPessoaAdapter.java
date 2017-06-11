package opetbrothers.com.encontrefacil.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import opetbrothers.com.encontrefacil.Model.FavoritosPessoaFisica;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;

/**
 * Created by Lucas Galvao Nunes on 11/06/2017.
 */

public class FavoritosPessoaAdapter extends ArrayAdapter<FavoritosPessoaFisica> {
    private List<FavoritosPessoaFisica> produtos;


    public FavoritosPessoaAdapter(Context context, int resource, List<FavoritosPessoaFisica> objects) {
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
        FavoritosPessoaFisica p = produtos.get(position);
        if(p != null){
            TextView txtNome = (TextView) v.findViewById(R.id.textNomeProduto);
            TextView textPreco = (TextView) v.findViewById(R.id.textPrecoProduto);
            ImageView imageProd = (ImageView) v.findViewById(R.id.imagemProduto);
            if(txtNome != null)
            {
                txtNome.setText(p.getFk_Produto().getNome());
            }
            if(textPreco != null)
            {
                textPreco.setText(p.getFk_Produto().getPreco());
            }
            if(imageProd != null)
            {
                byte[] foto = Base64.decode(p.getFk_Produto().getFoto(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
                imageProd.setImageBitmap(bitmap);
            }
        }
        return v;
    }
}
