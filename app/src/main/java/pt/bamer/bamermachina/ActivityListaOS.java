package pt.bamer.bamermachina;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import pt.bamer.bamermachina.adapters.OSRecyclerAdapter;
import pt.bamer.bamermachina.database.DBSQLite;
import pt.bamer.bamermachina.firebasefcm.MyFirebaseInstanceIDService;
import pt.bamer.bamermachina.pojos.JSONObjectQtd;
import pt.bamer.bamermachina.pojos.JSONObjectTimer;
import pt.bamer.bamermachina.pojos.OSBI;
import pt.bamer.bamermachina.pojos.OSBO;
import pt.bamer.bamermachina.pojos.OSPROD;
import pt.bamer.bamermachina.pojos.OSTIMER;
import pt.bamer.bamermachina.pojos.ObjSMS;
import pt.bamer.bamermachina.utils.Constantes;
import pt.bamer.bamermachina.utils.Funcoes;
import pt.bamer.bamermachina.webservices.WebServices;

public class ActivityListaOS extends AppCompatActivity {
    private static String TAG = ActivityListaOS.class.getSimpleName();
    private ActivityListaOS activityActivityListaOS = this;
    private LinearLayout ll_working_os;
    private TextView tv_os;
    private TextView tv_tempo_total;
    private TextView tv_tempo_parcial;
    private TextView tv_qtt_total;
    private Button bt_qtt_feita;
    private ActivityListaOS contextActivity = this;
    private RecyclerView recyclerView;
    private SmoothProgressBar pb_smooth;
    private OSRecyclerAdapter osRecyclerAdapter;
    private BancadaTrabalho bancadaTrabalho;
    private LinearLayout sms_ll;
    private Button sms_bt_machina;
    private Button sms_bt_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_listaos);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle(MrApp.getTituloBase(this) + " (" + MrApp.getMaquina() + " * " + MrApp.getOperadorNome() + ")");
        myToolbar.setTitleTextColor(Color.WHITE);

        pb_smooth = (SmoothProgressBar) findViewById(R.id.pb_smooth);
        pb_smooth.setVisibility(View.INVISIBLE);

        //noinspection ConstantConditions
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_dossier);
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
                OSBO osbo = bancadaTrabalho.getOsbo();
                if (osbo == null) {
                    Funcoes.alerta(activityActivityListaOS, "Erro", "O servidor está ocupado. Tente dentro de momentos");
                    return;
                }
                Intent intent = new Intent(view.getContext(), Dossier.class);
                intent.putExtra(Constantes.INTENT_EXTRA_BOSTAMP, osbo.bostamp);
                intent.putExtra(Constantes.INTENT_EXTRA_MODO_OPERACIONAL, Constantes.MODO_STARTED);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        bt_qtt_feita = (Button) findViewById(R.id.bt_qtt_feita);
        bt_qtt_feita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(activityActivityListaOS);
                @SuppressLint("InflateParams")
                View promptsView = li.inflate(R.layout.popup_qtt, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        activityActivityListaOS);
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
                                        emitirQtdProduzidaPorAvulso(activityActivityListaOS, Integer.parseInt(userInput.getText().toString()));
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

        Button bt_start_stop_OS_Em_Trabalho = (Button) findViewById(R.id.bt_stop);
        bt_start_stop_OS_Em_Trabalho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObjectTimer jsonObject = new JSONObjectTimer(bancadaTrabalho.getOsbo().bostamp, "", Constantes.ESTADO_CORTE, Constantes.MODO_STOPED);
                    WebServices.registarTempoemSQL(contextActivity, jsonObject);
                    ll_working_os.setVisibility(View.GONE);
                    bancadaTrabalho.pararCronometro();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.bancadaTrabalho = new BancadaTrabalho(this);

        //FIREBASE
        FirebaseDatabase databaseref = FirebaseDatabase.getInstance();

        DatabaseReference refFirebaseOSBO = databaseref.getReference(Constantes.NODE_OSBO);
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

        DatabaseReference refFirebaseOSTIMER = databaseref.getReference(Constantes.NODE_OSTIMER);
        ValueEventListener listenerFirebaseOSTIMER = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new TaskFirebaseOSTIMER(dataSnapshot).execute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        refFirebaseOSTIMER.addValueEventListener(listenerFirebaseOSTIMER);

        Animation animation = new AlphaAnimation(1.0f, 0.25f); // Change alpha from fully visible to invisible
        animation.setDuration(200); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in

        sms_ll = (LinearLayout) findViewById(R.id.sms_ll);
        sms_ll.setVisibility(View.GONE);
        sms_ll.setAnimation(animation);

        sms_bt_machina = (Button) findViewById(R.id.sms_bt_machina);
        sms_bt_machina.setVisibility(View.INVISIBLE);
