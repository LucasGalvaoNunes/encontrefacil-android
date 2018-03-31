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

import opetbrothers.com.encontrefacil.Model.Avaliacao_Produto;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;

/**
 * Created by Lucas Galvao Nunes on 11/06/2017.
 */

public class AvaliacoesAdapter extends ArrayAdapter<Avaliacao_Produto> {
    private List<Avaliacao_Produto> avaliacoes;


    public AvaliacoesAdapter(Context context, int resource, List<Avaliacao_Produto> objects) {
        super(context,resource,objects);
        avaliacoes = objects;
    }

    @Override
    public View getView(int position, View current, ViewGroup parentes){
        View v = current;
        if(v == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_avaliacoes_prod,null);
        }
        Avaliacao_Produto p = avaliacoes.get(position);
        if(p != null){
            TextView txtComentario = (TextView) v.findViewById(R.id.comentarioDalista);
            TextView textNome = (TextView) v.findViewById(R.id.nomeDaLista);
            if(textNome != null)
            {
                textNome.setText("- " + p.getFk_Pessoa_Fisica().getFk_Pessoa().getNome());
            }
            if(txtComentario != null)
            {
                txtComentario.setText(p.getComentario());
            }
        }
        return v;
    }
}
