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

package xyz.rc24.bot.commands.botadm;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.commands.RegistrableCommand;

/**
 * @author Artuto
 */
@RegistrableCommand
public class EvalCmd extends Command
{
    //private Bot bot;

    private final CompilerConfiguration compilerConfiguration;

    public EvalCmd()
    {
        this.name = "eval";
        this.help = "Executes Groovy code";
        this.category = Categories.BOT_ADMIN;
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.ownerCommand = true;
        this.guildOnly = false;

        String[] imports = new String[]{"com.jagrosh.jdautilities.command", "com.jagrosh.jdautilities.command.impl",
                "com.jagrosh.jdautilities.commons", "com.jagrosh.jdautilities.commons.utils",
                "java.awt", "java.io", "java.lang", "java.util", "java.util.stream",
                "xyz.rc24.bot", "xyz.rc24.bot.db", "xyz.rc24.bot.db.dao", "xyz.rc24.bot.utils",
                "net.dv8tion.jda.api", "net.dv8tion.jda.api.audit", "net.dv8tion.jda.api.entities",
                "net.dv8tion.jda.api.managers", "net.dv8tion.jda.api.utils", "net.dv8tion.jda.internal",
                "net.dv8tion.jda.internal.entities", "net.dv8tion.jda.internal.utils"};

        this.compilerConfiguration = new CompilerConfiguration()
                .addCompilationCustomizers(new ImportCustomizer().addStarImports(imports));
    }

    @Override
    protected void execute(CommandEvent event)
    {
        String eval = event.getArgs().replace("getToken", "getSelfUser");

        event.async(() ->
        {
            String output;

            try
            {
                output = String.valueOf(new GroovyShell(setupVariables(event), compilerConfiguration).evaluate(eval));
            }
            catch(Exception e)
            {
                event.replyError("Error! ```\n" + e.getMessage() + "```");
                return;
            }

            event.replySuccess("Success! ```\n" + output.replace(event.getJDA().getToken(), "YouTried") + "```");
        });
    }

    private Binding setupVariables(CommandEvent event)
    {
        return new Binding()
        {{
            //setVariable("bot", bot); // TODO
            setVariable("event", event);
            setVariable("client", event.getClient());
            setVariable("shardManager", event.getJDA().getShardManager());
            setVariable("jda", event.getJDA());
            setVariable("selfUser", event.getSelfUser());
            setVariable("author", event.getAuthor());
            setVariable("channel", event.getChannel());
            setVariable("message", event.getMessage());

            if(event.isFromType(ChannelType.TEXT))
            {
                setVariable("guild", event.getGuild());
                setVariable("tc", event.getTextChannel());
                setVariable("selfMember", event.getSelfMember());
                setVariable("member", event.getMember());
            }
        }};
    }
}