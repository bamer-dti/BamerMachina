package pt.bamer.bamermachina;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Timer;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import pt.bamer.bamermachina.adapters.OSRecyclerAdapter;
import pt.bamer.bamermachina.database.dbHelper;
import pt.bamer.bamermachina.pojos.JSONObjectQtd;
import pt.bamer.bamermachina.pojos.JSONObjectTimer;
import pt.bamer.bamermachina.pojos.OSBI;
import pt.bamer.bamermachina.pojos.OSBO;
import pt.bamer.bamermachina.pojos.OSPROD;
import pt.bamer.bamermachina.utils.Constantes;
import pt.bamer.bamermachina.utils.Funcoes;
import pt.bamer.bamermachina.webservices.WebServices;

public class ListaOS extends AppCompatActivity {
    private ListaOS activityListaOS = this;
    private static String TAG = ListaOS.class.getSimpleName();
    private LinearLayout ll_working_os;
    private TextView tv_os;
    private TextView tv_tempo_total;
    private TextView tv_tempo_parcial;
    private TextView tv_qtt_total;
    private TextView bt_qtt_feita;
    private OSBO keyBaseStarted;
    public Timer cronometroOS;
    private ListaOS activityContext = this;
    private RecyclerView recyclerView;
    private Menu menu;
    private SmoothProgressBar pb_smooth;
    private OSBO documentoEmTrabalho;
    private OSRecyclerAdapter osRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_listaos);

        pb_smooth = (SmoothProgressBar) findViewById(R.id.pb_smooth);
        pb_smooth.setVisibility(View.INVISIBLE);

        //noinspection ConstantConditions
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        ll_working_os = (LinearLayout) findViewById(R.id.ll_working_os);
        ll_working_os.setVisibility(View.GONE);

        tv_os = (TextView) findViewById(R.id.tv_os);

        tv_tempo_total = (TextView) findViewById(R.id.tv_tempo_total);
        tv_tempo_total.setVisibility(View.INVISIBLE);

        tv_tempo_parcial = (TextView) findViewById(R.id.tv_tempo_parcial);
        tv_tempo_parcial.setVisibility(View.INVISIBLE);

        tv_qtt_total = (TextView) findViewById(R.id.tv_qtt_total);
        ll_working_os.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (keyBaseStarted == null) {
                    Funcoes.alerta(activityListaOS, "Erro", "O servidor está ocupado. Tente dentro de momentos");
                    return;
                }
                Intent intent = new Intent(view.getContext(), Dossier.class);
                intent.putExtra(Constantes.INTENT_EXTRA_BOSTAMP, keyBaseStarted.bostamp);
                intent.putExtra(Constantes.INTENT_EXTRA_MODO_OPERACIONAL, Constantes.MODO_STARTED);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        bt_qtt_feita = (TextView) findViewById(R.id.bt_qtt_feita);
        bt_qtt_feita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(activityListaOS);
                @SuppressLint("InflateParams")
                View promptsView = li.inflate(R.layout.popup_qtt, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        activityListaOS);
                alertDialogBuilder.setView(promptsView);
                final EditText userInput = (EditText) promptsView.findViewById(R.id.et_qtt);
                int qttTotal = Integer.parseInt(tv_qtt_total.getText().toString());
                int qttParcial = Integer.parseInt(bt_qtt_feita.getText().toString());
                int qttRestante = qttTotal - qttParcial;
                userInput.setHint("" + qttRestante);
                userInput.setText("" + qttRestante);
                userInput.setSelection(userInput.getText().length());
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        emitirQtdProduzidaPorAvulso(activityListaOS, Integer.parseInt(userInput.getText().toString()));
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        Button bt_stop_OS_Em_Trabalho = (Button) findViewById(R.id.bt_stop);
        bt_stop_OS_Em_Trabalho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObjectTimer jsonObject = new JSONObjectTimer(keyBaseStarted.bostamp, "", Constantes.ESTADO_CORTE, 2, -1);
                    WebServices.registarTempoemSQL(activityContext, jsonObject);
                    ll_working_os.setVisibility(View.GONE);
                    pararCronometro();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        fazerRecyclerViewOSBO(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pararCronometro();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listaos, menu);
        this.menu = menu;
        boolean visi = MrApp.getPrefs().getBoolean(Constantes.PREF_MOSTRAR_OS_COMPLETOS, true);
        menu.findItem(R.id.itemmenu_mostrar_tudo).setTitle(visi ? Constantes.MOSTRAR_TUDO : Constantes.MOSTRAR_FILTRADO);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemmenu_mostrar_tudo:
                SharedPreferences prefs = MrApp.getPrefs();
                boolean now = prefs.getBoolean(Constantes.PREF_MOSTRAR_OS_COMPLETOS, true);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(Constantes.PREF_MOSTRAR_OS_COMPLETOS, !now);
                editor.commit();
                actionbarSetup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void emitirQtdProduzidaPorAvulso(Context context, int qtd) {
        int qtd_anterior = Integer.parseInt(bt_qtt_feita.getText().toString());
        try {
            if (documentoEmTrabalho == null) {
                Funcoes.alerta(context, "Erro", "O servidor está ocupado. Tente dentro de momentos");
                return;
            }
            String dim = "";
            String mk = "";
            String ref = "";
            String design = "Qtd Avulso";
            String bostamp = documentoEmTrabalho.bostamp;
            JSONObjectQtd jsonObjectQtd = new JSONObjectQtd(bostamp, dim, mk, ref, design, qtd);
            WebServices.registarQtdEmSQL(activityContext, bt_qtt_feita, qtd_anterior, qtd, jsonObjectQtd);
        } catch (JSONException e) {
            e.printStackTrace();
            Funcoes.alerta(activityContext, "ERRO", "Erro ao construir o objecto JSON.\nListaOS - método emitirQtdProduzidaPorAvulso");
        }
    }

    private void fazerRecyclerViewOSBO(Context context) {
        FirebaseDatabase databaseref = FirebaseDatabase.getInstance();
        DatabaseReference ref = databaseref.getReference();
        ValueEventListener listenerFirebase = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new TaskFirebase(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        osRecyclerAdapter = new OSRecyclerAdapter(context, new ArrayList<OSBO>());
        recyclerView.setAdapter(osRecyclerAdapter);
        ref.addValueEventListener(listenerFirebase);
    }

    private void iniciarTemposOSAposReplicacao(String bostamp) {
        Log.i(TAG, "iniciarTemposOSAposReplicacao: '" + bostamp + "' em trabalho visivel!");
    }

    private void pararCronometro() {
        Log.i(TAG, "********** A parar o cronometro...");
        if (cronometroOS != null) {
            cronometroOS.cancel();
            cronometroOS.purge();
            cronometroOS = null;
        }
    }

    private void actionbarSetup() {
        SharedPreferences prefs = MrApp.getPrefs();
        boolean vis = prefs.getBoolean(Constantes.PREF_MOSTRAR_OS_COMPLETOS, true);
        menu.findItem(R.id.itemmenu_mostrar_tudo).setTitle(vis ? Constantes.MOSTRAR_TUDO : Constantes.MOSTRAR_FILTRADO);
        new aplicarAdapter().execute();
    }

    public Timer getCronometroOS() {
        return null;
    }

    private class aplicarAdapter extends AsyncTask<Void, Void, Void> {
        private boolean vis;

        private ArrayList<OSBO> listaDeOs;

        @Override
        protected void onPreExecute() {
            SharedPreferences prefs = MrApp.getPrefs();
            vis = prefs.getBoolean(Constantes.PREF_MOSTRAR_OS_COMPLETOS, true);
            MrApp.mostrarAlertToWait(activityContext, "A organizar dados...");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            listaDeOs = new ArrayList<>();
            if (listaDeOs.size() != 0) {
                for (int i = 0; i < listaDeOs.size(); i++) {
                    OSBO osbo = listaDeOs.get(i);
                    @SuppressWarnings("unchecked")
                    String bostamp = osbo.bostamp;

                    //Está em modo started?
                    if (getPosicao(bostamp) == Constantes.MODO_STARTED) {
                        iniciarTemposOSAposReplicacao(bostamp);
                        keyBaseStarted = osbo;
                    } else {
                        if (vis) {
                            listaDeOs.add(osbo);
                        } else {
                            int qttProd = 0;
                            int qttPed = 0;
                            if (qttProd != qttPed) {
                                listaDeOs.add(osbo);
                            }
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(TAG, "A mostrar o OSRecyclerAdapter...");
            final OSRecyclerAdapter adapter = new OSRecyclerAdapter(activityListaOS, listaDeOs);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setAdapter(adapter);
                }
            });
            MrApp.esconderAlertToWait(activityListaOS);
        }
    }

    public static int getPosicao(String bostamp) {
        return Constantes.MODO_STOPED;
    }

    private class TaskFirebase extends AsyncTask<Void, Void, Void> {
        private final ArrayList<OSBO> listaOSBO;
        private final ArrayList<OSBI> listaOSBI;
        private final ArrayList<OSPROD> listaOSPROD;
        private final DataSnapshot dataSnapShot;
        private final ArrayList<OSBO> listaInspeccao;

        public TaskFirebase(DataSnapshot dataSnapshot) {
            this.listaOSBO = new ArrayList<>();
            this.listaOSBI = new ArrayList<>();
            this.listaOSPROD = new ArrayList<>();
            this.listaInspeccao = new ArrayList<>();
            this.dataSnapShot = dataSnapshot;
        }

        @Override
        protected void onPreExecute() {
            pb_smooth.setVisibility(View.VISIBLE);
            Log.i(TAG, "Secção " + MrApp.getSeccao() + ", estado " + MrApp.getEstado());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (DataSnapshot snap : dataSnapShot.getChildren()) {
                Log.i(TAG, "SNAP KEY: " + snap.getKey());
                if (snap.getKey().equals(Constantes.NODE_OSBO)) {
                    for (DataSnapshot snapshotOSBO : snap.getChildren()) {
                        String bostamp = snapshotOSBO.getKey();
                        OSBO osbo = snapshotOSBO.getValue(OSBO.class);
                        osbo.bostamp = bostamp;
                        if (osbo.seccao.equals(MrApp.getSeccao())
                                && osbo.estado.equals(MrApp.getEstado())) {
                            listaOSBO.add(osbo);
                        }
                    }
                }
                if (snap.getKey().equals(Constantes.NODE_OSBI)) {
                    for (DataSnapshot snapshotOSBO : snap.getChildren()) {
                        String bostamp = snapshotOSBO.getKey();
                        for (DataSnapshot dataSnapshotOSBI : snapshotOSBO.getChildren()) {
                            OSBI osbi = dataSnapshotOSBI.getValue(OSBI.class);
                            osbi.bostamp = bostamp;
                            osbi.bistamp = dataSnapshotOSBI.getKey();
                            listaOSBI.add(osbi);
                        }
                    }
                }

                if (snap.getKey().equals(Constantes.NODE_OSPROD)) {
                    for (DataSnapshot snapshotOSPROD : snap.getChildren()) {
                        String bostamp = snapshotOSPROD.getKey();
                        for (DataSnapshot dataSnapshotOSPROD : snapshotOSPROD.getChildren()) {
                            OSPROD osprod = dataSnapshotOSPROD.getValue(OSPROD.class);
                            osprod.bostamp = bostamp;
                            osprod.bistamp = dataSnapshotOSPROD.getKey();
                            listaOSPROD.add(osprod);
                        }
                    }
                }
            }
            Log.i(TAG, "listaOSBO: " + listaOSBO.size() + ", listaOSBI: " + listaOSBI.size() + ", lista OSPROD: " + listaOSPROD.size());

            new dbHelper(activityContext).gravarOSBO(listaOSBO);
            new dbHelper(activityContext).gravarOSBI(listaOSBI);
            new dbHelper(activityContext).gravarOSPROD(listaOSPROD);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            osRecyclerAdapter.updateSourceData(activityContext);

            pb_smooth.setVisibility(View.INVISIBLE);
        }
    }
}
