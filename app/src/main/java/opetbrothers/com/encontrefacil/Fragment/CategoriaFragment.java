//package opetbrothers.com.encontrefacil.Fragment;
//
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.annotation.Nullable;
//import android.support.v4.app.FragmentManager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.Toast;
//import com.google.gson.Gson;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import opetbrothers.com.encontrefacil.Adapters.CategoriaProdutoAdapter;
//import opetbrothers.com.encontrefacil.Model.Categoria_Produto;
//import opetbrothers.com.encontrefacil.R;
//import opetbrothers.com.encontrefacil.Util.HttpMetods;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class CategoriaFragment extends Fragment {
//
//    static ArrayAdapter arrayAdapter;
//    static List<Categoria_Produto> categorias;
//
//    ListView listView = null;
//
//    View mView;
//
//    public CategoriaFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_categoria, container, false);
//
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        listView = (ListView) getActivity().findViewById(R.id.listCategorias);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Categoria_Produto categoria_produto = (Categoria_Produto) parent.getItemAtPosition(position);
//                Bundle bundle = new Bundle();
//                bundle.putString("nomeCategoria", categoria_produto.getNome());
//                ProdutosFragment frag = new ProdutosFragment();
//                frag.setArguments(bundle);
//                FragmentManager manager = getFragmentManager();
//                manager.beginTransaction()
//                        .replace(R.id.mainLayout,frag)
//                        .commit();
//            }
//        });
//        categorias = new ArrayList<>();
//        new ConexaoWebService().execute();
//
//    }
//
//    private class ConexaoWebService extends AsyncTask<Void, Void, String> {
//        boolean isConnected = false;
//        ProgressDialog progress;
//        @Override
//        protected void onPreExecute()
//        {
//
//            ConnectivityManager cm =
//                    (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//
//            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//            isConnected = activeNetwork != null &&
//                    activeNetwork.isConnectedOrConnecting();
//
//            if(isConnected) {
//                progress = new ProgressDialog(getActivity());
//                progress.setMessage("Carregando...");
//                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                progress.setProgress(0);
//                progress.show();
//            }
//            else{
//                Toast.makeText(getActivity(), "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            String categorais = HttpMetods.GET("CategoriaProduto/Todas");
//            return categorais;
//
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            if(isConnected)
//            {
//                try{
//                    JSONObject object = new JSONObject(s);
//                    Gson gson = new Gson();
//                    if(object.getBoolean("ok"))
//                    {
//                        JSONArray categoriasArray = object.getJSONArray("lista");
//                        for(int i =0; i < categoriasArray.length(); i++)
//                        {
//                            Categoria_Produto categoria = gson.fromJson(categoriasArray.get(i).toString(), Categoria_Produto.class);
//                            categorias.add(categoria);
//                        }
//                        ListView listCategorias = (ListView) getActivity().findViewById(R.id.listCategorias);
//                        CategoriaProdutoAdapter categoriaProdutoAdapter = new CategoriaProdutoAdapter(getActivity(),R.layout.list_categoria_produto, categorias);
//                        listCategorias.setAdapter(categoriaProdutoAdapter);
//
//                    }
//
//
//                }catch (Exception e)
//                {
//                    Toast.makeText(getActivity(),"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
//                }
//                progress.dismiss();
//            }
//
//        }
//    }
//
//}
