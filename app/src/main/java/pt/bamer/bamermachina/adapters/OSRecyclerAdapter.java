package pt.bamer.bamermachina.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;

import java.util.ArrayList;

import pt.bamer.bamermachina.ActivityListaOS;
import pt.bamer.bamermachina.BancadaTrabalho;
import pt.bamer.bamermachina.Dossier;
import pt.bamer.bamermachina.MrApp;
import pt.bamer.bamermachina.R;
import pt.bamer.bamermachina.database.DBSQLite;
import pt.bamer.bamermachina.pojos.JSONObjectTimer;
import pt.bamer.bamermachina.pojos.OSBO;
import pt.bamer.bamermachina.utils.AsyncTasks;
import pt.bamer.bamermachina.utils.Constantes;
import pt.bamer.bamermachina.utils.Funcoes;
import pt.bamer.bamermachina.webservices.WebServices;

public class OSRecyclerAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private static final String TAG = OSRecyclerAdapter.class.getSimpleName();
    private final Context context;
    private final ActivityListaOS activityListaOS;
    private ArrayList<OSBO> listaOSBO;

    public OSRecyclerAdapter(Activity context) {
        this.context = context;
        this.activityListaOS = (ActivityListaOS) context;
        this.listaOSBO = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.view_osbo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        OSBO osbo = getItem(position);
        if (osbo == null) {
            return;
        }

        String dtcortef = osbo.dtcortef;
        final String bostamp = osbo.bostamp;

        String dttransf = osbo.dttransf;
        int obrano = osbo.obrano;
        String fref = osbo.fref;
        String nmfref = osbo.nmfref;
        String obs = osbo.obs;

        viewHolder.tv_fref.setText(fref + " - " + nmfref);
        viewHolder.tv_fref.setTag(osbo);
        viewHolder.tv_fref.setOnClickListener(this);

        viewHolder.tv_obrano.setText("OS " + obrano);
        viewHolder.tv_obrano.setOnClickListener(this);

        viewHolder.tv_descricao.setText(obs);

        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd.MM.yyyy");

        LocalDateTime localDateTime = Funcoes.cToT(dtcortef);
        viewHolder.tv_dtcortef.setText(dtf.print(localDateTime));

        localDateTime = Funcoes.cToT(dttransf);
        viewHolder.tv_dttransf.setText(dtf.print(localDateTime));

        new AsyncTasks.TaskCalculoQtt(bostamp, viewHolder.tv_qtt, viewHolder.tv_qttfeita, viewHolder.ll_root, this, position).execute();

        viewHolder.bt_posicao.setVisibility(View.INVISIBLE);
        if (activityListaOS != null) {
            new AsyncTasks.TaskCalcularTempo(bostamp, viewHolder, activityListaOS).execute();
        }

        viewHolder.bt_posicao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                JSONObjectTimer jsonObject;
                try {
                    jsonObject = new JSONObjectTimer(bostamp, "", Constantes.ESTADO_CORTE, Constantes.MODO_STARTED);
                    WebServices.registarTempoemSQL((Activity) context, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        viewHolder.bt_alertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "Não implementado!");
                Funcoes.alerta(view.getContext(), "Info", "Não implementado!");
            }
        });

        viewHolder.ll_root.setTag(bostamp);
        viewHolder.ll_root.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return listaOSBO == null ? 0 : listaOSBO.size();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.ll_root) {
            Intent intent = new Intent(view.getContext(), Dossier.class);
            intent.putExtra(Constantes.INTENT_EXTRA_BOSTAMP, view.getTag().toString());
            intent.putExtra(Constantes.INTENT_EXTRA_MODO_OPERACIONAL, Constantes.MODO_STOPED);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private OSBO getItem(int position) {
        return listaOSBO != null ? listaOSBO.get(position) : null;
    }

    public ArrayList<OSBO> getListaOSBO() {
        return listaOSBO;
    }

    public void updateSourceData(BancadaTrabalho bancadaTrabalho) {
        new UpdateSourceTask(bancadaTrabalho).execute();
    }

    public void removerOSBO(String bostamp) {
        Log.i(TAG, "Remover do OSRecyclerAdapter o bostamp " + bostamp);
        for (int i = 0; i < listaOSBO.size(); i++) {
            OSBO osbo = listaOSBO.get(i);
            if (osbo.bostamp.equals(bostamp)) {
                listaOSBO.remove(i);
                notifyItemRemoved(i);
                Log.i(TAG, "Notificar a remoção do item " + bostamp);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_fref;
        private final TextView tv_obrano;
        private final TextView tv_descricao;
        private final TextView tv_dtcortef;
        private final TextView tv_dttransf;
        private final TextView tv_qtt;
        private final TextView tv_qttfeita;
        private final LinearLayout ll_root;
        private final Button bt_posicao;
        private final Button bt_alertas;
        private final TextView tv_temporal;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_fref = (TextView) itemView.findViewById(R.id.tv_fref);
            tv_obrano = (TextView) itemView.findViewById(R.id.tv_obrano);
            tv_descricao = (TextView) itemView.findViewById(R.id.tv_descricao);
            tv_dtcortef = (TextView) itemView.findViewById(R.id.tv_dtcortef);
            tv_dttransf = (TextView) itemView.findViewById(R.id.tv_dttransf);
            tv_qtt = (TextView) itemView.findViewById(R.id.tv_qtt);
            tv_qttfeita = (TextView) itemView.findViewById(R.id.tv_qttfeita);
            ll_root = (LinearLayout) itemView.findViewById(R.id.ll_root);

            bt_posicao = (Button) itemView.findViewById(R.id.bt_posicao);
            bt_alertas = (Button) itemView.findViewById(R.id.bt_alertas);

            tv_temporal = (TextView) itemView.findViewById(R.id.tv_temporal);
        }

        public Button getBt_posicao() {
            return bt_posicao;
        }

        public Button getBt_alertas() {
            return bt_alertas;
        }

        public TextView getTv_temporal() {
            return tv_temporal;
        }
    }

    private class UpdateSourceTask extends AsyncTask<Void, Void, Void> {
        private final BancadaTrabalho bancadaTrabalho;

        public UpdateSourceTask(BancadaTrabalho bancadaTrabalho) {
            this.bancadaTrabalho = bancadaTrabalho;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<OSBO> lista = new DBSQLite(context).getOSBOOrdered();
            SharedPreferences prefs = MrApp.getPrefs();
            boolean mostrarTodos = prefs.getBoolean(Constantes.PREF_MOSTRAR_OS_COMPLETOS, true);
            Log.i(TAG, "mostrarTodos = " + mostrarTodos);
            if (mostrarTodos) {
                listaOSBO = lista;
            } else {
                for (OSBO osbo : lista) {
                    String bostamp = osbo.bostamp;
                    int qtt = new DBSQLite(context).getQtdBostamp(bostamp);
                    int qttProd = new DBSQLite(context).getQtdProdBostamp(bostamp);
                    if (qtt != qttProd) {
                        listaOSBO.add(osbo);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ((ActivityListaOS) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                    bancadaTrabalho.actualizarDados();
                }
            });
        }
    }
}
