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

package xyz.rc24.bot.commands.tools;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import xyz.rc24.bot.Bot;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.core.entities.CodeType;
import xyz.rc24.bot.database.GuildSettingsDataManager;
import xyz.rc24.bot.utils.FormatUtil;

/**
 * @author Artuto
 */

public class DefaultAddCmd extends Command
{
    private GuildSettingsDataManager dataManager;

    public DefaultAddCmd(Bot bot)
    {
        this.dataManager = bot.getGuildSettingsDataManager();
        this.name = "defaultadd";
        this.help = "Changes the default `add` command's type.";
        this.category = Categories.TOOLS;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
    }

    @Override
    protected void execute(CommandEvent event)
    {
        CodeType type = CodeType.fromCode(event.getArgs());
        if(type == CodeType.UNKNOWN)
        {
            event.replyError(FormatUtil.getCodeTypes());
            return;
        }

        if(dataManager.setDefaultAddType(type, event.getGuild().getIdLong()))
            event.replySuccess("Successfully set `" + type.getName() + "` as default `add` type!");
        else
            event.replyError("Error whilst updating the default add type! Please contact a developer.");
    }
}
