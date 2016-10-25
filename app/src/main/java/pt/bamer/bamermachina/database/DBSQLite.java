package pt.bamer.bamermachina.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import pt.bamer.bamermachina.MrApp;
import pt.bamer.bamermachina.pojos.OSBI;
import pt.bamer.bamermachina.pojos.OSBO;
import pt.bamer.bamermachina.pojos.OSPROD;
import pt.bamer.bamermachina.pojos.OSTIMER;

public class DBSQLite extends SQLiteOpenHelper {
    private static final String TAG = DBSQLite.class.getSimpleName();
    private static final String DATABASE_NAME = "opsec";
    private static final int DATABASE_VERSION = 4;

    private static final String TABELA_OSBO = "osbo";
    private static final String TABELA_OSBI = "osbi";
    private static final String TABELA_OSTIMER = "ostimer";
    private static final String COLID = "_id";
    private static final String BOSTAMP = "bostamp";
    private static final String BISTAMP = "bistamp";
    private static final String QTT = "qtt";
    private static final String DESIGN = "design";
    private static final String DIM = "dim";
    private static final String FAMILIA = "familia";
    private static final String MK = "mk";
    private static final String REF = "ref";
    private static final String TIPO = "tipo";
    private static final String COR = "cor";
    private static final String DTCLIENTE = "dtcliente";
    private static final String DTCORTEF = "dtcortef";
    private static final String DTEMBALA = "dtembala";
    private static final String DTEXPEDI = "dtexpedi";
    private static final String DTTRANSF = "dttransf";
    private static final String ESTADO = "estado";
    private static final String FREF = "fref";
    private static final String NMFREF = "nmfref";
    private static final String OBRANO = "obrano";
    private static final String OBS = "osb";
    private static final String ORDEM = "ordem";
    private static final String LASTTIME = "lasttime";
    private static final String POSICAO = "posicao";
    private static final String UNIXTIME = "unixtime";
    private static final String MAQUINA = "maquina";
    private static final String OPERADOR = "operador";
    private static final String SECCAO = "seccao";
    private static final String DATABASE_CREATE_TABLE_OSBO = "Create Table " + TABELA_OSBO + "("
            + COLID + " integer primary key autoincrement, "
            + COR + " integer not null, "
            + BOSTAMP + " text not null, "
            + DTCLIENTE + " text not null, "
            + DTCORTEF + " text not null, "
            + DTEMBALA + " text not null, "
            + DTEXPEDI + " real not null, "
            + DTTRANSF + " real not null, "
            + ESTADO + " real not null, "
            + FREF + " text not null, "
            + NMFREF + " text not null, "
            + OBRANO + " integer not null, "
            + OBS + " text not null, "
            + ORDEM + " ordem not null, "
            + SECCAO + " text not null "
            + ")";
    private static final String DATABASE_CREATE_TABLE_OSBI = "Create Table " + TABELA_OSBI + "("
            + COLID + " integer primary key autoincrement, "
            + DESIGN + " text not null, "
            + DIM + " text not null, "
            + FAMILIA + " text not null, "
            + MK + " text not null, "
            + QTT + " real not null, "
            + REF + " real not null, "
            + TIPO + " real not null, "
            + BOSTAMP + " text not null, "
            + BISTAMP + " text not null "
            + ")";
    private static final String DATABASE_CREATE_TABLE_OSTIMER = "Create Table " + TABELA_OSTIMER + "("
            + COLID + " integer primary key autoincrement, "
            + BOSTAMP + " text not null, "
            + BISTAMP + " text not null, "
            + ESTADO + " text not null, "
            + SECCAO + " text not null, "
            + MAQUINA + " text not null, "
            + OPERADOR + " text not null, "
            + LASTTIME + " real not null, "
            + POSICAO + " integer not null, "
            + UNIXTIME + " real not null "
            + ")";
    private static String TABELA_OSPROD = "osprod";
    private static final String DATABASE_CREATE_TABLE_OSPROD = "Create Table " + TABELA_OSPROD + "("
            + COLID + " integer primary key autoincrement, "
            + QTT + " real not null, "
            + BOSTAMP + " text not null, "
            + BISTAMP + " text not null "
            + ")";

