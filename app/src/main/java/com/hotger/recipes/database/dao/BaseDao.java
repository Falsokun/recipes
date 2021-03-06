package com.hotger.recipes.database.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
import static android.icu.text.MessagePattern.ArgType.SELECT;

/**
 * Base DAO not to write basic operations of insert and delete
 *
 * @param <T> - generic parameter for any object
 */
public interface BaseDao<T> {
    @Insert(onConflict = REPLACE)
    void insertAll(List<T> objs);

    @Delete
    void delete(T object);

    @Insert(onConflict = REPLACE)
    void insert(T prev);
}
