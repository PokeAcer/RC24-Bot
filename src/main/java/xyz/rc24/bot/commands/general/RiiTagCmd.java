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

package xyz.rc24.bot.commands.general;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.commands.RegistrableCommand;
import xyz.rc24.bot.utils.SearcherUtil;

import java.awt.Color;
import java.io.IOException;

@RegistrableCommand
public class RiiTagCmd extends Command
{
    @Autowired
    private OkHttpClient httpClient;

    //private final Logger logger;

    public RiiTagCmd()
    {
        this.name = "riitag";
        this.help = "Gets a user's RiiTag";
        this.arguments = "[user]";
        this.aliases = new String[]{"tag"};
        this.category = Categories.GENERAL;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        //this.logger = LoggerFactory.getLogger(RiiTagCmd.class);
    }

    @Override
    protected void execute(CommandEvent event)
    {
        event.async(() ->
        {
            Member member = SearcherUtil.findMember(event, event.getArgs());
            if(member == null)
                return;

            User user = member.getUser();
            Request request = new Request.Builder()
                    .head()
                    .url(String.format(URL, user.getId(), 0D))
                    .build();

            httpClient.newCall(request).enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    /*if(e instanceof SocketTimeoutException)
                    {*/
                        event.replyError("WOW! RiiTag has timed out! What a surprise, indeed " +
                                "||it's not a surprise, at all||.\n Complain to Larsenv, I guess.");
                        /*return;
                    }

                    event.replyError("Hm, something went wrong on our end. Ask a dev to check out my console.\n" +
                            "```" + e.getMessage() + "```");
                    logger.error("Something went wrong whilst checking if user {} has a RiiTag: {}", user.getId(),
                            e.getMessage(), e);*/
                }

                @Override
                public void onResponse(Call call, Response response)
                {
                    if(response.code() == 404)
                    {
                        event.replyError("**" + user.getAsTag() + "** does not have a RiiTag!");
                        return;
                    }

                    if(!(response.isSuccessful()))
                    {
                        onFailure(call, new IOException("Server error: HTTP Code " + response.code()));
                        return;
                    }

                    displayTag(event, user);
                }
            });
        });
    }

    private void displayTag(CommandEvent event, User user)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(user.getAsTag() + "'s RiiTag", null, user.getEffectiveAvatarUrl())
                .setColor(event.isFromType(ChannelType.TEXT) ? event.getSelfMember().getColor() : Color.BLUE)
                .setImage(String.format(URL, user.getId(), Math.random()));

        event.reply(embedBuilder.build());
    }

    private final String URL = "https://tag.rc24.xyz/%s/tag.max.png?randomizer=%f";
}
