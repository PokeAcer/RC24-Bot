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

package xyz.rc24.bot.config;

import org.yaml.snakeyaml.Yaml;
import xyz.rc24.bot.Bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static xyz.rc24.bot.RiiConnect24Bot.getLogger;

public class ConfigLoader
{
    public static Config init()
    {
        Yaml yaml = new Yaml();
        File file = new File("config.yml");

        if(!(file.exists()))
        {
            try(InputStream is = Bot.class.getResourceAsStream("/config.yml"))
            {
                Files.copy(is, file.toPath());
                getLogger().info("The config file has been generated, please edit it.");
                System.exit(0);
            }
            catch(IOException e)
            {
                throw new RuntimeException("Failed to copy config file:", e);
            }
        }

        try(InputStream is = new FileInputStream(file))
        {
            return yaml.loadAs(is, Config.class);
        }
        catch(Exception e)
        {
            throw new RuntimeException("Failed to load config file:", e);
        }
    }
}
