# Radio

Radio plugin for Nukkit

Please see [mcbbs](http://www.mcbbs.net/thread-932833-1-1.html) for more information.
## Usage
1. Copy the audio files in OGG Vobis format to the folder "music" in the data folder of this plugin. (The file suffix must be "ogg")
2. Restart the server.

## Commands
| Command | Alias | Permission | Description | Default |
| - | - | - | - | - |
| `/radio <play\|stop>` | `/fm` <br> `/music` | radio.command | Plays/Stops audio | true |
| `/radioadmin dump` |  | radio.admin.command | Dumps radio resource packs | OP |

## config.yml
```yaml
# If true, music will play automatically when the player joins the game.
# Allowed values: "true" or "false"
autoplay: true

# Allowed values: "order" or "random"
play-mode: random
```

###### If I have any grammar and terms error, please correct my wrong :)
