package org.embulk.reporter.fluentd;

import com.google.common.base.Optional;

import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.ConfigSource;
import org.embulk.config.Task;
import org.embulk.config.TaskSource;
import org.embulk.spi.AbstractReporterImpl;
import org.embulk.spi.ReporterPlugin;

public class FluentdReporterPlugin
        implements ReporterPlugin
{
    public interface PluginTask
            extends Task
    {
        // configuration option 1 (required integer)
        @Config("option1")
        public int getOption1();

        // configuration option 2 (optional string, null is not allowed)
        @Config("option2")
        @ConfigDefault("\"myvalue\"")
        public String getOption2();

        // configuration option 3 (optional string, null is allowed)
        @Config("option3")
        @ConfigDefault("null")
        public Optional<String> getOption3();
    }

    @Override
    public TaskSource configureTaskSource(final ConfigSource config)
    {
        return config.loadConfig(Task.class).dump();
    }

    public AbstractReporterImpl open(TaskSource taskSource)
    {
        PluginTask task = taskSource.loadTask(PluginTask.class);
        return new ReporterImpl();
    }

    @ThreadSafe
    private static class ReporterImpl extends AbstractReporterImpl {
        @Override
        public void report(Reporter.Level level, Map<String, Object> event) {
            System.out.println(event);
        }

        @Override
        public void close() {}

        @Override
        public void cleanup() {}
    }
}
