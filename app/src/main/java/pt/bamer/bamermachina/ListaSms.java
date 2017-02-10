package pt.bamer.bamermachina;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import pt.bamer.bamermachina.adapters.RecyclerAdapterSMS;
import pt.bamer.bamermachina.pojos.ObjSMS;
import pt.bamer.bamermachina.utils.Constantes;

/**
 * Criado por miguel.silva on 06-02-2017.
 */
public class ListaSms extends AppCompatActivity {
    private static final String TAG = ListaSms.class.getSimpleName();
    private RecyclerView recycler_v;
    private DatabaseReference refSMS;
    private RecyclerAdapterSMS recyclerAdapterSMS;
    private LinearLayoutManager linearLayoutManager;
    private Paint paint;
    private int modoSMS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lista_sms);

        modoSMS = getIntent().getIntExtra(Constantes.INTENT_EXTRA_SMS, Constantes.SMS_MACHINA);
        String textoTootalBar = modoSMS == Constantes.SMS_MACHINA ? getString(R.string.sms_maquina) : getString(R.string.sms_operador);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        setTitle(textoTootalBar);

        ActionBar sab = getSupportActionBar();
        if (sab != null) {
            sab.setDisplayHomeAsUpEnabled(true);
        }

        paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(Color.DKGRAY);
        paint.setAntiAlias(true);
        paint.setPathEffect(new DashPathEffect(new float[]{25.0f, 25.0f}, 0));

        recycler_v = (RecyclerView) findViewById(R.id.recycler_v);
        linearLayoutManager = new LinearLayoutManager(this);
        recycler_v.setLayoutManager(linearLayoutManager);

        if (modoSMS == Constantes.SMS_MACHINA) {
            refSMS = FirebaseDatabase.getInstance().getReference(Constantes.TAG_SMS).child(Constantes.TAG_NAOLIDAS).child(MrApp.getMaquina());
        }
        if (modoSMS == Constantes.SMS_OPERADOR) {
            refSMS = FirebaseDatabase.getInstance().getReference(Constantes.TAG_SMS).child(Constantes.TAG_NAOLIDAS).child(MrApp.getOperadorCodigo());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        attachRecyclerViewAdapter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if (recyclerAdapterSMS != null) {
            recyclerAdapterSMS.cleanup();
        }
        super.onStop();
    }

    private void attachRecyclerViewAdapter(Context context) {
        Query query = refSMS.orderByValue();
        recyclerAdapterSMS = new RecyclerAdapterSMS(ObjSMS.class, R.layout.layout_lista_sms, RecyclerAdapterSMS.ViewHolder.class, query, context, modoSMS);

        recycler_v.setAdapter(recyclerAdapterSMS);
        recycler_v.setLayoutManager(linearLayoutManager);

        recycler_v.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this).paint(paint).build());

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(100);
        itemAnimator.setRemoveDuration(50);
        recycler_v.setItemAnimator(itemAnimator);
    }
}
