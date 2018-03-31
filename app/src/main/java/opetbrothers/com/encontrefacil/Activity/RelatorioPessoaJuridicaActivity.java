package opetbrothers.com.encontrefacil.Activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.Util;

public class RelatorioPessoaJuridicaActivity extends AppCompatActivity {
    PDFView pdfView;
    PessoaJuridica pessoaJuridica;
    private static final int  MEGABYTE = 1024 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_pessoa_juridica);
        pdfView = (PDFView) findViewById(R.id.pdfView);

        String jsonPrefe = Util.RecuperarUsuario("pessoaJuridica", RelatorioPessoaJuridicaActivity.this);
        Gson gson = new Gson();
        pessoaJuridica = gson.fromJson(jsonPrefe, PessoaJuridica.class);
       new GetPDF().execute(pessoaJuridica);

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

    //region ASYNC
    private class GetPDF extends AsyncTask<PessoaJuridica, Void, File> {
        boolean isConnected = false;
        ProgressDialog progress;
        @Override
        protected void onPreExecute()
        {

            ConnectivityManager cm =
                    (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if(isConnected) {
                progress = new ProgressDialog(RelatorioPessoaJuridicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(RelatorioPessoaJuridicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected File doInBackground(PessoaJuridica... params) {
            String fileUrl = "http://35.184.152.136/EncontreFacilWs/RelatorioProdutos/" + params[0].getId_PessoaJuridica();
            String fileName = "Relatorio.pdf";
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "Relatorios");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);
            if(pdfFile.exists())
                pdfFile.delete();
            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);

                byte[] buffer = new byte[MEGABYTE];
                int bufferLength = 0;
                while((bufferLength = inputStream.read(buffer))>0 ){
                    fileOutputStream.write(buffer, 0, bufferLength);
                }
                fileOutputStream.close();
                return pdfFile;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(File s) {
            super.onPostExecute(s);
            if(isConnected)
            {
               if(s != null)
               {
                   if (s.exists()) {
                       pdfView.fromFile(s).load();
                   }
               }
                progress.dismiss();
            }

        }
    }
    //endregion


}


