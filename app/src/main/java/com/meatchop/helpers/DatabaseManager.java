package com.meatchop.helpers;

import android.content.Context;
import android.media.Rating;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.meatchop.sqlitedata.Orderdetailslocal;
import com.meatchop.sqlitedata.Ordertrackingdetailslocal;
import com.meatchop.sqlitedata.Ratingorderdetailslocal;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private final Context mContext;
    private static DatabaseManager INSTANCE;
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Orderdetailslocal, Integer> orderDetailsLocalDao = null;

    public DatabaseManager(Context mContext) {
        this.mContext = mContext;
        databaseHelper = OpenHelperManager.getHelper(mContext, DatabaseHelper.class);

        try {
            orderDetailsLocalDao = databaseHelper.getOrderDetailsLocalDao();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static DatabaseManager getInstance(Context context){
        if(INSTANCE == null) {
            INSTANCE = new DatabaseManager(context);
        }
        return INSTANCE;
    }

    public void releaseDB(){
        if (databaseHelper != null){
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
            INSTANCE = null;
        }
    }

    public int clearAllData(){
        try {
            if (databaseHelper == null) return -1;
            databaseHelper.clearOrderDetailsTable();
            databaseHelper.clearOrderTrackingDetailsTable();

            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public long getOrderDetailsCountFromSqlite(DatabaseHelper helper) {
        try {
            QueryBuilder qb = helper.getOrderDetailsLocalDao().queryBuilder();
            long count = qb.countOf();

            return count;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public Orderdetailslocal getOrderdetailsFromSqlite(DatabaseHelper helper, String orderid) {
        try {
            QueryBuilder qb = helper.getOrderDetailsLocalDao().queryBuilder();
            qb.where().eq("orderid", orderid);

            List<Orderdetailslocal> orderdetailslist = qb.query();
            Orderdetailslocal orderdetailslocal = null;
            for (int i=0; i<orderdetailslist.size(); i++) {
                orderdetailslocal = orderdetailslist.get(i);
            }
            return orderdetailslocal;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<Orderdetailslocal> getAllOrdersFromSqlite(DatabaseHelper helper, int limit) {
        try {
            QueryBuilder qb = helper.getOrderDetailsLocalDao().queryBuilder();
         // qb.where().eq("usermobile", "+" + usermobile);
            if (limit < 0) {
                qb.orderBy("orderplacedtimeinlong", false);
            } else {
                long limitinlong = (long) limit;
                qb.orderBy("orderplacedtimeinlong", false).limit(limitinlong);
            }

            List<Orderdetailslocal> orderdetailslist = qb.query();
            return orderdetailslist;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Ordertrackingdetailslocal getOrderTrackingdetailsFromSqlite(DatabaseHelper helper, String orderid) {
        try {
            QueryBuilder qb = helper.getOrdertrackingDetailsLocalDao().queryBuilder();
            qb.where().eq("orderid", orderid);

            List<Ordertrackingdetailslocal> ordertrackingdetailslist = qb.query();
            Ordertrackingdetailslocal ordertrackingdetailslocal = null;
            for (int i=0; i<ordertrackingdetailslist.size(); i++) {
                ordertrackingdetailslocal = ordertrackingdetailslist.get(i);
            }
            return ordertrackingdetailslocal;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<Ordertrackingdetailslocal> getAllOrderTrackingDetailsFromSqlite(DatabaseHelper helper, long limit) {
        try {
            QueryBuilder qb = helper.getOrdertrackingDetailsLocalDao().queryBuilder();
         // qb.where().eq("usermobile", usermobile);
            if (limit < 0) {
                qb.orderBy("orderplacedtimeinlong", false);
            } else {
                qb.orderBy("orderplacedtimeinlong", false).limit(25l);
            }

            List<Ordertrackingdetailslocal> orderdetailslist = qb.query();
            return orderdetailslist;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<Ratingorderdetailslocal> getAllRatingOrderDetailsFromSqlite(DatabaseHelper helper) {
        try {
            QueryBuilder qb = helper.getRatingOrderDetailsLocalDao().queryBuilder();
            // qb.where().eq("usermobile", usermobile);

            List<Ratingorderdetailslocal> ratingorderdetailslist = qb.query();
            return ratingorderdetailslist;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
