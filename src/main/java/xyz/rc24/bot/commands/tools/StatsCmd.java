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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.rc24.bot.Const;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.commands.RegistrableCommand;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

@RegistrableCommand
public class StatsCmd extends Command
{
    @Autowired
    private Gson gson;
    @Autowired
    private OkHttpClient httpClient;

    private final Logger logger;

    public StatsCmd()
    {
        this.name = "stats";
        this.help = "Shows stats for the RC24 services";
        this.category = Categories.TOOLS;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = false;

        this.logger = LoggerFactory.getLogger("Stats Command");
    }

    @Override
    protected void execute(CommandEvent event)
    {
        Request request = new Request.Builder()
                .url("http://164.132.44.106/stats.json")
                .addHeader("User-Agent", "RC24-Bot " + Const.VERSION)
                .build();

        httpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                event.replyError("Could not contact the Stats API! Please ask a owner to check the console. " +
                        "Error: ```\n" + e.getMessage() + "\n```");
                logger.error("Exception while contacting the Stats API! ", e);
            }

            @Override
            public void onResponse(Call call, Response response)
            {
                try(response)
                {
                    if(!(response.isSuccessful()))
                        throw new IOException("Unsuccessful response code: " + response.code());

                    if(response.body() == null)
                        throw new IOException("Response body is null!");

                    EmbedBuilder eb = new EmbedBuilder();
                    MessageBuilder mb = new MessageBuilder();

                    eb.setDescription(parseJSON(response));
                    eb.setColor(Color.decode("#29B7EB"));

                    mb.setContent("<:RC24:302470872201953280> Service stats of RC24:").setEmbed(eb.build());

                    event.reply(mb.build());
                }
                catch(IOException e) {onFailure(call, e);}
                catch(Exception e) {onFailure(call, new IOException(e));}
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private String parseJSON(Response response)
    {
        JsonObject json = gson.fromJson(new InputStreamReader(response.body().byteStream()), JsonObject.class);
        Set<String> keys = new TreeSet<>(json.keySet());

        StringBuilder green = new StringBuilder();
        StringBuilder yellow = new StringBuilder();
        StringBuilder red = new StringBuilder();
        StringBuilder sb = new StringBuilder();

        for(String k : keys)
        {
            String status = json.get(k).getAsString();

            switch(status)
            {
                case "green":
                    green.append("+ ").append(k).append("\n");
                    break;
                case "yellow":
                    yellow.append("* ").append(k).append("\n");
                    break;
                default:
                    red.append("- ").append(k).append("\n");
            }
        }

        sb.append("Supported by RiiConnect24:\n")
                .append("```diff\n").append(green.toString().isEmpty() ? "None!" : green)
                .append("```\nIn progress...\n")
                .append("```fix\n").append(yellow.toString().isEmpty() ? "None!" : yellow)
                .append("```\nNot supported:\n")
                .append("```diff\n").append(red.toString().isEmpty() ? "None!" : red).append("\n```");

        return sb.toString();
    }
}
