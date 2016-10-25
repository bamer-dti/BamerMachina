package pt.bamer.bamermachina;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import pt.bamer.bamermachina.adapters.OSRecyclerAdapter;
import pt.bamer.bamermachina.database.DBSQLite;
import pt.bamer.bamermachina.pojos.OSBO;
import pt.bamer.bamermachina.utils.Funcoes;


public class BancadaTrabalho {
    private static final String TAG = BancadaTrabalho.class.getSimpleName();
    private OSBO osbo;
    private ActivityListaOS activityListaOS;
    private LinearLayout ll_working_os;
    private TextView tv_os;
    private TextView tv_qtt_total;
    private Button bt_qtt_feita;
    private TextView tv_tempo_total;
    private TextView tv_tempo_parcial;
    private Timer cronometroOS;

    public BancadaTrabalho(ActivityListaOS activity) {
        this.activityListaOS = activity;
        this.ll_working_os = activity.getLl_working_os();
        this.tv_os = activity.getTv_os();
        this.tv_qtt_total = activity.getTv_qtt_total();
        this.bt_qtt_feita = activity.getBt_qtt_feita();
        this.tv_tempo_total = activity.getTv_tempo_total();
        this.tv_tempo_parcial = activity.getTv_tempo_parcial();
    }

    public void actualizarDados() {
        OSRecyclerAdapter adapter = (OSRecyclerAdapter) activityListaOS.getRecyclerView().getAdapter();
        //Verificar se a bancada continua em trabalho
        if (osbo != null) {
            int posicao = new DBSQLite(activityListaOS).getOSTimerPosicao(osbo.bostamp);
            if (posicao == -1) {
                Funcoes.alerta(activityListaOS, "Erro...", "A bancada deveria estar a trabalhar mas retornou código -1!");
            }
            if (posicao == 1) {
                adapter.removerOSBO(osbo.bostamp);
                return;
            }
            if (posicao == 2) {//STOPPED!!!
                osbo = null;
                ll_working_os.setVisibility(View.GONE);
                osbo = null;
            }
            return;
        }

        OSBO osboDB = new DBSQLite(activityListaOS).getOSBOemTrabalho();
        if (osboDB == null) {
            return;
        }
        if (osbo != null && osboDB.bostamp.equals(osbo.bostamp)) {
            Log.i(TAG, "OS " + osbo.obrano + " continua em tabalho");
        } else {
            osbo = osboDB;
            Log.i(TAG, "EXISTE UMA NOVA OS EM TRABALHO!");
            ll_working_os.setVisibility(View.VISIBLE);
            tv_os.setText("OS " + osbo.obrano);
            tv_qtt_total.setText("" + new DBSQLite(activityListaOS).getQtdBostamp(osbo.bostamp));
            bt_qtt_feita.setText("" + new DBSQLite(activityListaOS).getQtdProdBostamp(osbo.bostamp));
            tv_tempo_total.setText("HH:mm:ss");
            tv_tempo_parcial.setText("" + new DBSQLite(activityListaOS).getOSTimerPosicao(osbo.bostamp));
            adapter.removerOSBO(osbo.bostamp);

            iniciarCronometros(activityListaOS, osbo.bostamp);
        }
    }

    private void iniciarCronometros(final ActivityListaOS context, final String bostamp) {
        TimerTask actualizarTempos = new TimerTask() {
            @Override
            public void run() {
                final long tempoTotal = new DBSQLite(context).getTotalTempoBostamp(bostamp);
                long ultimoTempo = new DBSQLite(context).getUltimoTempo(bostamp);
                long unixNow = System.currentTimeMillis() / 1000L;
                final long intervaloTempo = unixNow - ultimoTempo;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String textoTempoTotal = "TT: " + Funcoes.milisegundos_em_HH_MM_SS(tempoTotal * 1000 + intervaloTempo * 1000);
                        tv_tempo_total.setText(textoTempoTotal);
                        tv_tempo_total.setVisibility(View.VISIBLE);

                        String textoIntervaloTempo = "" + Funcoes.milisegundos_em_HH_MM_SS(intervaloTempo * 1000);
                        tv_tempo_parcial.setText(textoIntervaloTempo);
                        tv_tempo_parcial.setVisibility(View.VISIBLE);
                        Log.d("CRONOGRAFO", textoTempoTotal + " ** " + textoIntervaloTempo);
                    }
                });
            }
        };

        cronometroOS = new Timer();
        cronometroOS.schedule(actualizarTempos, 1000, 1000);
    }

    public OSBO getOsbo() {
        return osbo;
    }

    public void pararCronometro() {
        if (cronometroOS != null) {
            Log.i(TAG, "A parar o cronómetro");
            cronometroOS.cancel();
            cronometroOS.purge();
            cronometroOS = null;
        }
    }

    public Timer getCronometroOS() {
        return cronometroOS;
    }
}
