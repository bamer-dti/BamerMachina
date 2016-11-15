package pt.bamer.bamermachina;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import pt.bamer.bamermachina.database.DBSQLite;
import pt.bamer.bamermachina.pojos.Machina;
import pt.bamer.bamermachina.pojos.Operador;
import pt.bamer.bamermachina.utils.Constantes;
import pt.bamer.bamermachina.utils.ValoresDefeito;

public class Entrada extends AppCompatActivity {

    private static final String TAG = Entrada.class.getSimpleName();
    private Spinner spinn_seccao;
    private TableLayout tbl;
    private SmoothProgressBar pb_smooth;
    private Spinner spinner_maquina;
    private Spinner spinner_funcionario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada);

        setTitle(MrApp.getTituloBase(this));

        new DBSQLite(this).resetDados();

        spinn_seccao = (Spinner) findViewById(R.id.spinn_seccao);
        final SharedPreferences prefs = MrApp.getPrefs();
        final String seccao = prefs.getString(Constantes.PREF_SECCAO, ValoresDefeito.SECCAO);

        tbl = (TableLayout) findViewById(R.id.tbl);
        tbl.setVisibility(View.GONE);

        pb_smooth = (SmoothProgressBar) findViewById(R.id.pb_smooth);
        pb_smooth.setVisibility(View.VISIBLE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constantes.NODE_SECCAO);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Machina> listaMachinas = new ArrayList<>();
                ArrayList<String> listaSeccao = new ArrayList<>();
                for (DataSnapshot snapSeccao : dataSnapshot.getChildren()) {
                    String secc = snapSeccao.getKey();
                    listaSeccao.add(secc);
                    for (DataSnapshot snapMaqFunc : snapSeccao.getChildren()) {
                        if (snapMaqFunc.getKey().equals(Constantes.NODE_MAQUINAS)) {
                            for (DataSnapshot snapMaquinas : snapMaqFunc.getChildren()) {
                                String maq = snapMaquinas.getKey();
                                Machina machina = snapMaquinas.getValue(Machina.class);
                                machina.seccao = secc;
                                machina.ref = maq;
                                new DBSQLite(spinn_seccao.getContext()).gravarMachina(machina);
                            }
                        }
                        if (snapMaqFunc.getKey().equals(Constantes.NODE_FUNCIONARIOS)) {
                            for (DataSnapshot snapFuncionarios : snapMaqFunc.getChildren()) {
                                int no = Integer.parseInt(snapFuncionarios.getKey());
                                Operador operador = snapFuncionarios.getValue(Operador.class);
                                operador.seccao = secc;
                                operador.no = no;
                                new DBSQLite(spinn_seccao.getContext()).gravarFuncionario(operador);
                            }
                        }
                    }
                }

                String[] array_seccoes = new String[listaSeccao.size()];
                array_seccoes = listaSeccao.toArray(array_seccoes);
                spinn_seccao.setAdapter(new ArrayAdapter<>(spinn_seccao.getContext(), android.R.layout.simple_spinner_dropdown_item, array_seccoes));
                int pos = Arrays.asList(array_seccoes).indexOf(seccao);
                spinn_seccao.setSelection(pos);

                tbl.setVisibility(View.VISIBLE);
                pb_smooth.setVisibility(View.GONE);

                MrApp.setListaDeMachinas(listaMachinas);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.i(TAG, "SECÇÃO PREFERENCE: " + seccao);
//        String[] array_seccoes = getResources().getStringArray(R.array.array_seccao);
//        int pos = Arrays.asList(array_seccoes).indexOf(seccao);
//        spinn_seccao.setSelection(pos);
        spinn_seccao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, final View view, int i, long l) {
                String novaSeccao = adapterView.getItemAtPosition(i).toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constantes.PREF_SECCAO, novaSeccao);
                editor.commit();
                Log.i(TAG, "Alterou a secção, vamos ajustar as máquinas e funcionários!");

                ArrayList<String> listaMaquinas = new DBSQLite(spinn_seccao.getContext()).getArrayMaquinasDaSeccao(novaSeccao);
                spinner_maquina.setAdapter(new ArrayAdapter<>(spinn_seccao.getContext(), android.R.layout.simple_spinner_dropdown_item, listaMaquinas));

                ArrayList<String> listaFuncionarios = new DBSQLite(spinn_seccao.getContext()).getArrayFuncionariosDaSeccao(novaSeccao);
                spinner_funcionario.setAdapter(new ArrayAdapter<>(spinn_seccao.getContext(), android.R.layout.simple_spinner_dropdown_item, listaFuncionarios));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_maquina = (Spinner) findViewById(R.id.spinner_maquina);
        spinner_funcionario = (Spinner) findViewById(R.id.spinner_funcionario);

        final Button butok = (Button) findViewById(R.id.butok);
        butok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MrApp.setMaquina(spinner_maquina.getSelectedItem().toString());
                MrApp.setOperadorCodigo(spinner_funcionario.getSelectedItem().toString(), butok.getContext());
                Intent intent = new Intent(view.getContext(), ActivityListaOS.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}
