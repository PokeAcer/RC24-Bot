/*
 * MIT License
 *
 * Copyright (c) 2017-2021 RiiConnect24 and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.rc24.bot.database;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import xyz.rc24.bot.RiiConnect24Bot;

import java.util.List;
import java.util.Optional;

/**
 * Database setters and getters with exception catching and handling
 *
 * @author Artuto
 */

public class Database
{
    private final Logger logger = RiiConnect24Bot.getLogger(getClass());

    boolean doInsert(@Language("MySQL") String query, Object... params)
    {
        try
        {
            DB.executeInsert(query, params);
            return true;
        }
        catch(Exception e)
        {
            logger.error("Exception while inserting into database: " + e.getMessage(), e);
            return false;
        }
    }

    boolean doDelete(@Language("MySQL") String query, Object... params)
    {
        try
        {
            DB.executeUpdate(query, params);
            return true;
        }
        catch(Exception e)
        {
            logger.error("Exception while deleting values from the database: " + e.getMessage(), e);
            return false;
        }
    }

    Optional<DbRow> getRow(@Language("MySQL") String query, Object... params)
    {
        DbRow row;

        try {row = DB.getFirstRow(query, params);}
        catch(Exception e)
        {
            logger.error("Exception while getting info from the database: " + e.getMessage(), e);
            row = null;
        }

        return Optional.ofNullable(row);
    }

    Optional<List<DbRow>> getRows(@Language("MySQL") String query, Object... params)
    {
        List<DbRow> rows;

        try {rows = DB.getResults(query, params);}
        catch(Exception e)
        {
            logger.error("Exception while getting info from the database: " + e.getMessage(), e);
            rows = null;
        }

        return Optional.ofNullable(rows);
    }
}
