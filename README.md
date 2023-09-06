<img align="right" src="https://share.alkanife.dev/alkabot.png" height="200" width="200">
<h1 align="center">
  Alkabot
  <br>
  <a href="https://github.com/alkanife/alkabot/blob/main/pom.xml">
    <img src="https://img.shields.io/badge/Open%20JDK-17-green" alt="JDK 17">
  </a>
  <a href="https://www.codefactor.io/repository/github/alkanife/alkabot">
    <img src="https://www.codefactor.io/repository/github/alkanife/alkabot/badge" alt="Codefactor">
  </a>
  <a href="https://github.com/alkanife/alkabot/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/alkanife/alkabot" alt="LICENSE">
  </a>
  <a href="https://github.com/alkanife/alkabot/releases/tag/2.0.0-dev2">
    <img src="https://img.shields.io/badge/version-2.0.0--infdev-blue" alt="version">
  </a>
</h1>

<p align="center">
  <b><a href="#overview">Overview</a></b>
  •
  <a href="#usage">Usage</a>
  •
  <a href="https://github.com/alkanife/alkabot/wiki">Wiki</a>
  •
  <a href="#project-dependencies">Project dependencies</a>
  •
  <a href="#license">License</a>
</p>

## Overview
Alkabot is a self-hosted, highly configurable Discord bot made in Java.

This is a bot designed to be unique to a single Discord server, as a private bot.

This project is currently in its development phase for version 2.0.0, some features are in work in progress and subject to change or deletion.

### Features
- Welcome messages
- On-join auto-role
- Music system
  - Support for [all lavaplayer supported formats](https://github.com/sedmelluq/lavaplayer#supported-formats)
  - Support for reading Spotify playlists (the bot will use the title of the music present in the Spotify playlist to search for it on YouTube.)
  - Shortcut system
- Logging system (called "notifications")

A list of commands is [available here](https://github.com/alkanife/alkabot/wiki/Commands)!

## Usage
You need **Java 17** or above to use Alkabot. Once downloaded, you need to generate default files with the `--generateFiles` flag.

See the wiki for [Configuration](https://github.com/alkanife/alkabot/wiki/Configuration) & [Tokens](https://github.com/alkanife/alkabot/wiki/Tokens).

```
Usage: java -jar alkabot.jar [options]
  Options:
    --config, -c
      Configuration file path
      Default: config.json
    --data, -d
      Data folder path
      Default: data
    --debug, -dev
      Enable debug mode
      Default: false
    --debugall, -devall
      Enable debug mode for everything
      Default: false
    --debugjda, -devjda
      Enable debug mode for JDA and lavaplayer
      Default: false
    --generateFiles, -gf
      Generate default files
      Default: false
    --help, -h
      Print usage
      Default: false
    --langs, -L
      Lang folder path
      Default: lang
    --logs, -l
      Logs folder path
      Default: logs
    --tokens, -t
      Tokens file path
      Default: tokens.json
    --version, -v
      Print build version
      Default: false
```

## Project dependencies
See [Maven dependencies](https://github.com/alkanife/alkabot/blob/main/pom.xml).

## License
Under the [Mozilla Public License 2.0](https://github.com/alkanife/alkabot/blob/main/LICENSE) license.
