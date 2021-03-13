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

import co.aikar.idb.DbRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Data manager for Birthdays
 *
 * @author Artuto
 */

public class BirthdayDataManager
{
    private final Database db;

    public BirthdayDataManager(Database db)
    {
        this.db = db;
    }

    public boolean setBirthday(long userId, String date)
    {
        return db.doInsert("INSERT INTO birthdays VALUES(?, ?) " +
                "ON DUPLICATE KEY UPDATE day = ?", userId, date, date);
    }

    public String getBirthday(long userId)
    {
        Optional<DbRow> optRow = db.getRow("SELECT * FROM birthdays WHERE user_id = ?", userId);

        return optRow.map(dbRow -> dbRow.getString("day")).orElse(null);
    }

    public List<Long> getPeopleWithDate(String date)
    {
        Optional<List<DbRow>> optRows = db.getRows("SELECT * FROM birthdays WHERE day = ?", date);
        if(!(optRows.isPresent()))
            return Collections.emptyList();

        List<Long> ids = new ArrayList<>();
        for(DbRow row : optRows.get())
            ids.add(row.getLong("user_id"));

        return ids;
    }
}
