package pt.bamer.bamermachina.fragmentos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import pt.bamer.bamermachina.ActivityListaOS;
import pt.bamer.bamermachina.BancadaTrabalho;
import pt.bamer.bamermachina.MrApp;
import pt.bamer.bamermachina.R;
import pt.bamer.bamermachina.adapters.OSRecyclerAdapter;
import pt.bamer.bamermachina.database.DBSQLite;
import pt.bamer.bamermachina.pojos.JSONObjectQtd;
import pt.bamer.bamermachina.pojos.JSONObjectTimer;
import pt.bamer.bamermachina.pojos.OSBI;
import pt.bamer.bamermachina.pojos.OSBO;
import pt.bamer.bamermachina.pojos.OSPROD;
import pt.bamer.bamermachina.pojos.OSTIMER;
import pt.bamer.bamermachina.utils.Constantes;
import pt.bamer.bamermachina.utils.Funcoes;
import pt.bamer.bamermachina.webservices.WebServices;

/**
 * Criado por miguel.silva on 10-02-2017.
 */

public class FragPai extends Fragment {
    private static final String TAG = FragPai.class.getSimpleName();
    private LinearLayout ll_working_os;
    private TextView tv_os;
    private TextView tv_tempo_total;
    private TextView tv_tempo_parcial;
    private TextView tv_qtt_total;
    private Button bt_qtt_feita;
    private RecyclerView recyclerView;
    private OSRecyclerAdapter osRecyclerAdapter;
    private BancadaTrabalho bancadaTrabalho;
    private SmoothProgressBar pb_smooth;
    private ValueEventListener listenerFirebaseOSBO;
    private DatabaseReference refFirebaseOSBO;
    private DatabaseReference refFirebaseOSTIMER;
    private ValueEventListener listenerFirebaseOSTIMER;

    public static FragPai newInstance() {
        return new FragPai();
    }

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof Activity){
//            this.listener = (FragmentActivity) context;
//        }
    }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ArrayList<Thing> things = new ArrayList<Thing>();
