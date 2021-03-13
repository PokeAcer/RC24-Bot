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

package xyz.rc24.bot.core.entities.impl;

import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import xyz.rc24.bot.RiiConnect24Bot;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.core.entities.GuildSettings;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

public class GuildSettingsImpl implements GuildSettings, GuildSettingsProvider
{
    private CodeType defaultAddType;
    private long guildId;
    private String prefix;

    public GuildSettingsImpl(CodeType defAddType, long gId, String prefix)
    {
        this.defaultAddType = defAddType;
        this.guildId = gId;
        this.prefix = prefix;
    }

    @Override
    public CodeType getDefaultAddType()
    {
        return defaultAddType;
    }

    @Override
    public long getGuildId()
    {
        return guildId;
    }

    @Override
    public String getPrefix()
    {
        return prefix == null ? RiiConnect24Bot.getInstance().getConfig().getPrefix() : prefix;
    }

    @Nullable
    @Override
    public Collection<String> getPrefixes()
    {
        return Collections.singleton(getPrefix());
    }

    public void setDefaultAddType(CodeType type)
    {
        this.defaultAddType = type;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }
}
