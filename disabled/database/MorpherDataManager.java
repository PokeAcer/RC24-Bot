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

import java.util.Optional;

/**
 * Data manager for Morpher
 *
 * @author Artuto
 */

public class MorpherDataManager
{
    private final Database db;

    public MorpherDataManager(Database db)
    {
        this.db = db;
    }

    public void setAssociation(long rootMsg, long mirrorMsg)
    {
        db.doInsert("INSERT INTO morpher VALUES(?, ?)", rootMsg, mirrorMsg);
    }

    public long getAssociation(long rootMsg)
    {
        Optional<DbRow> optRow = db.getRow("SELECT * FROM morpher WHERE root_msg_id = ?", rootMsg);

        return optRow.map(dbRow -> dbRow.getLong("mirror_msg_id")).orElse(0L);
    }

    public void removeAssociation(long rootMsg)
    {
        db.doDelete("DELETE FROM morpher WHERE root_msg_id = ?", rootMsg);
    }
}
