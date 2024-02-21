[![Release](https://jitpack.io/v/umjammer/vavi-util-tag.svg)](https://jitpack.io/#umjammer/vavi-util-tag)
[![Java CI](https://github.com/umjammer/vavi-util-tag/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/vavi-util-tag/actions/workflows/maven.yml)
[![CodeQL](https://github.com/umjammer/vavi-util-tag/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-util-tag/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-17-b07219)

# vavi-util-tag

manipulate mp3, m4a tag for java

## Status

| **Type** | **Description** | **IN Status** | **OUT Status** |
|:---------|:----------------|:--------------|:---------------|
| mp3 v1   |                 | ✅            | ?             |
| mp3 v2   |                 | ✅            | ✅            |
| mp4      |                 | ✅            | ?             |
| ITCBox   | iTunes Artwork  | ✅            | -             |

## Install

 * [maven](https://jitpack.io/#umjammer/vavi-util-tag)

## Usage

practicality is sufficient because it was actually applied to my itunes library (20,000 mp3s).

 * [List unnecessary mp3 v2 tags](https://github.com/umjammer/vavi-util-tag/blob/master/src/test/java/MP3ShowTagUnnecessaryByWalk.java)
 * [Delete unnecessary mp3 v2 tags](https://github.com/umjammer/vavi-util-tag/blob/master/src/test/java/MP3RemoveTagUnnecessaryByWalk2.java)
 * [List iTunes artwork](https://github.com/umjammer/vavi-util-tag/blob/master/src/test/java/vavi/util/itunes/artwork/ITCBoxFactoryTest.java)

## TODO

 * apply to `tritonus` mp3 (tritonus mp3 spi cannot deal tags)
 * use `vavi-util-serdes`
 * rename to `vavi-sound-tag`?
 * unit test
