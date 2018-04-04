package org.embulk.reporter.fluentd;

import org.embulk.config.ConfigSource;
import org.embulk.config.TaskSource;
import org.embulk.spi.AbstractReporterImpl;
import org.embulk.spi.Exec;
import org.embulk.spi.Reporter;
import org.embulk.spi.ReporterPlugin;
import org.komamitsu.fluency.EventTime;
import org.komamitsu.fluency.Fluency;
import org.komamitsu.fluency.sender.SenderErrorHandler;
import org.slf4j.Logger;

import javax.annotation.concurrent.ThreadSafe;

import java.io.IOException;
import java.util.Map;

public class FluentdReporterPlugin
        implements ReporterPlugin
{
    @Override
    public TaskSource configureTaskSource(final ConfigSource config)
    {
        return config.loadConfig(PluginTask.class).dump();
    }

    public AbstractReporterImpl open(TaskSource taskSource)
    {
        PluginTask task = taskSource.loadTask(PluginTask.class);
        fluentd = newFluentdService(task);
        return new FluentdReporterImpl(task);
    }

    @ThreadSafe
    private static class FluentdReporterImpl extends AbstractReporterImpl
    {
        private final PluginTask task;
        private final String tag;

        private FluentdReporterImpl(PluginTask task)
        {
            this.task = task;
            this.tag = task.getTag();
        }

        @Override
        public void report(Reporter.Level level, Map<String, Object> event)
        {
            EventTime eventTime = EventTime.fromEpochMilli(System.currentTimeMillis());
            try {
                fluentd.emit(tag, eventTime, event);
            }
            catch (IOException ex) {
                // ignore here
            }
        }

        @Override
        public void close()
        {
            try {
                if (fluentd != null && !fluentd.isTerminated()) {
                    fluentd.flush();
                    fluentd.close();
                }
            }
            catch (IOException ex) {
                // ignore here
            }
        }

        @Override
        public void cleanup()
        {
            if (fluentd != null && !fluentd.isTerminated()) {
                fluentd.clearBackupFiles();
            }
        }
    }

    private Fluency newFluentdService(final PluginTask task)
    {
        Fluency fluency = null;
        try {
            fluency = Fluency.defaultFluency(task.getHost(), task.getPort(),
                new Fluency.Config()
                    .setMaxBufferSize(task.getMaxBuffeSize() * 1024 * 1024L)
                    .setBufferChunkInitialSize(task.getBufferChunkInitialSize() * 1024 * 1024)
                    .setBufferChunkRetentionSize(task.getBufferChunkRetantionSize() * 1024 * 1024)
                    .setFlushIntervalMillis(task.getFlushIntervalMillis())
                    .setSenderMaxRetryCount(task.getMaxRetryCount())
                    .setFileBackupDir(task.getFileBackupDir().orNull())
                    .setSenderErrorHandler(new SenderErrorHandler()
                    {
                        @Override
                        public void handle(Throwable e)
                        {
                            log.warn(e.getMessage());
                        }
                    })
            );
        }
        catch (IOException ex) {
            log.warn("Failed to create Fluentd service", ex);
        }
        return fluency;
    }

    private static Fluency fluentd;
    private static final Logger log = Exec.getLogger(FluentdReporterPlugin.class);
}
