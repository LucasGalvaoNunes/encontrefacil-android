package opetbrothers.com.encontrefacil.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import opetbrothers.com.encontrefacil.Model.PessoaJuridica;
import opetbrothers.com.encontrefacil.R;
import opetbrothers.com.encontrefacil.Util.Util;

public class MainPessoaJuridicaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //region ATRIBUTES VIEW
    TextView textViewLojaLogada;

    //endregion

    PessoaJuridica pessoaJuridica;

    //region ANDROID METODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pessoa_juridica);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cadastraProduto = new Intent(MainPessoaJuridicaActivity.this,CadastroProdutoPessoaJuridicaActivity.class);
                startActivity(cadastraProduto);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView username = (TextView) header.findViewById(R.id.textViewLojaNome);
        ImageView imageLoja = (ImageView) header.findViewById(R.id.imageLojaMain);

        String jsonPrefe = Util.RecuperarUsuario("pessoaJuridica", MainPessoaJuridicaActivity.this);
        Gson gson = new Gson();
        pessoaJuridica = gson.fromJson(jsonPrefe, PessoaJuridica.class);
        if(pessoaJuridica.getFk_Pessoa().getFoto() != null)
        {
            byte[] foto = Base64.decode(pessoaJuridica.getFk_Pessoa().getFoto(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
            imageLoja.setImageBitmap(bitmap);
        }
        username.setText(pessoaJuridica.getRazao_Social());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_pessoa_juridica, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId())
        {
            case R.id.notification_pessoaFisica:
                return  true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (item.getItemId())
        {
            case R.id.nav_meusProdutos:
                startActivity(new Intent(MainPessoaJuridicaActivity.this,MeusProdutosPessoaJuridicaActivity.class));
                return true;
            case R.id.nav_meusDados:
                startActivity(new Intent(MainPessoaJuridicaActivity.this,MeusDadosPessoaJuridicaActivity.class));
                return true;
            case R.id.nav_relatorio:
                startActivity(new Intent(MainPessoaJuridicaActivity.this,RelatorioPessoaJuridicaActivity.class));
                return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //endregion

    //region BUTTON METODS
    public void LogoffPessoaJuridica(View v)
    {
        Util.SalvarDados("pessoaJuridica","", MainPessoaJuridicaActivity.this);
        Intent login = new Intent(MainPessoaJuridicaActivity.this,LoginActivity.class);
        startActivity(login);
        finish();
    }
    //endregion
}
