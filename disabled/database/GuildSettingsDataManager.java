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
import com.jagrosh.jdautilities.command.GuildSettingsManager;
import net.dv8tion.jda.api.entities.Guild;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.EntityBuilder;
import xyz.rc24.bot.core.entities.GuildSettings;
import xyz.rc24.bot.core.entities.impl.BotCoreImpl;
import xyz.rc24.bot.core.entities.impl.GuildSettingsImpl;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * Data manager for Guild settings
 *
 * @author Artuto
 */

public class GuildSettingsDataManager implements GuildSettingsManager<GuildSettings>
{
    private Bot bot;

    private final EntityBuilder entityBuilder;
    private final Database db;

    public GuildSettingsDataManager(Bot bot)
    {
        this.db = bot.getDatabase();
        this.entityBuilder = ((BotCoreImpl) bot.getCore()).getEntityBuilder();
    }

    @Override
    public void init()
    {
        this.bot = RiiConnect24Bot.getInstance();
    }

    @Nullable
    @Override
    public GuildSettings getSettings(Guild guild)
    {
        return getSettings(guild.getIdLong());
    }

    public GuildSettings getGuildSettings(long id)
    {
        Optional<DbRow> optRow = db.getRow("SELECT * FROM settings WHERE guild_id = ?", id);

        return optRow.map(entityBuilder::buildGuildSettings).orElse(entityBuilder.buildDefaultGuildSettings(id));
    }

    public boolean setDefaultAddType(CodeType type, long id)
    {
        ((GuildSettingsImpl) Objects.requireNonNull(getSettings(id))).setDefaultAddType(type);

        return db.doInsert("INSERT INTO settings (guild_id, default_add)" +
                "VALUES(?, ?) ON DUPLICATE KEY UPDATE default_add = ?", id, type.getId(), type.getId());
    }

    public boolean setPrefix(long id, String prefix)
    {
        ((GuildSettingsImpl) Objects.requireNonNull(getSettings(id))).setPrefix(prefix);

        return db.doInsert("INSERT INTO settings (guild_id, prefix)" +
                "VALUES(?, ?) ON DUPLICATE KEY UPDATE prefix = ?", id, prefix, prefix);
    }

    private GuildSettings getSettings(long id)
    {
        return (bot == null || bot.getCore() == null) ? null : bot.getCore().getGuildSettings(id);
    }
}
