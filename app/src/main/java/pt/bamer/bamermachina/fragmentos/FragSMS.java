package pt.bamer.bamermachina.fragmentos;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import pt.bamer.bamermachina.ActivityListaOS;
import pt.bamer.bamermachina.MrApp;
import pt.bamer.bamermachina.R;
import pt.bamer.bamermachina.adapters.RecyclerAdapterSMS;
import pt.bamer.bamermachina.pojos.ObjSMS;
import pt.bamer.bamermachina.utils.Constantes;

/**
 * Criado por miguel.silva on 10-02-2017.
 */
public class FragSMS extends Fragment {
    private static final String TAG = FragSMS.class.getSimpleName();
    private DatabaseReference refSMS;
    private int modoSMS;
    private RecyclerView recycler_v;
    private LinearLayoutManager linearLayoutManager;
    private Paint paint;
    private Toolbar toolbar;
    private FragSMS frag;
    private RecyclerAdapterSMS recyclerAdapterSMS;

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        frag = this;
//        ArrayList<Thing> things = new ArrayList<Thing>();
//        adapter = new ThingsAdapter(getActivity(), things);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_lista_sms, parent, false);

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
                    Log.i(TAG, "Clicou em Back na Toolbar");
                    fecharSelf();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void fecharSelf() {
        ActivityListaOS act = (ActivityListaOS) getActivity();
        FragmentManager sf = act.getSupportFragmentManager();
        FragmentTransaction ft = sf.beginTransaction();
        ft.remove(frag);
        ft.commit();
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        ListView lv = (ListView) view.findViewById(R.id.lvSome);
//        lv.setAdapter(adapter);


        modoSMS = getArguments().getInt("tipo");
        toolbar.setTitle("SMS " + (modoSMS == Constantes.SMS_OPERADOR ? "operador" : "máquina"));

        paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(Color.DKGRAY);
        paint.setAntiAlias(true);
        paint.setPathEffect(new DashPathEffect(new float[]{25.0f, 25.0f}, 0));

        recycler_v = (RecyclerView) getActivity().findViewById(R.id.recycler_v);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recycler_v.setLayoutManager(linearLayoutManager);

        if (modoSMS == Constantes.SMS_MACHINA) {
            refSMS = FirebaseDatabase.getInstance().getReference(Constantes.TAG_SMS).child(Constantes.TAG_NAOLIDAS).child(MrApp.getMaquina());
        }
        if (modoSMS == Constantes.SMS_OPERADOR) {
            refSMS = FirebaseDatabase.getInstance().getReference(Constantes.TAG_SMS).child(Constantes.TAG_NAOLIDAS).child(MrApp.getOperadorCodigo());
        }

        refSMS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        attachRecyclerViewAdapter();

        ActivityListaOS activity = (ActivityListaOS) getActivity();
        FragmentManager sf = activity.getSupportFragmentManager();
        FragmentTransaction ft = sf.beginTransaction();
        Fragment f = sf.findFragmentByTag(ActivityListaOS.TAG_FRAG_DETALHE_OS);
        if (f != null) {
            ft.hide(f);
        }
        f = sf.findFragmentByTag(ActivityListaOS.TAG_FRAG_PAI);
        if (f != null) {
            ft.hide(f);
        }
        ft.commit();
        Log.i(TAG, "onViewCreated()");
    }

    private void attachRecyclerViewAdapter() {
        Query query = refSMS.orderByValue();
        recyclerAdapterSMS = new RecyclerAdapterSMS(ObjSMS.class, R.layout.layout_lista_sms, RecyclerAdapterSMS.ViewHolder.class, query, getContext(), modoSMS);

        recycler_v.setAdapter(recyclerAdapterSMS);
        recycler_v.setLayoutManager(linearLayoutManager);

        recycler_v.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext()).paint(paint).build());

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(100);
        itemAnimator.setRemoveDuration(50);
        recycler_v.setItemAnimator(itemAnimator);

        recyclerAdapterSMS.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {

                int recs = recyclerAdapterSMS.getItemCount();
                Log.i(TAG, "OBSERVER positionStart: " + positionStart + "; itemCount: " + itemCount + "; recs: " + recs);
                if (recs <= 0) {
                    fecharSelf();
                }
            }
        });
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

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach()");
        ActivityListaOS activity = (ActivityListaOS) getActivity();
        FragmentManager sf = activity.getSupportFragmentManager();
        Fragment f;
        FragmentTransaction ft = sf.beginTransaction();
        f = sf.findFragmentByTag(ActivityListaOS.TAG_FRAG_DETALHE_OS);
        if (f != null) {
            Log.i(TAG, "O fragmento " + ActivityListaOS.TAG_FRAG_DETALHE_OS + " existe, vamos mostrar!");
            ft.show(f);
            ft.commit();
            return;
        }
        f = sf.findFragmentByTag(ActivityListaOS.TAG_FRAG_PAI);
        if (f != null) {
            ft.show(f);
            ft.commit();
        } else {
            Log.e(TAG, "O fragmento principal é NULO?!?");
        }

        if (recyclerAdapterSMS != null) {
            recyclerAdapterSMS.cleanup();
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
}
