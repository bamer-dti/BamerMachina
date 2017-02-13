package pt.bamer.bamermachina.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import pt.bamer.bamermachina.MrApp;
import pt.bamer.bamermachina.R;
import pt.bamer.bamermachina.pojos.ObjSMS;
import pt.bamer.bamermachina.utils.Constantes;

/**
 * Criado por miguel.silva on 06-02-2017.
 */
public class RecyclerAdapterSMS extends FirebaseRecyclerAdapter<ObjSMS, RecyclerAdapterSMS.ViewHolder> {
    @SuppressWarnings("unused")
    private static final String TAG = RecyclerAdapterSMS.class.getSimpleName();
    private final int modoSMS;
    private Context context;

    public RecyclerAdapterSMS(Class<ObjSMS> objSMSClass, int layout, Class<ViewHolder> viewHolderClass, Query query, Context context, int modoSMS) {
        super(objSMSClass, layout, viewHolderClass, query);
        this.context = context;
        this.modoSMS = modoSMS;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_linha_sms, parent, false);
        return new RecyclerAdapterSMS.ViewHolder(view);
    }

    @Override
    protected void populateViewHolder(ViewHolder viewHolder, ObjSMS objSMS, int position) {
        viewHolder.popularObjecto(objSMS, position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_root;
        private TextView tv_data;
        private TextView tv_titulo;
        private TextView tv_corpo;
        private CheckBox chk_lida;

        public ViewHolder(View view) {
            super(view);
            ll_root = (LinearLayout) view.findViewById(R.id.ll_root);
            tv_data = (TextView) view.findViewById(R.id.tv_data);
            tv_titulo = (TextView) view.findViewById(R.id.tv_titulo);
            tv_corpo = (TextView) view.findViewById(R.id.tv_corpo);
            chk_lida = (CheckBox) view.findViewById(R.id.chk_lida);
        }

        public void popularObjecto(final ObjSMS objSMS, final int position) {
            DateTime someDate = new DateTime(objSMS.getTempostamp());
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            String data = formatter.format(someDate.getMillis());
            tv_data.setText(data);
            tv_titulo.setText(objSMS.getTitulo());
            tv_corpo.setText(objSMS.getMensagem());
            chk_lida.setChecked(objSMS.isLida());
            chk_lida.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    objSMS.setLida(isChecked);
                    if (isChecked) {
                        objSMS.setLidaQuem(MrApp.getOperadorCodigo());
                        objSMS.setLidastamp(System.currentTimeMillis());
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constantes.TAG_SMS);
                        if (modoSMS == Constantes.SMS_MACHINA) {
                            ref.child(Constantes.TAG_NAOLIDAS).child(MrApp.getMaquina()).child(objSMS.getId()).removeValue();
                            ref.child(Constantes.TAG_LIDAS).child(MrApp.getMaquina()).child(objSMS.getId()).setValue(objSMS);
                        }
                        if (modoSMS == Constantes.SMS_OPERADOR) {
                            ref.child(Constantes.TAG_NAOLIDAS).child(MrApp.getOperadorCodigo()).child(objSMS.getId()).removeValue();
                            ref.child(Constantes.TAG_LIDAS).child(MrApp.getOperadorCodigo()).child(objSMS.getId()).setValue(objSMS);
                        }
                    }
                }
            });
            if (position % 2 == 0) {
                ll_root.setBackgroundColor(ContextCompat.getColor(context, R.color.md_blue_grey_50));
            } else {
                ll_root.setBackgroundColor(ContextCompat.getColor(context, R.color.md_white_1000));
            }
        }
    }
}
