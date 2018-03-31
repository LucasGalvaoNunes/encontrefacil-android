package opetbrothers.com.encontrefacil.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Lucas Galvao Nunes on 10/06/2017.
 */

public class AlarmProdDestaqueReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"THIS IS MY ALARM",Toast.LENGTH_LONG).show();
    }
}
