package pt.bamer.bamermachina.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;

import pt.bamer.bamermachina.ActivityListaOS;
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
                bt_alertas.setVisibility(View.GONE);

            } else {
                bt_posicao.setVisibility(View.INVISIBLE);
                bt_alertas.setVisibility(View.GONE);
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
        private final TextView tv_qttFeita;
        private final LinearLayout ll_root;
        private final Context context;
        private final int pecas;
        private int qttProd;

        public TaskCalculoQtt(String bostamp, int pecas, TextView tv_qttFeita, LinearLayout ll_root) {
            this.bostamp = bostamp;
            this.tv_qttFeita = tv_qttFeita;
            this.ll_root = ll_root;
            this.context = ll_root.getContext();
            this.pecas = pecas;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            qttProd = new DBSQLite(context).getQtdProdBostamp(bostamp);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            tv_qttFeita.setText("" + (qttProd == 0 ? "" : qttProd));
            if (pecas == qttProd) {
                ll_root.setBackgroundColor(ContextCompat.getColor(ll_root.getContext(), R.color.md_blue_grey_200));
            } else {
                ll_root.setBackgroundColor(Color.WHITE);
            }
        }
    }
}
