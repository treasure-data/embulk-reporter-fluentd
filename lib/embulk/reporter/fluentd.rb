Embulk::JavaPlugin.register_reporter(
  "fluentd", "org.embulk.reporter.fluentd.FluentdReporterPlugin",
  File.expand_path('../../../../classpath', __FILE__))
