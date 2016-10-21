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
import pt.bamer.bamermachina.database.DBSQLite;
import pt.bamer.bamermachina.pojos.JSONObjectQtd;
import pt.bamer.bamermachina.pojos.JSONObjectTimer;
import pt.bamer.bamermachina.pojos.OSBI;
import pt.bamer.bamermachina.pojos.OSBO;
import pt.bamer.bamermachina.pojos.OSPROD;
import pt.bamer.bamermachina.utils.Constantes;
import pt.bamer.bamermachina.utils.Funcoes;
import pt.bamer.bamermachina.webservices.WebServices;

public class ListaOS extends AppCompatActivity {
    private static String TAG = ListaOS.class.getSimpleName();
    public Timer cronometroOS;
    private ListaOS activityListaOS = this;
    private LinearLayout ll_working_os;
    private TextView tv_os;
    private TextView tv_tempo_total;
    private TextView tv_tempo_parcial;
    private TextView tv_qtt_total;
    private TextView bt_qtt_feita;
    private OSBO objectoOSBOIniciado;
    private ListaOS contextActivity = this;
    private RecyclerView recyclerView;
    private Menu menu;
    private SmoothProgressBar pb_smooth;
    private OSBO documentoEmTrabalho;
    private OSRecyclerAdapter osRecyclerAdapter;

    public static int getPosicao(String bostamp) {
        return Constantes.MODO_STARTED;
    }

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
        osRecyclerAdapter = new OSRecyclerAdapter(contextActivity);
        recyclerView.setAdapter(osRecyclerAdapter);

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
                if (objectoOSBOIniciado == null) {
                    Funcoes.alerta(activityListaOS, "Erro", "O servidor está ocupado. Tente dentro de momentos");
                    return;
                }
                Intent intent = new Intent(view.getContext(), Dossier.class);
                intent.putExtra(Constantes.INTENT_EXTRA_BOSTAMP, objectoOSBOIniciado.bostamp);
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
                    JSONObjectTimer jsonObject = new JSONObjectTimer(objectoOSBOIniciado.bostamp, "", Constantes.ESTADO_CORTE, 2, -1);
                    WebServices.registarTempoemSQL(contextActivity, jsonObject);
                    ll_working_os.setVisibility(View.GONE);
                    pararCronometro();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        FirebaseDatabase databaseref = FirebaseDatabase.getInstance();
        DatabaseReference refFirebaseOSBO = databaseref.getReference().child(Constantes.NODE_OSBO);
        ValueEventListener listenerFirebaseOSBO = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new TaskFirebaseOSBO(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        refFirebaseOSBO.addValueEventListener(listenerFirebaseOSBO);

        DatabaseReference refFirebaseOSBI = databaseref.getReference(Constantes.NODE_OSBI);
        ValueEventListener listenerFirebaseOSBI = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new TaskFirebaseOSBI(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        refFirebaseOSBI.addValueEventListener(listenerFirebaseOSBI);

        DatabaseReference refFirebaseOSPROD = databaseref.getReference().child("osprod");
        ValueEventListener listenerFirebase = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new TaskFirebaseOSPROD(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        refFirebaseOSPROD.addValueEventListener(listenerFirebase);
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
            WebServices.registarQtdEmSQL(contextActivity, bt_qtt_feita, qtd_anterior, qtd, jsonObjectQtd);
        } catch (JSONException e) {
            e.printStackTrace();
            Funcoes.alerta(contextActivity, "ERRO", "Erro ao construir o objecto JSON.\nListaOS - método emitirQtdProduzidaPorAvulso");
        }
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
        osRecyclerAdapter.updateSourceData();
    }

    public Timer getCronometroOS() {
        return null;
    }

    private class TaskFirebaseOSBO extends AsyncTask<Void, Void, Void> {
        private final ArrayList<OSBO> listaOSBO;
        private final DataSnapshot dataSnapShot;
        private boolean changed;

        public TaskFirebaseOSBO(DataSnapshot dataSnapshot) {
            this.listaOSBO = new ArrayList<>();
            this.dataSnapShot = dataSnapshot;
        }

        @Override
        protected void onPreExecute() {
            pb_smooth.setVisibility(View.VISIBLE);
            Log.i(TAG, "Secção " + MrApp.getSeccao() + ", estado " + MrApp.getEstado());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (DataSnapshot snapshotOSBO : dataSnapShot.getChildren()) {
                String bostamp = snapshotOSBO.getKey();
                OSBO osbo = snapshotOSBO.getValue(OSBO.class);
                osbo.bostamp = bostamp;
                if (osbo.seccao.equals(MrApp.getSeccao())
                        && osbo.estado.equals(MrApp.getEstado())) {
                    listaOSBO.add(osbo);

                }
            }
            Log.i(TAG, "listaOSBO: " + listaOSBO.size());

            new DBSQLite(contextActivity).gravarOSBO(listaOSBO);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            osRecyclerAdapter.updateSourceData();
            pb_smooth.setVisibility(View.INVISIBLE);
        }
    }

    private class TaskFirebaseOSBI extends AsyncTask<Void, Void, Void> {
        private final ArrayList<OSBI> listaOSBI;
        private final DataSnapshot dataSnapShot;

        public TaskFirebaseOSBI(DataSnapshot dataSnapshot) {
            this.listaOSBI = new ArrayList<>();
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
                String bostamp = snap.getKey();
                for (DataSnapshot dataSnapshotOSBI : snap.getChildren()) {
                    OSBI osbi = dataSnapshotOSBI.getValue(OSBI.class);
                    osbi.bostamp = bostamp;
                    osbi.bistamp = dataSnapshotOSBI.getKey();
//                    Log.i(TAG, "bostamp = " + osbi.bostamp + ", bistamp = " + osbi.bistamp);
                    listaOSBI.add(osbi);
                }
            }
            Log.i(TAG, "listaOSBI: " + listaOSBI.size());
            new DBSQLite(contextActivity).gravarOSBI(listaOSBI);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            osRecyclerAdapter.updateSourceData();
            pb_smooth.setVisibility(View.INVISIBLE);
        }
    }