    public DBSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_TABLE_OSBO);
        Log.i(TAG, "A criar a tabela " + TABELA_OSBO + " na base de dados " + DATABASE_NAME);
        db.execSQL(DATABASE_CREATE_TABLE_OSBI);
        Log.i(TAG, "A criar a tabela " + TABELA_OSBI + " na base de dados " + DATABASE_NAME);
        db.execSQL(DATABASE_CREATE_TABLE_OSPROD);
        Log.i(TAG, "A criar a tabela " + TABELA_OSPROD + " na base de dados " + DATABASE_NAME);
        db.execSQL(DATABASE_CREATE_TABLE_OSTIMER);
        Log.i(TAG, "A criar a tabela " + TABELA_OSTIMER + " na base de dados " + DATABASE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_OSBI);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_OSBO);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_OSPROD);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_OSTIMER);
        onCreate(db);
    }

    public void gravarOSBO(ArrayList<OSBO> listaOSBO) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(TABELA_OSBO, "", null);
        for (OSBO osbo : listaOSBO) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BOSTAMP, osbo.bostamp);
            contentValues.put(COR, osbo.cor);
            contentValues.put(DTCLIENTE, osbo.dtcliente);
            contentValues.put(DTCORTEF, osbo.dtcortef);
            contentValues.put(DTEMBALA, osbo.dtembala);
            contentValues.put(DTEXPEDI, osbo.dtexpedi);
            contentValues.put(DTTRANSF, osbo.dttransf);
            contentValues.put(ESTADO, osbo.estado);
            contentValues.put(FREF, osbo.fref);
            contentValues.put(NMFREF, osbo.nmfref);
            contentValues.put(OBRANO, osbo.obrano);
            contentValues.put(OBS, osbo.obs);
            contentValues.put(ORDEM, osbo.ordem);
            contentValues.put(SECCAO, osbo.seccao);
            db.insert(TABELA_OSBO, null, contentValues);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        Log.i(TAG, TABELA_OSBO + ": foram inseridos " + listaOSBO.size() + " registos");
    }

    public void gravarOSBI(ArrayList<OSBI> listaOSBI) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(TABELA_OSBI, "", null);
        for (OSBI osbi : listaOSBI) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DESIGN, osbi.design);
            contentValues.put(DIM, osbi.dim);
            contentValues.put(FAMILIA, osbi.familia);
            contentValues.put(MK, osbi.mk);
            contentValues.put(QTT, osbi.qtt);
            contentValues.put(REF, osbi.ref);
            contentValues.put(TIPO, osbi.tipo);
            contentValues.put(BOSTAMP, osbi.bostamp);
            contentValues.put(BISTAMP, osbi.bistamp);
            db.insert(TABELA_OSBI, null, contentValues);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        Log.i(TAG, TABELA_OSBI + ": foram inseridos " + listaOSBI.size() + " registos");
    }

    public void gravarOSPROD(ArrayList<OSPROD> listaOSPROD) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(TABELA_OSPROD, "", null);
        for (OSPROD osprod : listaOSPROD) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(QTT, osprod.qtt);
            contentValues.put(BOSTAMP, osprod.bostamp);
            contentValues.put(BISTAMP, osprod.bistamp);
            db.insert(TABELA_OSPROD, null, contentValues);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        Log.i(TAG, TABELA_OSPROD + ": foram inseridos " + listaOSPROD.size() + " registos");
    }

    public void gravarOSTIMER(ArrayList<OSTIMER> listaOSTIMER) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(TABELA_OSTIMER, "", null);
        for (OSTIMER ostimer : listaOSTIMER) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BOSTAMP, ostimer.bostamp);
            contentValues.put(BISTAMP, ostimer.bistamp);
            contentValues.put(LASTTIME, ostimer.lasttime);
            contentValues.put(POSICAO, ostimer.posicao);
            contentValues.put(UNIXTIME, ostimer.unixtime);
            contentValues.put(SECCAO, ostimer.seccao);
            contentValues.put(ESTADO, ostimer.estado);
            contentValues.put(MAQUINA, ostimer.maquina);
            contentValues.put(OPERADOR, ostimer.operador);
            db.insert(TABELA_OSTIMER, null, contentValues);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        Log.i(TAG, TABELA_OSTIMER + ": foram inseridos " + listaOSTIMER.size() + " registos");
    }

    public int getQtdBostamp(String bostamp) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABELA_OSBI, new String[]{"SUM(" + QTT + ") as " + QTT}, BOSTAMP + " = ?", new String[]{bostamp}, "", "", "");
        int qtt = 0;
        if (cursor.moveToFirst()) {
            do {
                qtt = cursor.getInt(cursor.getColumnIndex(QTT));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return qtt;
    }

    public int getQtdProdBostamp(String bostamp) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABELA_OSPROD, new String[]{"SUM(" + QTT + ") as " + QTT}, BOSTAMP + " = ?", new String[]{bostamp}, "", "", "");
        int qtt = 0;
        if (cursor.moveToFirst()) {
            do {
                qtt = cursor.getInt(cursor.getColumnIndex(QTT));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return qtt;
    }

    public ArrayList<OSBO> getOSBOOrdered() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<OSBO> lista = new ArrayList<>();
        Cursor cursor = db.query(TABELA_OSBO, new String[]{BOSTAMP, COR, DTCLIENTE, DTCORTEF, DTEMBALA, DTEXPEDI, DTTRANSF, ESTADO, FREF, NMFREF, OBRANO, OBS, ORDEM, SECCAO}
                , "", null, "", "", DTCORTEF + ", " + ORDEM
        );
        if (cursor.moveToFirst()) {
            do {
                OSBO osbo = new OSBO();
                osbo.bostamp = cursor.getString(cursor.getColumnIndex(BOSTAMP));
                osbo.cor = cursor.getInt(cursor.getColumnIndex(COR));
                osbo.dtcliente = cursor.getString(cursor.getColumnIndex(DTCLIENTE));
                osbo.dtcortef = cursor.getString(cursor.getColumnIndex(DTCORTEF));
                osbo.dtembala = cursor.getString(cursor.getColumnIndex(DTEMBALA));
                osbo.dtexpedi = cursor.getString(cursor.getColumnIndex(DTEXPEDI));
                osbo.dttransf = cursor.getString(cursor.getColumnIndex(DTTRANSF));
                osbo.estado = cursor.getString(cursor.getColumnIndex(ESTADO));
                osbo.fref = cursor.getString(cursor.getColumnIndex(FREF));
                osbo.nmfref = cursor.getString(cursor.getColumnIndex(NMFREF));
                osbo.obrano = cursor.getInt(cursor.getColumnIndex(OBRANO));
                osbo.obs = cursor.getString(cursor.getColumnIndex(OBS));
                osbo.ordem = cursor.getInt(cursor.getColumnIndex(ORDEM));
                osbo.seccao = cursor.getString(cursor.getColumnIndex(SECCAO));
                lista.add(osbo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public ArrayList<OSPROD> getProdAgrupadaPorBostamp() {
        ArrayList<OSPROD> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABELA_OSPROD,
                new String[]{BOSTAMP, "SUM(" + QTT + ") as " + QTT},
                "",
                null,
                BOSTAMP
                , ""
                , ""
        );
        if (cursor.moveToFirst()) {
            do {
                OSPROD osprod = new OSPROD();
                osprod.bostamp = cursor.getString(cursor.getColumnIndex(BOSTAMP));
                osprod.qtt = cursor.getInt(cursor.getColumnIndex(QTT));
                lista.add(osprod);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public int getOSTimerPosicao(String bostamp) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABELA_OSTIMER
                , new String[]{POSICAO}
                , BOSTAMP + " = ?"
                , new String[]{bostamp}
                , ""
                , ""
                , UNIXTIME + " desc"
                , "1"
        );
        int posicao = -1;
        if (cursor.moveToFirst()) {
            posicao = cursor.getInt(cursor.getColumnIndex(POSICAO));
        }
        Log.i(TAG, "getOSTimerPosicao(" + bostamp + ") = " + posicao + ", cursor com " + cursor.getCount() + " registos");
        cursor.close();
        db.close();
        return posicao;
    }

    public OSBO getOSBOemTrabalho() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursorOSBO = db.query(TABELA_OSBO,
                new String[]{BOSTAMP, COR, DTCLIENTE, DTCORTEF, DTEMBALA, DTEXPEDI, DTTRANSF, ESTADO, FREF, NMFREF, OBRANO, OBS, ORDEM, SECCAO},
                SECCAO + " = ? AND " + ESTADO + " = ?",
                new String[]{MrApp.getSeccao(), MrApp.getEstado()},
                "",
                "",
                ""
        );
        if (cursorOSBO.moveToFirst()) {
            do {
                Cursor cursorTimer = db.query(TABELA_OSTIMER,
                        new String[]{POSICAO},
                        BOSTAMP + " = ?",
                        new String[]{cursorOSBO.getString(cursorOSBO.getColumnIndex(BOSTAMP))},
                        "",
                        "",
                        UNIXTIME + " DESC",
                        "1"
                );
                if (cursorTimer.moveToFirst()) {
                    if (cursorTimer.getInt(cursorTimer.getColumnIndex(POSICAO)) == 1) {
                        OSBO osbo = new OSBO(
                                cursorOSBO.getInt(cursorOSBO.getColumnIndex(COR)),
                                cursorOSBO.getString(cursorOSBO.getColumnIndex(DTCORTEF)),
                                cursorOSBO.getString(cursorOSBO.getColumnIndex(DTCLIENTE)),
                                cursorOSBO.getString(cursorOSBO.getColumnIndex(DTEMBALA)),
                                cursorOSBO.getString(cursorOSBO.getColumnIndex(DTEXPEDI)),
                                cursorOSBO.getString(cursorOSBO.getColumnIndex(DTTRANSF)),
                                cursorOSBO.getString(cursorOSBO.getColumnIndex(ESTADO)),
                                cursorOSBO.getString(cursorOSBO.getColumnIndex(FREF)),
                                cursorOSBO.getString(cursorOSBO.getColumnIndex(NMFREF)),
                                cursorOSBO.getInt(cursorOSBO.getColumnIndex(OBRANO)),
                                cursorOSBO.getString(cursorOSBO.getColumnIndex(OBS)),
                                cursorOSBO.getInt(cursorOSBO.getColumnIndex(ORDEM)),
                                cursorOSBO.getString(cursorOSBO.getColumnIndex(SECCAO)),
                                cursorOSBO.getString(cursorOSBO.getColumnIndex(BOSTAMP))
                        );
                        cursorTimer.close();
                        db.close();
                        return osbo;
                    }
                }
            } while (cursorOSBO.moveToNext());
        }
        cursorOSBO.close();
        db.close();
        return null;
    }

    public long getUltimoTempo(String bostamp) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABELA_OSTIMER
                , new String[]{UNIXTIME}
                , BOSTAMP + " = ? AND " + POSICAO + " = ? AND " + SECCAO + " = ? AND " + MAQUINA + " = ?"
                , new String[]{bostamp, "1", MrApp.getSeccao(), MrApp.getMaquina()}
                , ""
                , ""
                , UNIXTIME + " desc"
                , "1"
        );
        long tim = 0;
        if (cursor.moveToFirst()) {
            tim = cursor.getLong(cursor.getColumnIndex(UNIXTIME));
        }
        cursor.close();
        db.close();
        return tim;
    }

    public long getTotalTempoBostamp(String bostamp) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABELA_OSTIMER
                , new String[]{LASTTIME, UNIXTIME}
                , BOSTAMP + " = ? AND " + POSICAO + " = ? AND " + SECCAO + " = ? AND " + MAQUINA + " = ?"
                , new String[]{bostamp, "2", MrApp.getSeccao(), MrApp.getMaquina()}
                , ""
                , ""
                , UNIXTIME
        );
        long tempoCalculado = 0;
        if (cursor.moveToFirst()) {
            do {
                long inicio = cursor.getLong(cursor.getColumnIndex(LASTTIME));
                long fim = cursor.getLong(cursor.getColumnIndex(UNIXTIME));
                tempoCalculado += (fim - inicio);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tempoCalculado;
    }
}
