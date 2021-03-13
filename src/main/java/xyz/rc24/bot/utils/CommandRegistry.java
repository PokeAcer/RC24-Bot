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

package xyz.rc24.bot.utils;

import com.jagrosh.jdautilities.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import xyz.rc24.bot.commands.RegistrableCommand;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class CommandRegistry implements ApplicationContextAware
{
    private static ApplicationContext ctx;
    private static Logger logger;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException
    {
        CommandRegistry.ctx = ctx;
        CommandRegistry.logger = LoggerFactory.getLogger(CommandRegistry.class);
    }

    public static Command[] getCommands() throws IOException
    {
        var scanner = new PathMatchingResourcePatternResolver();
        Resource[] resources = scanner.getResources("classpath:xyz/rc24/bot/commands/**");
        List<Class<?>> classes = new LinkedList<>();

        for(Resource resource : resources)
        {
            if(!(resource.getURI().toString().endsWith(".class")))
                continue;

            Class<?> clazz = getClassFromResource(resource);
            if(!(clazz == null) && clazz.isAnnotationPresent(RegistrableCommand.class))
                classes.add(clazz);
        }

        Command[] commands = new Command[classes.size()];
        for(int i = 0; i < classes.size(); i++)
        {
            Class<?> clazz = classes.get(i);
            commands[i] = (Command) ctx.getBean(clazz);
        }

        return commands;
    }

    private static Class<?> getClassFromResource(Resource resource)
    {
        try
        {
            String resourceUri = resource.getURI().toString();
            resourceUri = resourceUri.substring(resourceUri.lastIndexOf("xyz"))
                    .replace(".class", "").replace("/", ".");

            Class<?> clazz = Class.forName(resourceUri);
            if(Command.class.isAssignableFrom(clazz) && !(resourceUri.contains("$")))
            {
                logger.debug("Discovered and loaded command {}", resourceUri);
                return clazz;
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException("Failed to load command " + resource.getFilename() + ":", e);
        }

        return null;
    }
}
