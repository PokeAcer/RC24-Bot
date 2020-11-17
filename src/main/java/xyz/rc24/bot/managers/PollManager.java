/*
 * MIT License
 *
 * Copyright (c) 2017-2020 RiiConnect24 and its contributors
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

package xyz.rc24.bot.managers;

import org.yaml.snakeyaml.Yaml;
import xyz.rc24.bot.core.entities.Poll;
import xyz.rc24.bot.core.entities.impl.MiitomoPoll;
import xyz.rc24.bot.core.entities.impl.UKPollImpl;
import xyz.rc24.bot.core.entities.impl.USPollImpl;
import xyz.rc24.bot.core.entities.impl.WorldPollImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PollManager
{
    private final Set<Long> current;
    private final List<Poll> polls;
    private final Random random;
    private final ScheduledExecutorService threadPool;

    public PollManager() throws IOException
    {
        File miitomoFile = new File("miitomo_questions.yml");
        File usFile = new File("polls_049.yml");
        File ukFile = new File("polls_110.yml");
        File worldFile = new File("polls_world.yml");

        if(!(miitomoFile.exists()))
            downloadFile(MIITOMO_FILE, miitomoFile);

        if(!(usFile.exists()))
            downloadFile(US_FILE, usFile);

        if(!(ukFile.exists()))
            downloadFile(UK_FILE, ukFile);

        if(!(worldFile.exists()))
            downloadFile(WORLD_FILE, worldFile);

        Yaml yaml = new Yaml();
        this.polls = new ArrayList<>();
        this.current = new HashSet<>();

        populateList(yaml.load(new FileReader(miitomoFile)), yaml.load(new FileReader(usFile)),
                yaml.load(new FileReader(ukFile)), yaml.load(new FileReader(worldFile)));

        this.random = new Random();

        // Free up memory after this mess
        System.gc();

        this.threadPool = Executors.newScheduledThreadPool(2);
    }

    public Poll getRandomPoll()
    {
        return polls.get(random.nextInt(polls.size()));
    }

    public void trackId(long id)
    {
        current.add(id);
        threadPool.schedule(() -> unTrackId(id), 10, TimeUnit.MINUTES);
    }

    public void unTrackId(long id)
    {
        current.remove(id);
    }

    public boolean isTracked(long id)
    {
        return current.contains(id);
    }

    public ScheduledExecutorService getThreadPool()
    {
        return threadPool;
    }

    private void downloadFile(String downloadUrl, File file) throws IOException
    {
        URL url = new URL(downloadUrl);
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
    }

    @SuppressWarnings("unchecked")
    private void populateList(List<Map<String, Object>> miitomo, List<Map<String, Object>> us,
                              List<Map<String, Object>> uk, List<Map<String, Object>> world)
    {
        for(Map<String, Object> data : miitomo)
        {
            Map<String, Object> section = (Map<String, Object>) data.get("question");
            polls.add(new MiitomoPoll((String) section.get("english")));
        }

        for(Map<String, Object> data : us)
        {
            Map<String, Object> section = (Map<String, Object>) data.get("poll");
            polls.add(new USPollImpl(
                    getPart(section, "question"),
                    getPart(section, "response_1"),
                    getPart(section, "response_2")));
        }

        for(Map<String, Object> data : uk)
        {
            Map<String, Object> section = (Map<String, Object>) data.get("poll");
            polls.add(new UKPollImpl(
                    getPart(section, "question"),
                    getPart(section, "response_1"),
                    getPart(section, "response_2")));
        }

        for(Map<String, Object> data : world)
        {
            Map<String, Object> section = (Map<String, Object>) data.get("poll");
            polls.add(new WorldPollImpl(
                    getPart(section, "question"),
                    getPart(section, "response_1"),
                    getPart(section, "response_2")));
        }
    }

    @SuppressWarnings("unchecked")
    private String getPart(Map<String, Object> poll, String key)
    {
        return (String) ((Map<String, Object>) poll.get(key)).get("english");
    }

    private final String MIITOMO_FILE = "https://cdn.discordapp.com/attachments/287740297923002368/" +
            "748654266352140288/miitomo_questions.yml";
    private final String UK_FILE = "https://raw.githubusercontent.com/RiiConnect24/Site/master/" +
            "_data/votes/results_110.yml";
    private final String US_FILE = "https://raw.githubusercontent.com/RiiConnect24/Site/master/" +
            "_data/votes/results_049.yml";
    private final String WORLD_FILE = "https://raw.githubusercontent.com/RiiConnect24/Site/master/" +
            "_data/votes/results_world.yml";
}
