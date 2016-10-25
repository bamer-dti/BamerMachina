package pt.bamer.bamermachina;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import pt.bamer.bamermachina.adapters.TarefaRecyclerAdapter;
import pt.bamer.bamermachina.database.DBSQLite;
import pt.bamer.bamermachina.pojos.OSBI;
import pt.bamer.bamermachina.pojos.OSPROD;
import pt.bamer.bamermachina.utils.Constantes;

///**
// * Created by miguel.silva on 19-07-2016.
// */
public class Dossier extends AppCompatActivity {
    private static final String TAG = Dossier.class.getSimpleName();
    Dossier activity = this;
    private RecyclerView recyclerViewDossier;
    private Menu menu;
    private String bostamp;
    private int modoOperacional;
    private SmoothProgressBar pb_smooth;
    private TarefaRecyclerAdapter tarefaRecyclerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dossier);
        pb_smooth = (SmoothProgressBar) findViewById(R.id.pb_smooth);
        pb_smooth.setVisibility(android.view.View.INVISIBLE);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Paint paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(Color.DKGRAY);
        paint.setAntiAlias(true);
        paint.setPathEffect(new DashPathEffect(new float[]{25.0f, 25.0f}, 0));

        recyclerViewDossier = (RecyclerView) findViewById(R.id.recycler_view_dossier);
        recyclerViewDossier.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this).paint(paint).build());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerViewDossier.setLayoutManager(mLayoutManager);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerViewDossier.setItemAnimator(itemAnimator);

        Bundle extras = getIntent().getExtras();
        bostamp = "";
        modoOperacional = 0;
        if (extras != null) {
            bostamp = extras.getString(Constantes.INTENT_EXTRA_BOSTAMP);
            modoOperacional = extras.getInt(Constantes.INTENT_EXTRA_MODO_OPERACIONAL);
        }

        tarefaRecyclerAdapter = new TarefaRecyclerAdapter(this, modoOperacional);
        recyclerViewDossier.setAdapter(tarefaRecyclerAdapter);

        DatabaseReference refOSBI = FirebaseDatabase.getInstance().getReference(Constantes.NODE_OSBI).child(bostamp);
        refOSBI.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new TaskFirebaseOSBI(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference refOSPROD = FirebaseDatabase.getInstance().getReference(Constantes.NODE_OSPROD);
        refOSPROD.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new TaskFirebaseOSPROD(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_listaos, menu);
        this.menu = menu;
        boolean visi = MrApp.getPrefs().getBoolean(Constantes.PREF_MOSTRAR_TODAS_LINHAS_PROD, true);
        menu.findItem(R.id.itemmenu_mostrar_tudo).setTitle(visi ? Constantes.MOSTRAR_TUDO : Constantes.MOSTRAR_FILTRADO);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case R.id.itemmenu_mostrar_tudo:
                SharedPreferences prefs = MrApp.getPrefs();
                boolean now = prefs.getBoolean(Constantes.PREF_MOSTRAR_TODAS_LINHAS_PROD, true);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(Constantes.PREF_MOSTRAR_TODAS_LINHAS_PROD, !now);
                editor.commit();
                actionbarSetup();
////                onBackPressed();
//                return true;
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void actionbarSetup() {
        SharedPreferences prefs = MrApp.getPrefs();
        boolean vis = prefs.getBoolean(Constantes.PREF_MOSTRAR_TODAS_LINHAS_PROD, true);
        menu.findItem(R.id.itemmenu_mostrar_tudo).setTitle(vis ? Constantes.MOSTRAR_TUDO : Constantes.MOSTRAR_FILTRADO);
    }

    private class TaskFirebaseOSBI extends AsyncTask<Void, Void, Void> {
        private final DataSnapshot dataSnapShot;
        private ArrayList<OSBI> listaDoArrayOSBI;

        public TaskFirebaseOSBI(DataSnapshot dataSnapshot) {
            this.listaDoArrayOSBI = new ArrayList<>();
            this.dataSnapShot = dataSnapshot;
        }

        @Override
        protected void onPreExecute() {
            pb_smooth.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<OSBI> lista = new ArrayList<>();
            for (DataSnapshot dataSnapshotOSBI : dataSnapShot.getChildren()) {
                OSBI osbi = dataSnapshotOSBI.getValue(OSBI.class);
                osbi.bostamp = bostamp;
                osbi.bistamp = dataSnapshotOSBI.getKey();
                Log.i(TAG, "OSBI " + osbi.toString());
                lista.add(osbi);
            }

            new DBSQLite(activity).gravarOSBIParcial(lista);

            Log.i(TAG, "listaOSBIPARCIAL: " + lista.size());

            listaDoArrayOSBI = new DBSQLite(activity).getOSBIAgrupada();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pb_smooth.setVisibility(View.INVISIBLE);
            tarefaRecyclerAdapter.updateSource(listaDoArrayOSBI);
        }
    }

    private class TaskFirebaseOSPROD extends AsyncTask<Void, Void, Void> {
        private final DataSnapshot dataSnapShot;

        public TaskFirebaseOSPROD(DataSnapshot dataSnapshot) {
            this.dataSnapShot = dataSnapshot;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<OSPROD> listaOSPROD = new ArrayList<>();
            for (DataSnapshot snapshotOSPROD : dataSnapShot.getChildren()) {
                String bostamp = snapshotOSPROD.getKey();
                for (DataSnapshot dataSnapshotOSPROD : snapshotOSPROD.getChildren()) {
                    OSPROD osprod = dataSnapshotOSPROD.getValue(OSPROD.class);
                    osprod.bostamp = bostamp;
                    osprod.bistamp = dataSnapshotOSPROD.getKey();
                    listaOSPROD.add(osprod);
                    Log.i(TAG, "osbostamp:  " + osprod.bostamp + ", bistamp = " + osprod.bistamp + ", qtt = " + osprod.qtt);
                }
            }
            new DBSQLite(activity).gravarOSPROD(listaOSPROD);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tarefaRecyclerAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
