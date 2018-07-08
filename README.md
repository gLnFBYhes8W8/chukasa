# chukasa 

[![Build Status](https://travis-ci.org/hirooka/chukasa.svg?branch=master)](https://travis-ci.org/hirooka/chukasa) [![Build Status](https://circleci.com/gh/hirooka/chukasa.png?style=shield)](https://circleci.com/gh/hirooka/chukasa)

HTTP Live Streaming (HLS) server that distributes...

- video file on demand
- live stream of webcam
- live stream of tuner (with [hyakaruka (https://github.com/hirooka/hyaruka)](https://github.com/hirooka/hyaruka))
- captured video file by okkake

to cross-platform.

And it records stream of tuner by scheduler. (EXPERIMENTAL)

## Server

### Ubuntu 18.04 (with NVENC)

- Java 10
- FFmpeg 4.0.1
- MongoDB 3.6
- NVIDIA Geforce GTX 10 Series

### CentOS 7.4 (with QSV)

- Java 10
- FFmpeg 4.0.1
- MongoDB 3.6
- Intel Media Server Studio 2018 R1

### Windows 10 (with QSV)
- Java 10
- FFmpeg 4.0.1
- MongoDB 4.0
- Intel CPU

### Raspbian Stretch (with OpenMAX)

- Java 9
- FFmpeg 4.0.1
- MongoDB 3.0
- Raspberry Pi 3 Model B

## Client

- iOS (Safari on iPhone, iPad, iPod)
- macOS (Safari)
- tvOS (via AirPlay from iOS or macOS)
- Windows 10 (Microsoft Edge)
- Android (Google Chrome)
- Oculus Go (Browser)