<h3 align="center">
  <b><a href="https://github.com/alkanife/alkabot/">⬅️ Go back</a></b>
</h3>

## [tokens.json](https://github.com/alkanife/alkabot/blob/main/template_folder/tokens.json)

| Name | Can be null? | Description |
| -- | -- | -- |
| `discord_token` | No | Your bot token (see [Discord Documentation](https://discord.com/developers/docs/intro)). |
| `spotify.client_id` | Yes | See [Spotify Documentation](https://developer.spotify.com/documentation/web-api/). Disables support for Spotify playlists if null. |
| `spotify.client_secret` | Yes | See [Spotify Documentation](https://developer.spotify.com/documentation/web-api/). Disables support for Spotify playlists if null. |

## [configuration.json](https://github.com/alkanife/alkabot/blob/main/template_folder/configuration.json)

<p align="center">
  <a href="#admin">Admin</a>
  •
  <a href="#guild">Guild</a>
  •
  <a href="#welcome-messages">Welcome messages</a>
  •
  <a href="#on-join-auto-role">On-join auto-role</a>
  •
  <a href="#music-settings">Music settings</a>
  •
  <a href="#commands">Commands</a>
  •
  <a href="#notifications">Notifications</a>
</p>

> :warning: The bot is currently in its development phase for version 2.0.0, some features are not fully operational/available, despite being present in the config.

| Name | Can be null? | Description |
| -- | -- | -- |
| `lang_file` | No | Path to the lang.json file. |
| `shortcut_file` | Yes | Path to the shortcuts.json file. Will use "shortcuts.json" if null. |
| `debug` | Boolean | Debug mode activation. If Alkabot is started with the "debug" argument, this parameter is ignored. |

### Admin
| Name | Can be null? | Description |
| -- | -- | -- |
| `metrics_for_nerds` | Boolean | [Not yet implemented] |
| `admin_only` | Boolean | If set to true, the bot will only respond to administrators. |
| `administrators_id` | Yes | List of administrator IDs. |

### Guild
| Name | Can be null? | Description |
| -- | -- | -- |
| `guild_id` | No | The Discord server ID. |
| `presence.status` | Yes | The bot status (see [JDA OnlineStatus](https://javadoc.io/doc/net.dv8tion/JDA/latest/net/dv8tion/jda/api/OnlineStatus.html)). `ONLINE` by default. |
| `presence.activity.show` | Boolean | If set to true the bot will display an activity in its profile. If the type or text is null, the activity will not display. |
| `presence.activity.type` | Yes | The type of activity (see [JDA ActivityType](https://javadoc.io/doc/net.dv8tion/JDA/latest/net/dv8tion/jda/api/entities/Activity.ActivityType.html)). |
| `presence.activity.text` | Yes | This is the text that appears after the name of the activity. Example: **Watching** `logs` |

### Welcome messages
| Name | Can be null? | Description |
| -- | -- | -- |
| `enable` | Boolean | Welcome messages activation. |
| `channel_id` | Yes | The channel the bot will send the welcome message. Welcome messages are disabled if this parameter is null. |

### On-join auto-role
| Name | Can be null? | Description |
| -- | -- | -- |
| `enable` | Boolean | Auto-role activation. |
| `role_id` | Yes | The role the bot will give to a new member. Auto-role is disabled if this parameter is null. |

### Music settings
| Name | Can be null? | Description |
| -- | -- | -- |
| `stop_when_alone` | Boolean | Decide whether the bot should leave the voice channel when alone or not |

### Commands
Allows you to choose the active commands of the bot. Disabled commands will not show up on Discord.

### Notifications

<p align="center">
  <a href="#self-admin">Self/Admin</a>
  •
  <a href="#message">Message</a>
  •
  <a href="#member">Member</a>
  •
  <a href="#moderator">Moderator</a>
  •
  <a href="#voice">Voice</a>
  •
  <a href="#guild-notifications">Guild</a>
</p>

`channel_id`: The channel where notifications will be sent. Disables notifications for its category if null.

#### Self/Admin
| Name | Description |
| -- | -- |
| `admin` | Will send a message when the bot is starting or shutting down. |
| `commands` | Will send a message when someone executes a command. |

#### Message
| Name | Description |
| -- | -- |
| `cache` | Define how many messages the bot should keep in mind to log deletes and edits. I recommend not going over 20 or 30. |
| `edit` | Will send a message when someone edit a message. If the message is present in the cache, the bot will log the message before modification. |
| `delete` | Will send a message when someone delete a message. If the message is present in the cache, the bot will log the message before deletion. |

#### Member
| Name | Description |
| -- | -- |
| `join` | Will send a message when someone join the Discord server. |
| `leave` | Will send a message when someone leave (excluding kicks) the Discord server. |

#### Moderator
| Name | Description |
| -- | -- |
| `ban` | Will send a message when a user is banned from the Discord server. |
| `unban` | Will send a message when a moderator unban someone. |
| `kick` | Will send a message when a moderator kick a member. |
| `timeout` | **EXPERIMENTAL** Will send a message if the bot detect a change in the timeout of someone. However, it is very limited. This gives access only to the old timeout duration and the new one, and does not always trigger when a moderator timeout a member. |
| `deafen_member` | Will send a message when a moderator deafen a member. |
| `undeafen_member` | Will send a message when a moderator undeafen a member. |
| `mute_member` | Will send a message when a moderator mute a member. |
| `unmute_member` | Will send a message when a moderator unmute a member. |
| `change_member_nickname` | Will send a message when a moderator change the nickname of a member. |

#### Voice
| Name | Description |
| -- | -- |
| `join` | Will send a message when someone joins a voice channel. |
| `leave` | Will send a message when someone leave a voice channel. |
| `move` | Will send a message when someone has moved from one voice channel to another. |

#### Guild notifications
[Not yet implemented]