//        sms_bt_machina.startAnimation(animation);
        sms_bt_machina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contextActivity, ListaSms.class);
                intent.putExtra(Constantes.INTENT_EXTRA_SMS, Constantes.SMS_MACHINA);
                startActivity(intent);
            }
        });
        DatabaseReference refSMS = databaseref.getReference().child(Constantes.TAG_SMS).child(Constantes.TAG_NAOLIDAS).child(MrApp.getMaquina());
        refSMS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MrApp.contadorSMSMachina = 0;
                int count = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i(TAG, ds.toString());
                    ObjSMS obj = ds.getValue(ObjSMS.class);
                    Log.i(TAG, "Lida: " + obj.isLida());
                    if (!obj.isLida())
                        count++;
                }
                MrApp.contadorSMSMachina = count;
                actualizarMostradorSMS();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sms_bt_user = (Button) findViewById(R.id.sms_bt_user);
        sms_bt_user.setVisibility(View.GONE);
//        sms_bt_user.startAnimation(animation);
        sms_bt_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contextActivity, ListaSms.class);
                intent.putExtra(Constantes.INTENT_EXTRA_SMS, Constantes.SMS_OPERADOR);
                startActivity(intent);
            }
        });
        DatabaseReference refSMSUser = databaseref.getReference().child(Constantes.TAG_SMS).child(Constantes.TAG_NAOLIDAS).child(MrApp.getOperadorCodigo());
        refSMSUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MrApp.contadorSMSUser = 0;
                int count = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i(TAG, ds.toString());
                    ObjSMS obj = ds.getValue(ObjSMS.class);
                    Log.i(TAG, "Lida: " + obj.isLida());
                    if (!obj.isLida())
                        count++;
                }
                MrApp.contadorSMSUser = count;
                actualizarMostradorSMS();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void actualizarMostradorSMS() {
        sms_bt_machina.setVisibility(View.GONE);
        sms_bt_user.setVisibility(View.GONE);
        if (MrApp.contadorSMSMachina + MrApp.contadorSMSUser != 0) {
            sms_ll.setVisibility(View.VISIBLE);
        } else {
            sms_ll.setVisibility(View.GONE);
            return;
        }

        if (MrApp.contadorSMSMachina != 0) {
            sms_bt_machina.setText(getString(R.string.sms_maquina) + ": " + MrApp.contadorSMSMachina);
            sms_bt_machina.setVisibility(View.VISIBLE);
        }

        if (MrApp.contadorSMSUser != 0) {
            sms_bt_user.setText(getString(R.string.sms_operador) + ": " + MrApp.contadorSMSUser);
            sms_bt_user.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String token = FirebaseInstanceId.getInstance().getToken();
        MyFirebaseInstanceIDService.sendRegistrationToServer(token);
        Log.i(TAG, "CHECK_TOKEN: " + token);
        bancadaTrabalho.actualizarDados();
    }


    @Override
    protected void onPause() {
        MyFirebaseInstanceIDService.unRegisterUtilizador();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        MyFirebaseInstanceIDService.unRegisterUtilizador();
        super.onDestroy();
    }

    private void emitirQtdProduzidaPorAvulso(Context context, int qtd) {
        int qtd_anterior = Integer.parseInt(bt_qtt_feita.getText().toString());
        try {
            if (bancadaTrabalho.getOsbo() == null) {
                Funcoes.alerta(context, "Erro", "O servidor está ocupado. Tente dentro de momentos");
                return;
            }
            String dim = "";
            String mk = "";
            String ref = "";
            String design = "Qtd Avulso";
            String bostamp = bancadaTrabalho.getOsbo().bostamp;
            JSONObjectQtd jsonObjectQtd = new JSONObjectQtd(bostamp, dim, mk, ref, design, qtd, "");
            WebServices.registarQtdEmSQL(contextActivity, bt_qtt_feita, qtd_anterior, qtd, jsonObjectQtd);
        } catch (JSONException e) {
            e.printStackTrace();
            Funcoes.alerta(contextActivity, "ERRO", "Erro ao construir o objecto JSON.\nActivityListaOS - método emitirQtdProduzidaPorAvulso");
        }
    }

    public LinearLayout getLl_working_os() {
        return ll_working_os;
    }

    public TextView getTv_os() {
        return tv_os;
    }

    public TextView getTv_qtt_total() {
        return tv_qtt_total;
    }

    public Button getBt_qtt_feita() {
        return bt_qtt_feita;
    }

    public TextView getTv_tempo_total() {
        return tv_tempo_total;
    }

    public TextView getTv_tempo_parcial() {
        return tv_tempo_parcial;
    }

    public BancadaTrabalho getBancadaTrabalho() {
        return bancadaTrabalho;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
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

            new DBSQLite(contextActivity).gravarOSBO(listaOSBO);
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
            new DBSQLite(contextActivity).gravarOSBI(listaOSBI);
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
            new DBSQLite(contextActivity).gravarOSPROD(listaOSPROD);
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

            new DBSQLite(contextActivity).gravarOSTIMER(listaOSTIMER);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            osRecyclerAdapter.updateSourceData(bancadaTrabalho);
            pb_smooth.setVisibility(View.INVISIBLE);
        }
    }
}
