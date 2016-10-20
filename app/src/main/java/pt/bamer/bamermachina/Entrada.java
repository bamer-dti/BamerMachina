package pt.bamer.bamermachina;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Arrays;

import pt.bamer.bamermachina.utils.Constantes;
import pt.bamer.bamermachina.utils.ValoresDefeito;

///**
// * Created by miguel.silva on 09-08-2016.
// */
public class Entrada extends AppCompatActivity {

    private static final String TAG = Entrada.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada);

        Spinner spinn_seccao = (Spinner) findViewById(R.id.spinn_seccao);
        final SharedPreferences prefs = MrApp.getPrefs();
        final String seccao = prefs.getString(Constantes.PREF_SECCAO, ValoresDefeito.SECCAO);

        Log.i(TAG, "SECÇÃO PREFERENCE: " + seccao);
        String[] array_seccoes = getResources().getStringArray(R.array.array_seccao);
        int pos = Arrays.asList(array_seccoes).indexOf(seccao);
        spinn_seccao.setSelection(pos);
        spinn_seccao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, final View view, int i, long l) {
                String novaSeccao = adapterView.getItemAtPosition(i).toString();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constantes.PREF_SECCAO, novaSeccao);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Spinner spinner_maquina = (Spinner) findViewById(R.id.spinner_maquina);
        final Spinner spinner_funcionario = (Spinner) findViewById(R.id.spinner_funcionario);

        Button butok = (Button) findViewById(R.id.butok);
        butok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MrApp.setMaquina(spinner_maquina.getSelectedItem().toString());
                MrApp.setOperador(spinner_funcionario.getSelectedItem().toString());
                Intent intent = new Intent(view.getContext(), ListaOS.class);
                startActivity(intent);
            }
        });
    }

    private void alertaExitApp(Context context) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage("A aplicação irá ser fechada para reconfigurar os dados");
        alertBuilder.setTitle("Reinicio!");
        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(0);
            }
        });

        alertBuilder.create();
        alertBuilder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}
