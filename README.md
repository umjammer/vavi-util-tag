[![Release](https://jitpack.io/v/umjammer/vavi-util-tag.svg)](https://jitpack.io/#umjammer/vavi-util-tag)
[![CodeQL](https://github.com/umjammer/vavi-util-tag/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/umjammer/vavi-util-tag/actions/workflows/codeql-analysis.yml)
![Java](https://img.shields.io/badge/Java-8-b07219)

# vavi-util-tag

manipulate mp3, m4a tag for java

## Status

| **Type** | **Description** | **IN Status** | **OUT Status** |
|:---------|:----------------|:--------------|:---------------|
| mp3 v1   |                 | ✅            | ?             |
| mp3 v2   |                 | ✅            | ✅            |
| mp4      |                 | ✅            | ?             |
| ITCBox   | iTunes Artwork  | ✅            | -             |

## Usage

 * [List unnecessary mp3 v2 tags](https://github.com/umjammer/vavi-util-tag/blob/master/src/test/java/Test7_2.java)
 * [Delete unnecessary mp3 v2 tags](https://github.com/umjammer/vavi-util-tag/blob/master/src/test/java/Test7.java)
 * [List iTunes artwork](https://github.com/umjammer/vavi-util-tag/blob/master/src/test/java/vavi/util/itunes/artwork/ITCBoxFactoryTest.java)

## TODO

 * apply to `tritonus` mp3 (tritonus mp3 spi cannot deal tags)
 * use `vavi-util-serdes`
 * rename to `vavi-sound-tag`?