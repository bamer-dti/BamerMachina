package pt.bamer.bamermachina.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;

import pt.bamer.bamermachina.ActivityListaOS;
import pt.bamer.bamermachina.MrApp;
import pt.bamer.bamermachina.R;
import pt.bamer.bamermachina.adapters.OSRecyclerAdapter;
import pt.bamer.bamermachina.database.DBSQLite;

public class AsyncTasks {
    public static class TaskCalcularTempo extends android.os.AsyncTask<Void, Void, Void> {

        private final Button bt_posicao;
        private final String bostamp;
        private final TextView tv_temporal;
        private final Timer timer;
        private final Context context;
        private final Button bt_alertas;
        private long tempoCalculado;
        private int posicaoSQL = 0;

        public TaskCalcularTempo(String bostamp, OSRecyclerAdapter.ViewHolder viewHolder, Activity activity) {
            this.bostamp = bostamp;
            this.bt_posicao = viewHolder.getBt_posicao();
            this.bt_alertas = viewHolder.getBt_alertas();
            this.tv_temporal = viewHolder.getTv_temporal();
            ActivityListaOS oriActivity;
            if (activity instanceof ActivityListaOS)
                oriActivity = (ActivityListaOS) activity;
            else
                oriActivity = null;
            this.timer = oriActivity == null ? null : oriActivity.getBancadaTrabalho().getCronometroOS();
            this.context = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            posicaoSQL = new DBSQLite(context).getOSTimerPosicao(bostamp);
            tempoCalculado = new DBSQLite(context).getTotalTempoBostamp(bostamp);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (timer == null) {
                bt_posicao.setVisibility(View.VISIBLE);
                bt_alertas.setVisibility(View.VISIBLE);

            } else {
                bt_posicao.setVisibility(View.INVISIBLE);
                bt_alertas.setVisibility(View.INVISIBLE);
            }
            String texto = bt_posicao.getContext().getString(R.string.iniciar_upper);
            texto = posicaoSQL == Constantes.MODO_STOPED ? bt_posicao.getContext().getString(R.string.continuar_upper) : texto;
            bt_posicao.setText(texto);
            String textoTempo = Funcoes.milisegundos_em_HH_MM_SS(tempoCalculado * 1000);
            tv_temporal.setText("" + (tempoCalculado == 0 ? "" : textoTempo));
        }
    }

    public static class TaskCalculoQtt extends android.os.AsyncTask<Void, Void, Void> {

        private final String bostamp;
        private final TextView tv_qtt;
        private final TextView tv_qttFeita;
        private final LinearLayout ll_root;
        private final OSRecyclerAdapter osRecyclerAdapter;
        private final int position;
        private final Context context;
        private int qtt;
        private int qttProd;

        public TaskCalculoQtt(String bostamp, TextView tv_qtt, TextView tv_qttFeita, LinearLayout ll_root, OSRecyclerAdapter adapter, int position) {
            this.bostamp = bostamp;
            this.tv_qtt = tv_qtt;
            this.tv_qttFeita = tv_qttFeita;
            this.ll_root = ll_root;
            this.osRecyclerAdapter = adapter;
            this.position = position;
            this.context = ll_root.getContext();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            qtt = new DBSQLite(context).getQtdBostamp(bostamp);
            qttProd = new DBSQLite(context).getQtdProdBostamp(bostamp);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            tv_qtt.setText("" + qtt);
            tv_qttFeita.setText("" + (qttProd == 0 ? "" : qttProd));
            if (qtt == qttProd) {
                ll_root.setBackgroundColor(ContextCompat.getColor(ll_root.getContext(), R.color.md_blue_grey_500));
                SharedPreferences prefs = MrApp.getPrefs();
                boolean mostra = prefs.getBoolean(Constantes.PREF_MOSTRAR_OS_COMPLETOS, true);
                if (!mostra) {
                    osRecyclerAdapter.getListaOSBO().remove(position);
                    osRecyclerAdapter.notifyItemRemoved(position);
                    osRecyclerAdapter.notifyItemRangeChanged(position, osRecyclerAdapter.getListaOSBO().size());
                }
            } else {
                ll_root.setBackgroundColor(Color.WHITE);
            }
        }
    }
}
