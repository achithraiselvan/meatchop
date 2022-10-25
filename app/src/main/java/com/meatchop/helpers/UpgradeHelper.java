/*$Id$*/
package com.meatchop.helpers;

import android.content.res.Resources;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class UpgradeHelper {

    private static final String LOG_TAG = "MeatChop";//NO I18N

    protected static final Set<Integer> VERSION;
    static {
        VERSION = new LinkedHashSet<Integer>();
    }

    public static final void addUpgrade(final int version) {
        Log.d(LOG_TAG, "Adding %s to upgrade path "+ version);//NO I18N
        VERSION.add(version);
    }

    public static List<String> availableUpdates(final Resources resources) {
        final List<String> updates = new ArrayList<String>();

        for (final Integer version : VERSION) {
            final String fileName = String.format("updates/migration-%s.sql", version); //NO I18N

            Log.d(LOG_TAG, "Adding db version "+ version + "to update list, loading file " + fileName); //NO I18N

            final String sqlStatements = loadAssetFile(resources, fileName);

            final String[] splitSql = sqlStatements.split("\\r?\\n");
            for (final String sql : splitSql) {
                if (isNotComment(sql)) {
                    updates.add(sql);
                }
            }
        }
        return updates;
    }

    private static String loadAssetFile(final Resources resources, final String fileName) {
        try {
            final InputStream is = resources.getAssets().open(fileName);
            final byte[] buffer = new byte[is.available()];
            is.read(buffer);
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(buffer);
            os.close();
            is.close();
            return os.toString();
        }
        catch (final IOException e) {
            Log.e(LOG_TAG, "IOException: ", e);//NO I18N
            throw new RuntimeException(e);
        }
    }

    private static boolean isNotComment(final String sql) {
        return !sql.startsWith("--") && !sql.startsWith("#"); //NO I18N
    }
}