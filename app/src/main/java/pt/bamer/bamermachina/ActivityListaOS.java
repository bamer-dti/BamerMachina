package pt.bamer.bamermachina;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import pt.bamer.bamermachina.firebasefcm.MyFirebaseInstanceIDService;
import pt.bamer.bamermachina.fragmentos.FragDetalheOS;
import pt.bamer.bamermachina.fragmentos.FragPai;
import pt.bamer.bamermachina.fragmentos.FragSMS;
import pt.bamer.bamermachina.pojos.ObjSMS;
import pt.bamer.bamermachina.utils.Constantes;

public class ActivityListaOS extends AppCompatActivity {
    public static final String TAG_FRAG_PAI = "fragpai";
    public static final String TAG_FRAG_DETALHE_OS = "fragdetalheos";
    public static final String TAG_FRAG_SMS = "fragsms";
    private static String TAG = ActivityListaOS.class.getSimpleName();
    private LinearLayout sms_ll;
    private Button sms_bt_machina;
    private Button sms_bt_user;
    private FragPai fragPai;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_listaos);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
            setTitle(MrApp.getTituloBase(this) + " (" + MrApp.getMaquina() + " * " + MrApp.getOperadorNome() + ")");
            myToolbar.setTitleTextColor(Color.WHITE);
        } else {
            Log.e(TAG, "A toolbar em " + TAG + " é nulo??");
        }
        SmoothProgressBar pb_smooth = (SmoothProgressBar) findViewById(R.id.pb_smooth);
        pb_smooth.setVisibility(View.INVISIBLE);

        //noinspection ConstantConditions
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            fragPai = FragPai.newInstance();

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frl_listaos, fragPai, TAG_FRAG_PAI);
            fragmentTransaction.commit();
        }

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
                mostrarFragmentoSMS(Constantes.SMS_MACHINA);
            }
        });
        DatabaseReference refSMS = FirebaseDatabase.getInstance().getReference().child(Constantes.TAG_SMS).child(Constantes.TAG_NAOLIDAS).child(MrApp.getMaquina());
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
                mostrarFragmentoSMS(Constantes.SMS_OPERADOR);
            }
        });
        DatabaseReference refSMSUser = FirebaseDatabase.getInstance().getReference().child(Constantes.TAG_SMS).child(Constantes.TAG_NAOLIDAS).child(MrApp.getOperadorCodigo());
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

    @Override
    protected void onResume() {
        super.onResume();
        String token = FirebaseInstanceId.getInstance().getToken();
        MyFirebaseInstanceIDService.sendRegistrationToServer(token);
        Log.i(TAG, "CHECK_TOKEN: " + token);
//        bancadaTrabalho.actualizarDados();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.w(TAG, "BOTÃO BACK!");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (removerFragSMS()) {
                return true;
            }
            if (removerFragDetalhe()) {
                return true;
            }
            perguntaSeFechaSessao();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void perguntaSeFechaSessao() {
        Log.i(TAG, "Verificar se o utilizador pretende mesmo fechar a sessão");
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Snackbar snak = Snackbar.make(this.findViewById(R.id.frl_listaos), getString(R.string.questao_clicar_novamente_fecho), Snackbar.LENGTH_LONG);
        View view = snak.getView();
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.md_red_400));
        TextView t = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        t.setTextSize(30);
        snak.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void mostrarFragmentoSMS(int tipoSMS) {
        FragmentManager sf = getSupportFragmentManager();
        FragmentTransaction ft = sf.beginTransaction();
        Fragment f = sf.findFragmentByTag(TAG_FRAG_SMS);
        if (f != null) {
            ft.remove(f);
        }

        Bundle bundle = new Bundle();
        bundle.putInt("tipo", tipoSMS);
        //set Fragmentclass Arguments
        FragSMS fragSMS = new FragSMS();
        fragSMS.setArguments(bundle);
        ft.add(R.id.frl_listaos, fragSMS, TAG_FRAG_SMS);
        ft.commit();
    }

    public boolean removerFragSMS() {
        FragmentManager sf = getSupportFragmentManager();
        Fragment f = sf.findFragmentByTag(TAG_FRAG_SMS);
        if (f != null) {
            FragmentTransaction ft = sf.beginTransaction();
            ft.remove(f);
            ft.commit();
            return true;
        }
        return false;
    }

    public void mostrarFragmentoDetalhe(String bostamp, int modoStarted, int obrano) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("bostamp", bostamp);
        bundle.putInt("modo", modoStarted);
        bundle.putInt("obrano", obrano);
        //set Fragmentclass Arguments
        FragDetalheOS fragDetalheOS = new FragDetalheOS();
        fragDetalheOS.setArguments(bundle);
        ft.add(R.id.frl_listaos, fragDetalheOS, TAG_FRAG_DETALHE_OS);
        if (fragPai.isAdded()) {
            ft.hide(fragPai);
        }
        ft.commit();
    }

    //    }
//        return null;
//        }
//            }
//                    return fragment;
//                if (fragment != null && fragment.isVisible())
//            for (android.support.v4.app.Fragment fragment : fragments) {
//        if (fragments != null) {
//        List<android.support.v4.app.Fragment> fragments = fragmentManager.getFragments();
//        FragmentManager fragmentManager = getSupportFragmentManager();
    public boolean removerFragDetalhe() {
        FragmentManager sf = getSupportFragmentManager();
        Fragment f = sf.findFragmentByTag(TAG_FRAG_DETALHE_OS);
        if (f != null) {
            FragmentTransaction ft = sf.beginTransaction();
            ft.remove(f);
            ft.commit();
            return true;
        }
        return false;
    }

    public void iniciarFragmentoPrincipal() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.frl_listaos, fragPai, TAG_FRAG_PAI);
        ft.commit();
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


}