    private class TaskFirebaseOSPROD extends AsyncTask<Void, Void, Void> {
        private final DataSnapshot snap;
        private final ArrayList<OSPROD> listaOSPROD;

        public TaskFirebaseOSPROD(DataSnapshot dataSnapshot) {
            this.snap = dataSnapshot;
            this.listaOSPROD = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (DataSnapshot snapshotOSPROD : snap.getChildren()) {
                String bostamp = snapshotOSPROD.getKey();
                for (DataSnapshot dataSnapshotOSPROD : snapshotOSPROD.getChildren()) {
                    OSPROD osprod = dataSnapshotOSPROD.getValue(OSPROD.class);
                    osprod.bostamp = bostamp;
                    osprod.bistamp = dataSnapshotOSPROD.getKey();
                    listaOSPROD.add(osprod);
                    Log.i(TAG, "osbostamp:  " + osprod.bostamp + ", bistamp = " + osprod.bistamp + ", qtt = " + osprod.qtt);
                }
            }
            Log.i(TAG, "listaOSPROD: " + listaOSPROD.size());
            new DBSQLite(contextActivity).gravarOSPROD(listaOSPROD);

            ArrayList<OSPROD> listaProd = new DBSQLite(contextActivity).getProdAgrupadaPorBostamp();
            ArrayList<OSBO> listaOSBO = osRecyclerAdapter.getListaOSBO();
            for (OSPROD osprod : listaProd) {
                for (int i = 0; i < listaOSBO.size(); i++) {
                    OSBO osbo = listaOSBO.get(i);
                    if (osprod.bostamp.equals(osbo.bostamp)) {
                        osRecyclerAdapter.notificar(contextActivity, i);
                    }
                }
            }
            return null;
        }
    }

}
