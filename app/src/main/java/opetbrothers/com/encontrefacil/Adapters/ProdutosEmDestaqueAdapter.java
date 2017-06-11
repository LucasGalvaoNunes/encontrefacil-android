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

import opetbrothers.com.encontrefacil.Model.ProdutoDestaque;
import opetbrothers.com.encontrefacil.R;

/**
 * Created by Lucas Galvao Nunes on 10/06/2017.
 */

public class ProdutosEmDestaqueAdapter extends ArrayAdapter<ProdutoDestaque>
{
    private List<ProdutoDestaque> produtosEmDestaque;


    public ProdutosEmDestaqueAdapter(Context context, int resource, List<ProdutoDestaque> objects) {
        super(context,resource,objects);
        produtosEmDestaque = objects;
    }

    @Override
    public View getView(int position, View current, ViewGroup parentes){
        View v = current;
        if(v == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_produto_pessoa_fisica,null);
        }
        ProdutoDestaque produtoDestaque = produtosEmDestaque.get(position);
        if(produtoDestaque != null){
            ImageView imageProduto = (ImageView) v.findViewById(R.id.imagemProduto);
            TextView txtNome = (TextView) v.findViewById(R.id.textNomeProduto);
            TextView txtPreco = (TextView) v.findViewById(R.id.textPrecoProduto);

            if(imageProduto != null)
            {
                if(produtoDestaque.getFk_produto().getFoto() != null)
                {
                    byte[] foto = Base64.decode(produtoDestaque.getFk_produto().getFoto(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
                    imageProduto.setImageBitmap(bitmap);
                }
            }
            if(txtNome != null)
            {
                txtNome.setText(produtoDestaque.getFk_produto().getNome());
            }
            if(txtPreco != null)
            {
                txtPreco.setText(produtoDestaque.getFk_produto().getPreco());
            }
        }
        return v;
    }
}
