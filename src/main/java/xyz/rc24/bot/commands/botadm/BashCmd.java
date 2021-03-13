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
import xyz.rc24.bot.commands.Categories;
import xyz.rc24.bot.commands.RegistrableCommand;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Runs a shell command.
 *
 * @author Artuto
 */
@RegistrableCommand
public class BashCmd extends Command
{
    public BashCmd()
    {
        this.name = "bash";
        this.help = "Runs a bash command";
        this.category = Categories.BOT_ADMIN;
        this.guildOnly = false;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        if(event.getArgs().isEmpty())
        {
            event.replyError("Cannot execute a empty command!");
            return;
        }

        event.async(() ->
        {
            Scanner scanner;
            String output = "";

            try
            {
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(getCommandInterpreter() + event.getArgs() + " && 2>&1");

                process.waitFor(10, TimeUnit.SECONDS); // timeout of 10 seconds
                scanner = new Scanner(process.getInputStream());
            }
            catch(IOException ignored) {event.replyError("Unknown command."); return;}
            catch(InterruptedException ignored) {event.replyError("Process has timed out"); return;}

            while(scanner.hasNextLine())
                output += scanner.nextLine() + "\n";

            if(!(output.isEmpty())) event.replySuccess("Done! Output: ```\n" + output + "```");
            else event.reactSuccess();
        });
    }

    private String getCommandInterpreter()
    {
        String os = System.getProperty("os.name");

        if(os.startsWith("Windows"))
            return "cmd /c ";
        else if(os.equals("Linux"))
            return "sh -c ";
        else
            return "";
    }
}