//        adapter = new ThingsAdapter(getActivity(), things);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragm_listaos, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        ListView lv = (ListView) view.findViewById(R.id.lvSome);
//        lv.setAdapter(adapter);


        pb_smooth = (SmoothProgressBar) getActivity().findViewById(R.id.pb_smooth);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_dossier);
        ll_working_os = (LinearLayout) view.findViewById(R.id.ll_working_os);
        tv_os = (TextView) view.findViewById(R.id.tv_os);
        tv_tempo_total = (TextView) view.findViewById(R.id.tv_tempo_total);
        tv_tempo_parcial = (TextView) view.findViewById(R.id.tv_tempo_parcial);
        tv_qtt_total = (TextView) view.findViewById(R.id.tv_qtt_total);
        bt_qtt_feita = (Button) view.findViewById(R.id.bt_qtt_feita);
        Button bt_start_stop_OS_Em_Trabalho = (Button) view.findViewById(R.id.bt_stop);

        this.bancadaTrabalho = new BancadaTrabalho(this);

        ll_working_os.setVisibility(View.GONE);
        tv_tempo_total.setVisibility(View.INVISIBLE);
        tv_tempo_parcial.setVisibility(View.INVISIBLE);
        LinearLayoutManager recyclerViewLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        osRecyclerAdapter = new OSRecyclerAdapter(getContext(), bancadaTrabalho);
        recyclerView.setAdapter(osRecyclerAdapter);

        ll_working_os.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OSBO osbo = bancadaTrabalho.getOsbo();
                if (osbo == null) {
                    Funcoes.alerta(getContext(), "Erro", "O servidor está ocupado. Tente dentro de momentos");
                    return;
                }
                ((ActivityListaOS) getActivity()).mostrarFragmentoDetalhe(osbo.bostamp, Constantes.MODO_STARTED, osbo.obrano);
            }
        });


        bt_qtt_feita.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(getContext());
                @SuppressLint("InflateParams") View promptsView = li.inflate(R.layout.popup_qtt, null);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
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
                                        emitirQtdProduzidaPorAvulso(Integer.parseInt(userInput.getText().toString()));
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


        bt_start_stop_OS_Em_Trabalho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObjectTimer jsonObject = new JSONObjectTimer(bancadaTrabalho.getOsbo().bostamp, "", Constantes.ESTADO_CORTE, Constantes.MODO_STOPED);
                    WebServices.registarTempoemSQL(getContext(), jsonObject, bancadaTrabalho);
                    ll_working_os.setVisibility(View.GONE);
                    bancadaTrabalho.pararCronometro();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        //FIREBASE
        FirebaseDatabase databaseref = FirebaseDatabase.getInstance();

        refFirebaseOSBO = databaseref.getReference(Constantes.NODE_OSBO);
        listenerFirebaseOSBO = new ValueEventListener() {
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

        DatabaseReference refFirebaseOSPROD = databaseref.getReference(Constantes.NODE_OSPROD);
        ValueEventListener listenerFirebaseOSPROD = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new TaskFirebaseOSPROD(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        refFirebaseOSPROD.addValueEventListener(listenerFirebaseOSPROD);

        refFirebaseOSTIMER = databaseref.getReference(Constantes.NODE_OSTIMER);
        listenerFirebaseOSTIMER = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new TaskFirebaseOSTIMER(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        refFirebaseOSTIMER.addValueEventListener(listenerFirebaseOSTIMER);

    }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
//        this.listener = null;
        refFirebaseOSBO.removeEventListener(listenerFirebaseOSBO);
        refFirebaseOSTIMER.removeEventListener(listenerFirebaseOSTIMER);
    }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void emitirQtdProduzidaPorAvulso(int qtd) {
        int qtd_anterior = Integer.parseInt(bt_qtt_feita.getText().toString());
        try {
            if (bancadaTrabalho.getOsbo() == null) {
                Funcoes.alerta(getContext(), "Erro", "O servidor está ocupado. Tente dentro de momentos");
                return;
            }
            String dim = "";
            String mk = "";
            String ref = "";
            String design = "Qtd Avulso";
            String bostamp = bancadaTrabalho.getOsbo().bostamp;
            JSONObjectQtd jsonObjectQtd = new JSONObjectQtd(bostamp, dim, mk, ref, design, qtd, "");
            WebServices.registarQtdEmSQL(getContext(), bt_qtt_feita, qtd_anterior, qtd, jsonObjectQtd);
        } catch (JSONException e) {
            e.printStackTrace();
            Funcoes.alerta(getContext(), "ERRO", "Erro ao construir o objecto JSON.\nActivityListaOS - método emitirQtdProduzidaPorAvulso");
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public LinearLayout getLl_working_os() {
        return ll_working_os;
    }

    public TextView getTv_os() {
        return tv_os;
    }

    public TextView getTv_tempo_total() {
        return tv_tempo_total;
    }

    public TextView getTv_tempo_parcial() {
        return tv_tempo_parcial;
    }

    public TextView getTv_qtt_total() {
        return tv_qtt_total;
    }

    public Button getBt_qtt_feita() {
        return bt_qtt_feita;
    }

    private class TaskFirebaseOSBO extends AsyncTask<Void, Void, Void> {
        private final ArrayList<OSBO> listaOSBO;
        private final DataSnapshot dataSnapShot;

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

            new DBSQLite(getActivity()).gravarOSBO(listaOSBO);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            osRecyclerAdapter.updateSourceData(bancadaTrabalho);
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
            new DBSQLite(getContext()).gravarOSBI(listaOSBI);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            osRecyclerAdapter.updateSourceData(bancadaTrabalho);
            pb_smooth.setVisibility(View.INVISIBLE);
        }
    }

    private class TaskFirebaseOSPROD extends AsyncTask<Void, Void, Void> {
        private final DataSnapshot snap;
        private final ArrayList<OSPROD> listaOSPROD;

        public TaskFirebaseOSPROD(DataSnapshot dataSnapshot) {
            this.snap = dataSnapshot;
            this.listaOSPROD = new ArrayList<>();
            pb_smooth.setVisibility(View.VISIBLE);
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
                    Log.v(TAG, "osbostamp:  " + osprod.bostamp + ", bistamp = " + osprod.bistamp + ", qtt = " + osprod.qtt);
                }
            }
            try {
                new DBSQLite(getActivity()).gravarOSPROD(listaOSPROD, "Método TaskFirebaseOSPROD na classe " + TAG);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            osRecyclerAdapter.updateSourceData(bancadaTrabalho);
            pb_smooth.setVisibility(View.INVISIBLE);
        }
    }

    private class TaskFirebaseOSTIMER extends AsyncTask<Void, Void, Void> {
        private final ArrayList<OSTIMER> listaOSTIMER;
        private final DataSnapshot dataSnapShot;

        public TaskFirebaseOSTIMER(DataSnapshot dataSnapshot) {
            this.listaOSTIMER = new ArrayList<>();
            this.dataSnapShot = dataSnapshot;
        }

        @Override
        protected void onPreExecute() {
            pb_smooth.setVisibility(View.VISIBLE);
            Log.i(TAG, "TaskFirebaseOSTIMER Secção " + MrApp.getSeccao() + ", estado " + MrApp.getEstado());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (DataSnapshot snapshotOSTIMER : dataSnapShot.getChildren()) {
                String bostamp = snapshotOSTIMER.getKey();
                for (DataSnapshot snap : snapshotOSTIMER.getChildren()) {
                    OSTIMER ostimer = snap.getValue(OSTIMER.class);
                    String bistamp = snap.getKey();
                    ostimer.bostamp = bostamp;
                    ostimer.bistamp = bistamp;
                    if (ostimer.seccao.equals(MrApp.getSeccao())
                            && ostimer.estado.equals(MrApp.getEstado())
                            )
                        listaOSTIMER.add(ostimer);
                }
            }
            Log.i(TAG, "listaOSTIMER: " + listaOSTIMER.size());

            try {
                new DBSQLite(getContext()).gravarOSTIMER(listaOSTIMER);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            osRecyclerAdapter.updateSourceData(bancadaTrabalho);
            pb_smooth.setVisibility(View.INVISIBLE);
        }
    }
}
