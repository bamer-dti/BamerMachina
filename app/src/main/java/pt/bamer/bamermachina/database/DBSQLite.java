package pt.bamer.bamermachina.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import pt.bamer.bamermachina.pojos.OSBI;
import pt.bamer.bamermachina.pojos.OSBO;
import pt.bamer.bamermachina.pojos.OSPROD;

public class DBSQLite extends SQLiteOpenHelper {
    private static final String TAG = DBSQLite.class.getSimpleName();
    private static final String DATABASE_NAME = "opsec";
    private static final int DATABASE_VERSION = 2;

    private static final String TABELA_OSBO = "osbo";
    private static final String TABELA_OSBI = "osbi";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_OSBI);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_OSBO);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_OSPROD);
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
        if(cursor.moveToFirst()){
            do {
                OSPROD osprod = new OSPROD();
                osprod.bostamp = cursor.getString(cursor.getColumnIndex(BOSTAMP));
                osprod.qtt = cursor.getInt(cursor.getColumnIndex(QTT));
                lista.add(osprod);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
}
