<img align="right" src="https://share.alkanife.fr/alkabot.png" height="200" width="200">
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
  <a href="https://github.com/alkanife/alkabot/">
    <img src="https://img.shields.io/badge/version-2.0.0--dev1-blue" alt="version">
  </a>
</h1>

<p align="center">
  <b><a href="#overview">Overview</a></b>
  •
  <a href="https://github.com/alkanife/alkabot/blob/main/doc/commands.md">Commands</a>
  •
  <a href="#useinstallation">Use/Installation</a>
  •
  <a href="https://github.com/alkanife/alkabot/blob/main/doc/config.md">Configuration</a>
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
- **[Work in Progress]** Logging system (called "notifications")

A list of commands is [available here](https://github.com/alkanife/alkabot/blob/main/doc/commands.md)!

## Use/Installation
You need **Java 17** or above to use Alkabot.

### Installation
Download the latest release and the `template_folder.zip`, place Alkabot in a new folder with the content of the template folder, [configure it](#configuration), and you are good to go.

Your folder should look like this:
```
Alkabot/
├─ logs/
├─ Alkabot-<version>.jar
├─ configuration.json
├─ tokens.json
├─ lang-en.json
├─ lang-fr.json
├─ start.bat
├─ start.sh
```

### Usage
```
java -jar Alkabot.jar help
java -jar alkabot.jar [debug/prod] [tokens file path] [configuration file path]

Default: java -jar Alkabot.jar prod tokens.json configuration.json
```

## Project dependencies
This project requires **Java 17+**.

- [DV8FromTheWorld/**JDA**](https://github.com/DV8FromTheWorld/JDA)
- [sedmelluq/**lavaplayer**](https://github.com/sedmelluq/lavaplayer)
- [google/**gson**](https://github.com/google/gson)
- [qos-ch/**logback**](https://github.com/qos-ch/logback)
- [**spotify-web-api-java**](https://github.com/spotify-web-api-java/spotify-web-api-java)

## License
Under the [Mozilla Public License 2.0](https://github.com/alkanife/alkabot/blob/main/LICENSE) license.
