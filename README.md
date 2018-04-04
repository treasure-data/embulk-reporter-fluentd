# Fluentd Reporter plugin for Embulk

Send events/logs to somewhere using [Fluentd](https://www.fluentd.org/)

## Overview

* **Plugin type**: Reporter

## Configuration

- **host**: (string, default: `localhost`)
- **hosts**: list of host. hosts are pairs of host and port (list, required)
  - **host**: (integer, required)
  - **port**: (integer, default: `24224`)

- **max_buffer_size**: (string, default: `16`)
- **buffer_chunk_initial_size**: (string, default: `1`)
- **buffer_chunk_retention_size**: (string, default: `16`)
- **flush_interval_millis**: (string, default: `600`)
- **max_retry_count**: (string, default: `12`)
- **file_backup_dir**: (string, default: `null`)

## Example

```yaml
in:
  type: s3
  parser:
    ... 
out:
  type: stdout
reporters:
  skipped_data:
    type: fluentd
    host: localhost
    port: 24224
    tag: debug.test
  log:
    type: stdout
```

## Build

```
$ ./gradlew gem  # -t to watch change of files and rebuild continuously
```
