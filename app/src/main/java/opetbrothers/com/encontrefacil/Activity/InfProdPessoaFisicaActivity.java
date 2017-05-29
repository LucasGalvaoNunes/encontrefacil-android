package opetbrothers.com.encontrefacil.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import opetbrothers.com.encontrefacil.R;

public class InfProdPessoaFisicaActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    MapView mMapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inf_prod_pessoa_fisica);

        mMapView = (MapView) findViewById(R.id.mapViewInfProd);

        if(mMapView != null)
        {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-33.852, 151.211);
        mMap.addMarker(new MarkerOptions().position(sydney)
                .title("Loja"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 25));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_infprod_pessoafisica,menu);
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
            case R.id.infAvaliacoes:
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(InfProdPessoaFisicaActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_avaliacao_produto, null);
                // ADD HERE ALL USABLE ITEMS
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
