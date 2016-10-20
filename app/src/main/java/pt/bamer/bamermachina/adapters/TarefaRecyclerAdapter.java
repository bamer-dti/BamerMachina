package pt.bamer.bamermachina.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;

import pt.bamer.bamermachina.Dossier;
import pt.bamer.bamermachina.MrApp;
import pt.bamer.bamermachina.R;
import pt.bamer.bamermachina.pojos.JSONObjectQtd;
import pt.bamer.bamermachina.pojos.OSBI;
import pt.bamer.bamermachina.utils.Constantes;
import pt.bamer.bamermachina.webservices.WebServices;

///**
// * Created by miguel.silva on 21-04-2016.
// */
public class TarefaRecyclerAdapter extends RecyclerView.Adapter {
    @SuppressWarnings("unused")
    private static final String TAG = TarefaRecyclerAdapter.class.getSimpleName();
    private final Dossier activityDossier;
    private final List<OSBI> lista;
    private int modoOperacional;

    public TarefaRecyclerAdapter(Dossier activityDossier, List<OSBI> lista, int modoOperacional) {
        this.activityDossier = activityDossier;
        this.lista = lista;
        this.modoOperacional = modoOperacional;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(this.activityDossier).inflate(R.layout.view_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final OSBI osbi = getItem(position);
        if (osbi == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        final String bostamp = osbi.bostamp;
        final String dim = osbi.dim;
        final String mk = osbi.mk;
        final String ref = osbi.ref;
        final String design = osbi.design;
        boolean hideButs = Constantes.MODO_STARTED == modoOperacional;

        viewHolder.tv_ref.setText(ref + " - " + design);

        viewHolder.tv_qtt.setText("...");

        TaskQtd taskQtd = new TaskQtd(viewHolder);
        taskQtd.execute();

        viewHolder.tv_dim.setText(dim + (mk.equals("") ? "" : ", mk " + mk));

        if (modoOperacional == Constantes.MODO_STARTED) {
            viewHolder.bt_total.setVisibility(hideButs ? View.VISIBLE : View.GONE);
            viewHolder.bt_total.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qtd = 0;
                    try {
                        WebServices.registarQtdEmSQL(activityDossier, viewHolder, qtd, qtd, new JSONObjectQtd(bostamp, dim, mk, ref, design, qtd));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            viewHolder.bt_total.setVisibility(View.GONE);
        }

        if (modoOperacional == Constantes.MODO_STARTED) {
            viewHolder.bt_parcial.setVisibility(hideButs ? View.VISIBLE : View.GONE);
            viewHolder.bt_parcial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater li = LayoutInflater.from(activityDossier);
                    @SuppressLint("InflateParams")
                    View promptsView = li.inflate(R.layout.popup_qtt, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            activityDossier);
                    alertDialogBuilder.setView(promptsView);
                    final EditText userInput = (EditText) promptsView.findViewById(R.id.et_qtt);

                    final int qttTotal = osbi.qtt;
                    int qttParcial = 0;
                    int qttRestante = qttTotal - qttParcial;
                    userInput.setHint("" + qttRestante);
                    userInput.setText("" + qttRestante);
                    userInput.setSelection(userInput.getText().length());
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            int qttEfectuada = Integer.parseInt(userInput.getText().toString());
                                            try {
                                                WebServices.registarQtdEmSQL(activityDossier, viewHolder, qttTotal, qttEfectuada, new JSONObjectQtd(bostamp, dim, mk, ref, design, qttEfectuada));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        } else {
            viewHolder.bt_parcial.setVisibility(View.GONE);
        }

    }

    private OSBI getItem(int position) {
        return lista != null ? lista.get(position) : null;
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    public void actualizarQtdProd(String bostamp, String dim, String mk, String ref, String design) {
        for (int i = 0; i < lista.size(); i++) {
            OSBI osbi = lista.get(i);
            String bostamp_ = osbi.bostamp;
            String dim_ = osbi.dim;
            String mk_ = osbi.mk;
            String ref_ = osbi.ref;
            String design_ = osbi.design;
            if (bostamp_.equals(bostamp)) {
                if (dim_.equals(dim)) {
                    if (mk_.equals(mk)) {
                        if (ref_.equals(ref)) {
                            if (design_.equals(design)) {
                                Log.i(TAG, "Actualizar quantidade produzida na posição " + i);
                                notifyItemChanged(i);
                            }
                        }
                    }
                }
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_ref;
        private final TextView tv_qtt;
        private final TextView tv_dim;
        private final Button bt_total;
        private final Button bt_parcial;
        private final LinearLayout llinha;
        private final Context contextHolder;

        //        public ViewHolder(View itemView, int ViewType) {
        public ViewHolder(View itemView) {
            super(itemView);
            llinha = (LinearLayout) itemView.findViewById(R.id.llinha);

            tv_ref = (TextView) itemView.findViewById(R.id.tv_ref);
            tv_qtt = (TextView) itemView.findViewById(R.id.tv_qtt);
            tv_dim = (TextView) itemView.findViewById(R.id.tv_dim);

            bt_total = (Button) itemView.findViewById(R.id.bt_total);
            bt_parcial = (Button) itemView.findViewById(R.id.bt_parcial);
            contextHolder = activityDossier;
        }
    }

    public void pintarObjecto(ViewHolder holder, int qttTotal, int qttParcial) {
        SharedPreferences prefs = MrApp.getPrefs();
        final boolean vertudo = prefs.getBoolean(Constantes.PREF_MOSTRAR_TODAS_LINHAS_PROD, true);
        holder.tv_qtt.setText(qttTotal + (qttParcial == 0 ? "" : "-" + qttParcial + "=" + (qttTotal - qttParcial)));
        holder.llinha.setBackgroundColor(ContextCompat.getColor(holder.contextHolder, R.color.md_white_1000));
        if (qttParcial > 0)
            holder.llinha.setBackgroundColor(ContextCompat.getColor(holder.contextHolder, R.color.md_amber_200));
        holder.llinha.setVisibility(View.VISIBLE);
        if (qttTotal - qttParcial == 0) {
            if (vertudo) {
                holder.llinha.setBackgroundColor(ContextCompat.getColor(holder.contextHolder, R.color.md_amber_900));
            } else {
                removerItem(holder.getAdapterPosition());
            }
        }
        if (modoOperacional == Constantes.MODO_STARTED) {
            holder.bt_total.setVisibility(qttParcial != 0 ? View.INVISIBLE : View.VISIBLE);
            holder.bt_parcial.setVisibility(qttTotal - qttParcial == 0 ? View.INVISIBLE : View.VISIBLE);
        } else {
            holder.bt_total.setVisibility(View.GONE);
            holder.bt_parcial.setVisibility(View.GONE);
        }
    }

    private class TaskQtd extends AsyncTask<Void, Void, Void> {
        private final OSBI osbi;
        private final ViewHolder holder;
        private int qtt;
        private int qttFeita;

        public TaskQtd(ViewHolder viewHolder) {
            this.osbi = getItem(viewHolder.getAdapterPosition());
            this.holder = viewHolder;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            @SuppressWarnings("unchecked")
            String bostamp = osbi.bostamp;
            String dim = osbi.dim;
            String mk = osbi.mk;
            String ref = osbi.ref;
            String design = osbi.design;
            qtt = osbi.qtt;
            qttFeita = 0;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pintarObjecto(holder, qtt, qttFeita);
        }
    }

    public void removerItem(int position) {
        if (position >= 0 && position <= lista.size()) {
            lista.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, lista.size());
        }
    }
}
