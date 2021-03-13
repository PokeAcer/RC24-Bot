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
import com.google.gson.Gson;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.core.BotCore;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.impl.BotCoreImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Artuto
 */

@SuppressWarnings("unchecked")
public class CodeDataManager
{
    private final BotCore core;
    private final Database db;
    private final Gson gson = new Gson();

    public CodeDataManager(Bot bot)
    {
        this.core = bot.getCore();
        this.db = bot.getDatabase();
    }

    public Map<CodeType, Map<String, String>> getAllCodes(long user)
    {
        Optional<DbRow> optRow = db.getRow("SELECT * FROM codes WHERE user_id = ?", user);
        if(!(optRow.isPresent()))
            return new HashMap<>();

        DbRow row = optRow.get();
        Map<CodeType, Map<String, String>> map = new HashMap<>();

        for(CodeType type : CodeType.values())
        {
            if(type == CodeType.UNKNOWN)
                continue;

            Map<String, String> typeMap = gson.fromJson(row.getString(type.getColumn(), ""), Map.class);
            if(typeMap == null)
                typeMap = new HashMap<>();

            map.put(type, typeMap);
        }

        return map;
    }

    @SuppressWarnings("unused")
    public Map<String, String> getCodesForType(CodeType type, long user)
    {
        Optional<DbRow> optRow = db.getRow("SELECT * FROM codes WHERE user_id = ?", user);
        if(!(optRow.isPresent()))
            return Collections.emptyMap();

        DbRow row = optRow.get();

        return gson.fromJson(row.getString(type.getColumn(), ""), Map.class);
    }

    public boolean addCode(CodeType type, long id, String code, String name)
    {
        String json = gson.toJson(updateCodeCache(type, id, code, name));

        return db.doInsert("INSERT INTO codes (user_id, " + type.getColumn() + ") " +
                "VALUES(?, ?) ON DUPLICATE KEY UPDATE " + type.getColumn() + " = ?", id, json, json);
    }

    public boolean editCode(CodeType type, long id, String code, String name)
    {
        return db.doInsert("UPDATE codes SET " + type.getColumn() + " = ? " +
                "WHERE user_id = ?", gson.toJson(updateCodeCache(type, id, code, name)), id);
    }

    public boolean removeCode(CodeType type, long id, String name)
    {
        Map<String, String> map = removeFromCodeCache(type, id, name);
        if(map == null)
            return true;

        return db.doInsert("UPDATE codes SET " + type.getColumn() + " = ? " +
                "WHERE user_id = ?", gson.toJson(map), id);
    }

    public String getFlag(long user)
    {
        Optional<DbRow> optRow = db.getRow("SELECT * FROM codes WHERE user_id = ?", user);

        return optRow.map(dbRow -> dbRow.getString("flag")).orElse("");
    }

    public boolean setFlag(long user, String flag)
    {
        ((BotCoreImpl) core).setFlag(user, flag);

        return db.doInsert("INSERT INTO codes (user_id, flag) " +
                "VALUES(?, ?) ON DUPLICATE KEY UPDATE flag = ?", user, flag, flag);
    }

    private Map<String, String> updateCodeCache(CodeType type, long id, String code, String name)
    {
        Map<String, String> currentCodes = core.getCodesForType(type, id);
        if(currentCodes == null || currentCodes.getClass().getSimpleName().equals("EmptyMap"))
            currentCodes = new HashMap<>();

        currentCodes.put(name, code);
        ((BotCoreImpl) core).updateCodeCache(type, id, currentCodes);

        return currentCodes;
    }

    private Map<String, String> removeFromCodeCache(CodeType type, long id, String name)
    {
        Map<String, String> currentCodes = core.getCodesForType(type, id);
        if(currentCodes == null || currentCodes.getClass().getSimpleName().equals("EmptyMap"))
            return null;

        currentCodes.remove(name);
        ((BotCoreImpl) core).updateCodeCache(type, id, currentCodes);

        return currentCodes;
    }
}
