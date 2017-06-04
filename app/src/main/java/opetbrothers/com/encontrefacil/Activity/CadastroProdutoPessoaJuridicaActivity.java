package opetbrothers.com.encontrefacil.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import opetbrothers.com.encontrefacil.Model.Categoria_Produto;
import opetbrothers.com.encontrefacil.Model.Marca_Produto;
import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.Model.Produto;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.HttpMetods;
import opetbrothers.com.encontrefacil.Util.PackageManagerUtils;
import opetbrothers.com.encontrefacil.Util.Util;

public class CadastroProdutoPessoaJuridicaActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    ImageView imagemProduto;
    EditText editCategoriaProduto;
    EditText editMarca;
    EditText editNome;
    EditText editDescricao;
    EditText editPreco;

    //region ANDROID METODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto_pessoa_juridica);

        editCategoriaProduto = (EditText) findViewById(R.id.editCategoriaProduto);
        editMarca = (EditText) findViewById(R.id.editMarcaProduto);
        editNome = (EditText) findViewById(R.id.editNomeProduto);
        editDescricao = (EditText) findViewById(R.id.editDescricaoProduto);
        editPreco = (EditText) findViewById(R.id.editPrecoProduto);
        imagemProduto = (ImageView) findViewById(R.id.imageCadastroProduto);

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            // Recorta a imagem em 125x125
            Bitmap imageBitmap = ThumbnailUtils.extractThumbnail((Bitmap) extras.get("data"),500,500,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            new GoogleVisionAPI().execute(imageBitmap);
            imagemProduto.setImageBitmap(imageBitmap);

        }
    }
    //endregion

    //region WEBSERVICE
    private class GoogleVisionAPI extends AsyncTask<Bitmap, Void, BatchAnnotateImagesResponse> {
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
                progress = new ProgressDialog(CadastroProdutoPessoaJuridicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(CadastroProdutoPessoaJuridicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected BatchAnnotateImagesResponse doInBackground(final Bitmap... params) {
            try {
                HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                VisionRequestInitializer requestInitializer =
                        new VisionRequestInitializer(getString(R.string.google_vision_api)) {
                            /**
                             * We override this so we can inject important identifying fields into the HTTP
                             * headers. This enables use of a restricted cloud platform API key.
                             */
                            @Override
                            protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                    throws IOException {
                                super.initializeVisionRequest(visionRequest);

                                String packageName = getPackageName();
                                visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                            }
                        };

                Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                builder.setVisionRequestInitializer(requestInitializer);

                Vision vision = builder.build();

                BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                        new BatchAnnotateImagesRequest();
                batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                    AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                    // Add the image
                    com.google.api.services.vision.v1.model.Image base64EncodedImage = new com.google.api.services.vision.v1.model.Image();
                    // Convert the bitmap to a JPEG
                    // Just in case it's a format that Android understands but Cloud Vision
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    params[0].compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();

                    // Base64 encode the JPEG
                    base64EncodedImage.encodeContent(imageBytes);
                    annotateImageRequest.setImage(base64EncodedImage);

                    // add the features we want
                    annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                        Feature labelDetection = new Feature();
                        labelDetection.setType("TEXT_DETECTION");
                        labelDetection.setMaxResults(10);
                        add(labelDetection);
                    }{
                        Feature labelDetection = new Feature();
                        labelDetection.setType("LABEL_DETECTION");
                        labelDetection.setMaxResults(10);
                        add(labelDetection);
                    }});

                    // Add the list of one thing to the request
                    add(annotateImageRequest);
                }});

                Vision.Images.Annotate annotateRequest =
                        vision.images().annotate(batchAnnotateImagesRequest);
                // Due to a bug: requests to Vision API containing large images fail when GZipped.
                annotateRequest.setDisableGZipContent(true);

                BatchAnnotateImagesResponse response = annotateRequest.execute();
                return response;

            } catch (GoogleJsonResponseException e) {
                Toast.makeText(CadastroProdutoPessoaJuridicaActivity.this, "Erro Ao chamar google vision", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(CadastroProdutoPessoaJuridicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(CadastroProdutoPessoaJuridicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            return null;
        }

        @Override
        protected void onPostExecute(BatchAnnotateImagesResponse response) {
            super.onPostExecute(response);
            if(isConnected)
            {
                List<EntityAnnotation> textAnnotations = response.getResponses().get(0).getTextAnnotations();
                List<EntityAnnotation> labelAnnotations = response.getResponses().get(0).getLabelAnnotations();
                if (textAnnotations != null) {
                    editNome.setText(textAnnotations.get(0).getDescription().replace("\n", " "));
                }
                if(labelAnnotations != null)
                {
                    String categorias = "";
                    for(int i = 0; i < labelAnnotations.size(); i++)
                    {
                        if(i != labelAnnotations.size() - 1)
                            categorias += labelAnnotations.get(i).getDescription() + "; ";
                        else
                            categorias += labelAnnotations.get(i).getDescription();
                    }
                    editCategoriaProduto.setText(categorias);
                }
                progress.dismiss();
            }

        }
    }

    private class SalvarDados extends AsyncTask<Produto, Void, String> {
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
                progress = new ProgressDialog(CadastroProdutoPessoaJuridicaActivity.this);
                progress.setMessage("Carregando...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setProgress(0);
                progress.show();
            }
            else{
                Toast.makeText(CadastroProdutoPessoaJuridicaActivity.this, "Verifique a conexão com a internet...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Produto... params) {
            Gson gson = new Gson();
            String json = gson.toJson(params[0]);
            String produto = HttpMetods.POST("Produto/Cadastrar",gson.toJson(params[0]));
            return produto;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isConnected)
            {
                if(s != null)
                {
                    try{
                        JSONObject object = new JSONObject(s);
                        object.remove("type");
                        Gson gson = new Gson();
                        if(object.getBoolean("ok"))
                        {
                            String json = object.getJSONObject("produtoEntity").toString();
                            Produto produto = gson.fromJson(json, Produto.class);
                            ClearEdits();
                            startActivity(new Intent(CadastroProdutoPessoaJuridicaActivity.this,MeusProdutosPessoaJuridicaActivity.class));
                        }


                    }catch (Exception e)
                    {
                        Toast.makeText(CadastroProdutoPessoaJuridicaActivity.this,"Não foi possivel se conectar",Toast.LENGTH_LONG).show();
                    }
                    progress.dismiss();
                }

            }

        }
    }
    //endregion

    //region BUTTONS METODS
    public void CadastrarProduto(View v){
        //region VALIDACAO
        if (editCategoriaProduto.getText().toString().length() == 0) {
            editCategoriaProduto.setError("É obrigatorio!");
            return;
        }
        if (editMarca.getText().toString().length() == 0) {
            editMarca.setError("É obrigatorio!");
            return;
        }
        if (editNome.getText().toString().length() == 0) {
            editNome.setError("É obrigatorio!");
            return;
        }
        if (editDescricao.getText().toString().length() == 0) {
            editDescricao.setError("É obrigatorio!");
            return;
        }
        if (editPreco.getText().toString().length() == 0) {
            editPreco.setError("É obrigatorio!");
            return;
        }
        //endregion

        imagemProduto.buildDrawingCache();
        Bitmap bmap = imagemProduto.getDrawingCache();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteImage = stream.toByteArray();
        String bytesEnconded = Base64.encodeToString(byteImage,Base64.DEFAULT);

        String jsonPrefe = Util.RecuperarUsuario("pessoaJuridica", CadastroProdutoPessoaJuridicaActivity.this);
        Gson gson = new Gson();
        PessoaJuridica pessoaJuridica = gson.fromJson(jsonPrefe, PessoaJuridica.class);

        Produto produto = new Produto(new Categoria_Produto(editCategoriaProduto.getText().toString()),
                new Marca_Produto(editMarca.getText().toString()),
                pessoaJuridica,
                editNome.getText().toString(),
                editDescricao.getText().toString(),
                editPreco.getText().toString(),
                bytesEnconded,
                new Timestamp(System.currentTimeMillis()));
        new SalvarDados().execute(produto);

    }
    public void TirarFotoProduto(View v)
    {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intentCamera.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(intentCamera, REQUEST_IMAGE_CAPTURE);
        }
    }

    //endregion

    //region OTHER METODS

    public void ClearEdits()
    {
        editCategoriaProduto.getText().clear();
        editMarca.getText().clear();
        editNome.getText().clear();
        editDescricao.getText().clear();
        editPreco.getText().clear();
    }
    //endregion
}
