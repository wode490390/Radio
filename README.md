# Radio for Nukkit
[![Nukkit](https://img.shields.io/badge/Nukkit-1.0-green)](https://github.com/NukkitX/Nukkit)
[![Build](https://img.shields.io/circleci/build/github/wode490390/Radio/master)](https://circleci.com/gh/wode490390/Radio/tree/master)
[![Release](https://img.shields.io/github/v/release/wode490390/Radio)](https://github.com/wode490390/Radio/releases)
[![Release date](https://img.shields.io/github/release-date/wode490390/Radio)](https://github.com/wode490390/Radio/releases)
<!--[![MCBBS](https://img.shields.io/badge/-mcbbs-inactive)](https://www.mcbbs.net/thread-932833-1-1.html "音乐电台")
[![Servers](https://img.shields.io/bstats/servers/6082)](https://bstats.org/plugin/bukkit/Radio/6082)
[![Players](https://img.shields.io/bstats/players/6082)](https://bstats.org/plugin/bukkit/Radio/6082)-->

Radio is a plugin that allows the client to play specified background music.

If you found any bugs or have any suggestions, please open an issue on [GitHub Issues](https://github.com/wode490390/Radio/issues).

If you like this plugin, please star it on [GitHub](https://github.com/wode490390/Radio).

## Usage
1. Copy audio files in **OGG Vobis format** (file suffix must be `ogg`) to "Path_to_Nukkit/plugins/Radio/**music**/".
2. Restart the server.
3. Audio files will be automatically sent to the client when the player joins the game for the first time.

## Download
- [Releases](https://github.com/wode490390/Radio/releases)
- [Snapshots](https://circleci.com/gh/wode490390/Radio)

## Commands
| Command | Alias | Permission | Description | Default |
| - | - | - | - | - |
| `/radio [play\|stop]` | `/fm` <br> `/music` | radio.command | Plays/Stops audio | true |
| `/radioadmin dump` |  | radio.admin.command | Dumps radio resource packs | OP |

## Configuration

<details>
<summary>config.yml</summary>

```yaml
# If true, music will play automatically when the player joins the game.
# Allowed values: "true" or "false"
autoplay: true

# Allowed values: "order" or "random"
play-mode: random
```
</details>

## Compiling
1. Install [Maven](https://maven.apache.org/).
2. Run `mvn clean package`. The compiled JAR can be found in the `target/` directory.

## Metrics Collection

This plugin uses [bStats](https://github.com/wode490390/bStats-Nukkit). You can opt out using the global bStats config; see the [official website](https://bstats.org/getting-started) for more details.

<!--[![Metrics](https://bstats.org/signatures/bukkit/Radio.svg)](https://bstats.org/plugin/bukkit/Radio/6082)-->

###### If I have any grammar and/or term errors, please correct them :)
