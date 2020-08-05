# VoiceRecorder
> A simple voice channel recording bot for the Sirimangalo discord server, using the [JDA](https://github.com/DV8FromTheWorld/JDA)
> Java wrapper for the Discord API.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
# Contents

- [Commands](#commands)
- [Credits](#credits)
- [Configuration](#configuration)
- [License](#license)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Commands

|                   Command                   |                                      Description                                              |
|:--------------------------------------------|:----------------------------------------------------------------------------------------------|
| `!record`                                   | Start recording the current channel. This command must be called from a voice channel.        |   
| `!record [voicechannel]`                    | Start recording a voice channel, where `[voicechannel]` is its name.                          |
| `!stop`                                     | Stop recording.                                                                               |

## Credits

- [nwaldispuehl's](https://github.com/nwaldispuehl) and [Hanns Holger Rutz'](https://github.com/Sciss) Native Java ports 
([java-lame](https://github.com/nwaldispuehl/java-lame) and [jump3r](https://github.com/Sciss/jump3r)) 
of the [MP3 lame-3.98.4 library](https://svn.code.sf.net/p/lame/svn/trunk/lame),
based on [Ken Händel's](https://sourceforge.net/u/kenchis/profile/) 'jump3r - Java Unofficial MP3 EncodeR' project; 
**License: GNU LGPL version 3.0**.

## Configuration

For further technical details and troubleshooting notes, please click here [here](CONFIG.md).

## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
