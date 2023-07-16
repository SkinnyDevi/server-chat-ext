# SKDV's Server Chat Extended

Welcome to the best chat mod you will find for Forge!

Packed with many features you'd desire from bukkit/spigot servers!

***Working on SinglePlayer and Multiplayer!*** ***(This mod is only needed server-side!)***

![welcome_image](https://raw.githubusercontent.com/SkinnyDevi/server-chat-ext/master/images/intro_image1.png)
![prefix_show](https://raw.githubusercontent.com/SkinnyDevi/server-chat-ext/master/images/intro_image2.png)

## Main features:
- Chat Colours! Use chat colours every where you want
- Prefixes and Suffixes! Custom names to be added to express roles and many more things!
- Allow no opped players to change their own prefixes and suffixes!
- Broadcast command!

## Commands
### Change your custom text
For Opped players:

``
/changeext change (suffix/prefix) (playername) "your custom text"
``

For normal players (config must be correctly adjusted):

``
/changeext change (customsuffix/customprefix) "your custom text"
``

### Reset your prefix/suffix
For Opped players:

``
/changeext reset (all/suffix/prefix) (playername)
``

For normal players (config must be correctly adjusted):

``
/changeext change (customall/customsuffix/customprefix)
``

## Default configuration
```toml
["Server Chat Extended configuration"]
#Enable this chat mod. Defaults to true
enable_chatext = true
#Enable a timestamp besides the message sent to indicate the time when the message was sent. Defaults to true
enable_chat_timestamp = true
#Allow setting a prefix in chat. Defaults to true
allow_prefix = true
#Allow setting a suffix in chat. Defaults to true
allow_suffix = true
#Allows players which are not OP to change their own prefix and suffix. Defaults to false
allow_noop_change = false
#Set the format of the timestmap represented on chat.
#Examples can be found here: https://www.digitalocean.com/community/tutorials/java-simpledateformat-java-date-format#java-simpledateformat
#Defaults to "HH:mm".
timestamp_format = "HH:mm"
#Colour of the timestamp. Use minecraft colour codes with '&'. Examples: &c, &l, &5.
#Defaults to gray (&7).
timestamp_colour = "&7"

```
