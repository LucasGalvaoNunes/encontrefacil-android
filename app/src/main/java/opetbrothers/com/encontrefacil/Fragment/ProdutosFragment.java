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
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.google.gson.Gson;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import opetbrothers.com.encontrefacil.Adapters.ProdutosPessoaFisicaAdapter;
//import opetbrothers.com.encontrefacil.Model.Produto;
//import opetbrothers.com.encontrefacil.R;
//import opetbrothers.com.encontrefacil.Util.HttpMetods;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class ProdutosFragment extends Fragment {
//
//    String nomeCategoria;
//
//    public ProdutosFragment()
//    {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        nomeCategoria = getArguments().getString("nomeCategoria");
//        new ConexaoWebService().execute();
//
//        return inflater.inflate(R.layout.fragment_produtos, container, false);
//    }
//
//
//    private class ConexaoWebService extends AsyncTask<Void, Void, String> {
//        boolean isConnected = false;
//        ProgressDialog progress;
//
//        @Override
//        protected void onPreExecute() {
//
//            ConnectivityManager cm =
//                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//
//            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//            isConnected = activeNetwork != null &&
//                    activeNetwork.isConnectedOrConnecting();
//
//            if (isConnected) {
//                progress = new ProgressDialog(getActivity());
//                progress.setMessage("Carregando...");
//                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                progress.setProgress(0);
//                progress.show();
//            } else {
//                Toast.makeText(getActivity(), "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            String categorais = "";
//            if (nomeCategoria != "")
//                categorais = HttpMetods.GET("Produto/PorCategoria/" + nomeCategoria);
//            return categorais;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            if (isConnected) {
//                if (s != "") {
//                    try {
//                        List<Produto> produtos = new ArrayList<Produto>();
//                        JSONObject object = new JSONObject(s);
//                        Gson gson = new Gson();
//                        if (object.getBoolean("ok")) {
//                            JSONArray produtosArray = object.getJSONArray("lista");
//                            for (int i = 0; i < produtosArray.length(); i++) {
//                                Produto produto = gson.fromJson(produtosArray.get(i).toString(), Produto.class);
//                                produtos.add(produto);
//                            }
//
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(getActivity(), "Não foi possivel se conectar", Toast.LENGTH_LONG).show();
//                    }
//                } else {
//                    Toast.makeText(getActivity(), "Categoria nao selecionada", Toast.LENGTH_LONG).show();
//                }
//                progress.dismiss();
//            }
//        }
//    }
//}