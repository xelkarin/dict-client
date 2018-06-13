/*
 * Copyright (C) 2018 Robert Gill <locke@sdf.lonestar.org>
 * All rights reserved.
 *
 * This file is a part of DICT Client.
 *
 */

package org.lonestar.sdf.locke.apps.dictclient;

import android.database.CursorWrapper;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.misc.BaseDaoEnabled;

import java.sql.SQLException;

class BaseModel extends BaseDaoEnabled {
    @SuppressWarnings("unused")
    public static CursorWrapper cursorWrapper(CloseableIterator iterator) {
        throw new UnsupportedOperationException();
    }

    public BaseModel() {
        try {
            setDao(DatabaseManager.getInstance().getDao(this.getClass()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
