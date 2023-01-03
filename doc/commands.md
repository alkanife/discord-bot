<h3 align="center">
  <b><a href="https://github.com/alkanife/alkabot/">⬅️ Go back</a></b>
  •
  <a href="#admin">Admin</a>
  •
  <a href="#information">Information</a>
  •
  <a href="#utilities">Utilities</a>
  •
  <a href="#music">Music</a>
</h3>

## Admin
> These commands are accessible in the bot's DM, and can only be used by administrators. Most commands can be used in the terminal.

`status`: Show uptime and RAM usage

`stop`: Shutdown the bot

## Information

`about`: Give a link to this GitHub repo. (will change in future updates)

## Utilities

`copy <message url>`: Copy a Discord message, but keep the syntax. Example: ":shipit: ***I'm a potato.***" will result `:shipit: ***I'm a potato.***`

`info server`: About the server

`info emote <emote>`: About an emote

`info member <member>` About a member

## Music

> The "query" is what will be used to determine what will be played. It can be an link to a supported source (see [lavaplayer supported formats](https://github.com/sedmelluq/lavaplayer#supported-formats)), a link to a Spotify playlist (https://open.spotify.com/playlist/) if the configuration allow it, or the name of a shortcut.

> By default, Alkabot will search the query on YouTube if it's none of the above.

`play <query>`: Start the jukebox and/or add a track to the queue.

`playnext <query>`: Start the jukebox and/or add a track to the very first position in the queue.

`forceplay <query>`: Same as /playnext, but will skip the playing track.

`skip [jump]`: Make a jump of x track(s) in the queue. This will skip the playing track by default.

`remove [track number]`: Will remove the track N°x from the queue. You can know the track number with /queue. This will remove the last track of the queue by default.

`queue [page]`: Show the now playing track and the queue.

`shuffle`: Shuffle the queue.

`clear`: Clear the queue.

`stop`: Stop the jukebox, but do not clear the queue.

`destroy`: Stop the jukebox and clear the queue.

`shortcut bind <name> <query>`: This will bind a name to a query. For example, by creating a shortcut named "lofi" with the following query "https://www.youtube.com/watch?v=jfKfPfyJRdk", instead of doing /play with entering the url each time, you just have to do "/play lofi".

`shortcut unbind <name>`: Delete a shortcut.

`shortcut info <name>`: Display the creation date, author, and query of a shortcut.

`shortcut list`: List shortcuts.