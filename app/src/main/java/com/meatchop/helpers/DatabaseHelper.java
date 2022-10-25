package com.meatchop.helpers;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.meatchop.R;
import com.meatchop.sqlitedata.Orderdetailslocal;
import com.meatchop.sqlitedata.Ordertrackingdetailslocal;
import com.meatchop.sqlitedata.Ratingorderdetailslocal;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something
    // appropriate for your app
    private static final String DATABASE_NAME = "themeatchop.db";
    // any time you make changes to your database objects, you may have to
    // increase the database version
    private static final int DATABASE_VERSION = 4;
    private RuntimeExceptionDao<Orderdetailslocal, Integer> orderDetailsLocalDao = null;
    private RuntimeExceptionDao<Ordertrackingdetailslocal, Integer> ordertrackingDetailsLocalDao = null;
    private RuntimeExceptionDao<Ratingorderdetailslocal, Integer> ratingorderDetailsLocalDao = null;

    // the DAO object we use to access the Contact table
 // private RuntimeExceptionDao<RatingTimeDetails, Integer> ratingTimeDetailsDao = null;

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION,
                R.raw.ormlite_config);
        this.context = context;
    }

    /**
     * This is called when the database is first created. Usually you should
     * call createTable statements here to create the tables that will store
     * your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate on Db Helper");  // NO I18N
            Log.d("DatabaseHelper", "before table creation");
         // TableUtils.createTable(connectionSource, Address.class);
            TableUtils.createTable(connectionSource, Orderdetailslocal.class);
            TableUtils.createTable(connectionSource, Ordertrackingdetailslocal.class);
            TableUtils.createTable(connectionSource, Ratingorderdetailslocal.class);

        } catch (Exception e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

 /* public void clearFoodItemTable() {
        try {
            Log.d("DatabaseHelper", "before clearing FoodItemTable");
            TableUtils.clearTable(connectionSource, FoodItem.class);
        } catch (Exception e) {
            Log.e(DatabaseHelper.class.getName(), "Can't clear FoodItem Table", e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    } */

    /**
     * This is called when your application is upgraded and it has a higher
     * version number. This allows you to adjust the various data to match the
     * new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            // Simply loop round until newest version has been reached and add the appropriate migration
            int actualoldversion = oldVersion;
            while (++oldVersion <= newVersion) {
                switch (oldVersion) {
                    case 2: {
                     // UpgradeHelper.addUpgrade(2);
                        TableUtils.createTable(connectionSource, Orderdetailslocal.class);
                        TableUtils.createTable(connectionSource, Ordertrackingdetailslocal.class);
                        TableUtils.createTable(connectionSource, Ratingorderdetailslocal.class);
                     // TableUtils.createTable(connectionSource, OrderDetailsLocal.class);
                        break;
                    }
                    case 3: {
                        UpgradeHelper.addUpgrade(3);
                        break;
                    }

                    case 4: {
                        UpgradeHelper.addUpgrade(4);
                        break;
                    }
                }
            }
            // Get all the available updates
            final List<String> availableUpdates = UpgradeHelper.availableUpdates(context.getResources());
            Log.d("Dosavillage", "Found a total of " + availableUpdates.size() + "update statements"); //NO I18N

            for (final String statement : availableUpdates) {
                db.beginTransaction();
                try {
                    Log.d("Dosavillage", "Executing statement: " + statement); //NO I18N
                    db.execSQL(statement);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }
        } catch (Exception e) {
            Log.e("Dosavillage", "Can't migrate databases, bootstrap database, data will be lost", e); //NO I18N
            // Completely resource the database on failure, you would need to attempt to drop all existing tables in your onCreate method
            onCreate(db, connectionSource);
        }

    }

    private boolean checkTableExist(SQLiteDatabase database, String tableName) {
        Cursor c = null;
        boolean tableExist = false;
        try {
            c = database.query(tableName, null,null,null,null,null,null);
            tableExist = true;
        }catch (Exception e){

        }
        return tableExist;
    }

    public RuntimeExceptionDao<Orderdetailslocal, Integer> getOrderDetailsLocalDao() throws SQLException {
        if (orderDetailsLocalDao == null) {
            orderDetailsLocalDao = getRuntimeExceptionDao(Orderdetailslocal.class);
        }
        return orderDetailsLocalDao;
    }

    public RuntimeExceptionDao<Ordertrackingdetailslocal, Integer> getOrdertrackingDetailsLocalDao() throws SQLException {
        if (ordertrackingDetailsLocalDao == null) {
            ordertrackingDetailsLocalDao = getRuntimeExceptionDao(Ordertrackingdetailslocal.class);
        }
        return ordertrackingDetailsLocalDao;
    }

    public RuntimeExceptionDao<Ratingorderdetailslocal, Integer> getRatingOrderDetailsLocalDao() throws SQLException {
        if (ratingorderDetailsLocalDao == null) {
            ratingorderDetailsLocalDao = getRuntimeExceptionDao(Ratingorderdetailslocal.class);
        }
        return ratingorderDetailsLocalDao;
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It
     * will create it or just give the cached value.
     */

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        orderDetailsLocalDao = null;
        ordertrackingDetailsLocalDao = null;
        ratingorderDetailsLocalDao = null;
        super.close();
    }

    public void clearOrderDetailsTable() throws SQLException {
        TableUtils.clearTable(getConnectionSource(), Orderdetailslocal.class);
    }

    public void clearOrderTrackingDetailsTable() throws SQLException {
        TableUtils.clearTable(getConnectionSource(), Ordertrackingdetailslocal.class);
    }

    public void clearRatingOrderDetailsTable() throws SQLException {
        TableUtils.clearTable(getConnectionSource(), Ratingorderdetailslocal.class);
    }

}
