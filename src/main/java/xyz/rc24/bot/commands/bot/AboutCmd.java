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

package xyz.rc24.bot.commands.bot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import xyz.rc24.bot.Const;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.commands.RegistrableCommand;
import xyz.rc24.bot.utils.FormatUtil;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static java.lang.String.format;
import static net.dv8tion.jda.api.entities.ChannelType.TEXT;
import static xyz.rc24.bot.Const.COLOR;

@RegistrableCommand
public class AboutCmd extends Command
{
    public AboutCmd()
    {
        this.name = "about";
        this.category = Categories.BOT;
        this.help = "Information about the bot";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.reply(new EmbedBuilder()
                .setColor(event.isFromType(TEXT) ? event.getSelfMember().getColor() : COLOR)
                .setThumbnail(event.getSelfUser().getEffectiveAvatarUrl())
                .setDescription(format(DESCRIPTION, event.getSelfUser(), event.getClient().getServerInvite()))
                .addField("Stats:", stats(event), false)
                .build());
    }

    private String stats(CommandEvent event)
    {
        JDA jda = event.getJDA();
        long seconds = event.getClient().getStartTime().until(OffsetDateTime.now(), ChronoUnit.SECONDS);

        return new StringBuilder()
                .append("Uptime: ").append(FormatUtil.secondsToTime(seconds)).append("\n")
                .append("Servers: **").append(jda.getGuildCache().size()).append("**\n")
                .append("Channels: **").append(jda.getTextChannelCache().size() + jda.getVoiceChannelCache().size()).append("**\n")
                .append("Users: **").append(jda.getGuildCache().stream().mapToInt(Guild::getMemberCount).sum()).append("**")
                .toString();
    }

    private static final String DESCRIPTION = "Hi! I'm **%#s**, you can use me to store your friend codes, " +
            "check error code against the Wiimmfi API, check your RiiTag™ and more!\n" +
            "I'm on version **" + Const.VERSION + "**\n" +
            "You can invite me to your server by using the `invite` command.\n" +
            "I was written in the Java language using [JDA (Java Discord API)]" +
            "(https://github.com/DV8FromTheWorld/JDA) <:jda:816003149029703691> and [JDA Utils]" +
            "(https://github.com/JDA-Applications/JDA-Utilities)\n" +
            "If you need help join the [support server](%s).";
}
