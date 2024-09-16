# Alkabot

[![Licence](https://img.shields.io/github/license/alkanife/discord-bot?style=for-the-badge)](LICENSE)
[![Java version 17 or above](https://img.shields.io/badge/Java-17%2B-FF160B?style=for-the-badge)](pom.xml)
[![Version - 3.0-indev](https://img.shields.io/badge/Version-3.0--indev-blueviolet?style=for-the-badge)](pom.xml)
[![Jenkins Build](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fjenkins.alka.dev%2Fjob%2Fdiscord-bot%2F&style=for-the-badge)](https://jenkins.alka.dev/job/discord-bot/)

## Usage

```
Usage....: java -jar Alkabot.jar [options...]
           java -jar Alkabot.jar options.txt

Example..: java -jar Alkabot.jar -setup
           java -jar Alkabot.jar -start -debug ...

If you just have placed the bot JAR inside this directory, you must make a setup.
To do so, use the option '-setup', it will create all the files the bot needs.
However, some default values (like the secrets) must be filled manually.

Options:
   -help, -h                  Display usage
   -version, -v               Display version
   -setup                     Create default files and setup directories
   -start                     Start the bot!
   -debug                     Enable debug mode
   -debug-jda                 Enable debug mode for JDA and Lavaplayer
   -track-time                Track loading times (for debug purposes)
   -disable-file-logging      Disable file logging
   -latest-log-file-path      Path to the latest log file, with extension
   -archive-log-file-path     Path to the log archive, with name pattern
   -log-file-max-size         Maximum size of a log file
   -log-file-total-size-cap   Maximum size of all log files
   -log-archive-max-history   Maximum number of log files to keep
   -secret-file-path          Path to the secret file, with extension
   -config-file-path          Path to the configuration file, with extension
   -music-data-file-path      Path to the music data, with extension
   -override-lang-file-path   Override the language pack of the configuration
   -override-secrets          Override secrets (raw json)
```
