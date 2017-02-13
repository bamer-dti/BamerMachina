package pt.bamer.bamermachina.fragmentos;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import pt.bamer.bamermachina.ActivityListaOS;
import pt.bamer.bamermachina.R;
import pt.bamer.bamermachina.adapters.TarefaRecyclerAdapter;
import pt.bamer.bamermachina.database.DBSQLite;
import pt.bamer.bamermachina.pojos.OSBI;
import pt.bamer.bamermachina.pojos.OSPROD;
import pt.bamer.bamermachina.utils.Constantes;

/**
 * Criado por miguel.silva on 10-02-2017.
 */
public class FragDetalheOS extends Fragment {
    private static final String TAG = FragDetalheOS.class.getSimpleName();
    private SmoothProgressBar pb_smooth;
    private TarefaRecyclerAdapter tarefaRecyclerAdapter;
    private String bostamp;
    private Toolbar toolbar;
    private FragDetalheOS frag;

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ActivityListaOS activity = (ActivityListaOS) getActivity();
        FragmentManager sf = activity.getSupportFragmentManager();
        FragmentTransaction ft = sf.beginTransaction();
        Fragment f = sf.findFragmentByTag(ActivityListaOS.TAG_FRAG_PAI);
        if (f != null) {
            ft.hide(f);
        }
        ft.commit();
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ArrayList<Thing> things = new ArrayList<Thing>();
//        adapter = new ThingsAdapter(getActivity(), things);
        frag = this;
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dossier, parent, false);
        try {
            toolbar = (Toolbar) view.findViewById(R.id.toolbar_frag);
            toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_blue_100));

            //Botão HOME
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityListaOS act = (ActivityListaOS) getActivity();
                    FragmentManager sf = act.getSupportFragmentManager();
                    FragmentTransaction ft = sf.beginTransaction();
                    Fragment f = sf.findFragmentByTag(ActivityListaOS.TAG_FRAG_PAI);
                    if (f != null) {
                        ft.show(f);
                        ft.remove(frag);
                        ft.commit();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        ListView lv = (ListView) view.findViewById(R.id.lvSome);
//        lv.setAdapter(adapter);
        bostamp = getArguments().getString("bostamp");
        int modoOperacional = getArguments().getInt("modo");
        int obrano = getArguments().getInt("obrano");

        Log.i(TAG, "Parametros: bostamp = '" + bostamp + "'; modo = " + modoOperacional);

        toolbar.setTitle("OS " + obrano);

        pb_smooth = (SmoothProgressBar) getActivity().findViewById(R.id.pb_smooth);
        pb_smooth.setVisibility(View.INVISIBLE);

        Paint paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(Color.DKGRAY);
        paint.setAntiAlias(true);
        paint.setPathEffect(new DashPathEffect(new float[]{25.0f, 25.0f}, 0));

        RecyclerView recyclerViewDossier = (RecyclerView) view.findViewById(R.id.recycler_view_dossier);
        recyclerViewDossier.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext()).paint(paint).build());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewDossier.setLayoutManager(mLayoutManager);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerViewDossier.setItemAnimator(itemAnimator);

        tarefaRecyclerAdapter = new TarefaRecyclerAdapter(getContext(), modoOperacional);
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

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        ActivityListaOS activity = (ActivityListaOS) getActivity();
        FragmentManager sf = activity.getSupportFragmentManager();
        FragmentTransaction ft = sf.beginTransaction();
        Fragment f = sf.findFragmentByTag(ActivityListaOS.TAG_FRAG_PAI);
        if (f != null) {
            ft.show(f);
            ft.commit();
        } else {
            Log.e(TAG, "O fragmento principal é NULO?!?");
        }

//        this.listener = null;
    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
            pb_smooth.setVisibility(View.INVISIBLE);
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

            new DBSQLite(getActivity()).gravarOSBIParcial(lista);

            listaDoArrayOSBI = new DBSQLite(getActivity()).getOSBIAgrupada();

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
                    Log.d(TAG, "osbostamp:  " + osprod.bostamp + ", bistamp = " + osprod.bistamp + ", qtt = " + osprod.qtt);
                }
            }
            try {
                //TODO analisar porque dá isto erro de contexto NULO!
                new DBSQLite(getContext()).gravarOSPROD(listaOSPROD, "Método TaskFirebaseOSPROD na classe " + TAG);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tarefaRecyclerAdapter.notifyDataSetChanged();
                }
            });
        }
    }

}
