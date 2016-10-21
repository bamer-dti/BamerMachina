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
import android.view.Menu;
import android.view.MenuItem;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import pt.bamer.bamermachina.adapters.OSRecyclerAdapter;
import pt.bamer.bamermachina.adapters.TarefaRecyclerAdapter;
import pt.bamer.bamermachina.pojos.OSBI;
import pt.bamer.bamermachina.utils.Constantes;

///**
// * Created by miguel.silva on 19-07-2016.
// */
public class Dossier extends AppCompatActivity {
    private static final String TAG = Dossier.class.getSimpleName();
    Dossier activity = this;
    private RecyclerView recyclerView;
    private Menu menu;
    private String bostamp;
    private int modoOperacional;
    private SmoothProgressBar pb_smooth;

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

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this).paint(paint).build());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);

        Bundle extras = getIntent().getExtras();
        bostamp = "";
        modoOperacional = 0;
        if (extras != null) {
            bostamp = extras.getString(Constantes.INTENT_EXTRA_BOSTAMP);
            modoOperacional = extras.getInt(Constantes.INTENT_EXTRA_MODO_OPERACIONAL);
        }

        RecyclerView.Adapter adapter = new OSRecyclerAdapter(this);
        recyclerView.setAdapter(adapter);
        MrApp.esconderAlertToWait(activity);
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

    private class efectuarAdapter extends AsyncTask<Void, Void, Void> {

        private boolean vertudo;
        private List<OSBI> listaOSBI;

        efectuarAdapter(ArrayList<OSBI> listaOSBI) {
            this.listaOSBI = listaOSBI;
        }

        @Override
        protected void onPreExecute() {
            SharedPreferences prefs = MrApp.getPrefs();
            vertudo = prefs.getBoolean(Constantes.PREF_MOSTRAR_TODAS_LINHAS_PROD, true);
            MrApp.mostrarAlertToWait(activity, "A organizar dados...");
        }


        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final TarefaRecyclerAdapter tarefaRecyclerAdapter = new TarefaRecyclerAdapter(activity, listaOSBI, modoOperacional);
            runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  recyclerView.setAdapter(tarefaRecyclerAdapter);
                              }
                          }
            );
            MrApp.esconderAlertToWait(activity);
        }
    }
}
