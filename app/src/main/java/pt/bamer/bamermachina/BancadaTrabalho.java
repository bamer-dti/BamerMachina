package pt.bamer.bamermachina;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import pt.bamer.bamermachina.adapters.OSRecyclerAdapter;
import pt.bamer.bamermachina.database.DBSQLite;
import pt.bamer.bamermachina.fragmentos.FragPai;
import pt.bamer.bamermachina.pojos.OSBO;
import pt.bamer.bamermachina.utils.Funcoes;


public class BancadaTrabalho {
    private static final String TAG = BancadaTrabalho.class.getSimpleName();
    private Context contexto;
    private OSBO osbo;
    private FragPai fragmento;
    private LinearLayout ll_working_os;
    private TextView tv_os;
    private TextView tv_qtt_total;
    private Button bt_qtt_feita;
    private TextView tv_tempo_total;
    private TextView tv_tempo_parcial;
    private Timer cronometroOS;

    public BancadaTrabalho(FragPai fragPai) {
        this.fragmento = fragPai;
        this.contexto = fragPai.getContext();
        this.ll_working_os = fragPai.getLl_working_os();
        this.tv_os = fragPai.getTv_os();
        this.tv_qtt_total = fragPai.getTv_qtt_total();
        this.bt_qtt_feita = fragPai.getBt_qtt_feita();
        this.tv_tempo_total = fragPai.getTv_tempo_total();
        this.tv_tempo_parcial = fragPai.getTv_tempo_parcial();
    }

    @SuppressLint("SetTextI18n")
    public void actualizarDados() {
        OSRecyclerAdapter adapter = (OSRecyclerAdapter) fragmento.getRecyclerView().getAdapter();
        //Verificar se a bancada continua em trabalho
        if (osbo != null) {
            int posicao = new DBSQLite(contexto).getOSTimerPosicao(osbo.bostamp);
            if (posicao == -1) {
                Funcoes.alerta(contexto, "Erro...", "A bancada deveria estar a trabalhar mas retornou código -1!");
            }
            if (posicao == 1) {
                adapter.removerOSBO(osbo.bostamp);
                bt_qtt_feita.setText("" + new DBSQLite(contexto).getQtdProdBostamp(osbo.bostamp));
                return;
            }
            if (posicao == 2) {//STOPPED!!!
                osbo = null;
                ll_working_os.setVisibility(View.GONE);
                osbo = null;
            }
            return;
        }

        OSBO osboDB = new DBSQLite(contexto).getOSBOemTrabalho();
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
            tv_qtt_total.setText("" + new DBSQLite(contexto).getQtdBostamp(osbo.bostamp));
            bt_qtt_feita.setText("" + new DBSQLite(contexto).getQtdProdBostamp(osbo.bostamp));
            tv_tempo_total.setText("HH:mm:ss");
            tv_tempo_parcial.setText("" + new DBSQLite(contexto).getOSTimerPosicao(osbo.bostamp));
            adapter.removerOSBO(osbo.bostamp);

            iniciarCronometros(contexto, osbo.bostamp);
        }
    }

    private void iniciarCronometros(final Context context, final String bostamp) {
        TimerTask actualizarTempos = new TimerTask() {
            @Override
            public void run() {
                final long tempoTotal = new DBSQLite(context).getTotalTempoBostamp(bostamp);
                long ultimoTempo = new DBSQLite(context).getUltimoTempo(bostamp);
                long unixNow = System.currentTimeMillis() / 1000L;
                final long intervaloTempo = unixNow - ultimoTempo;
                ((Activity) context).runOnUiThread(new Runnable() {
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
