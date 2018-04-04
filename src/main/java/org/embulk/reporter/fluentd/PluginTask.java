package org.embulk.reporter.fluentd;

import com.google.common.base.Optional;
import org.embulk.config.Config;
import org.embulk.config.ConfigDefault;
import org.embulk.config.Task;

public interface PluginTask
            extends Task
{
    @Config("tag")
    String getTag();

    @Config("host")
    String getHost();

    @Config("port")
    @ConfigDefault("24224")
    int getPort();

    @Config("max_buffer_size")
    @ConfigDefault("16")
    int getMaxBuffeSize();

    @Config("buffer_chunk_initial_size")
    @ConfigDefault("1")
    int getBufferChunkInitialSize();

    @Config("buffer_chunk_retention_size")
    @ConfigDefault("16")
    int getBufferChunkRetantionSize();

    @Config("flush_interval_millis")
    @ConfigDefault("600")
    int getFlushIntervalMillis();

    @Config("max_retry_count")
    @ConfigDefault("12")
    int getMaxRetryCount();

    @Config("file_backup_dir")
    @ConfigDefault("null")
    Optional<String> getFileBackupDir();
}

