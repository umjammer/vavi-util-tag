[![Maven Package](https://github.com/umjammer/vavi-util-tag/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/umjammer/vavi-util-tag/actions/workflows/maven-publish.yml)
[![Java CI](https://github.com/umjammer/vavi-util-tag/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/vavi-util-tag/actions/workflows/maven.yml)
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

## Install

 * https://github.com/umjammer/vavi-util-tag/packages/1623134
 * this project uses github packages. add a personal access token to `~/.m2/settings.xml`
 * see https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry

## Usage

 * [List unnecessary mp3 v2 tags](https://github.com/umjammer/vavi-util-tag/blob/master/src/test/java/Test7_2.java)
 * [Delete unnecessary mp3 v2 tags](https://github.com/umjammer/vavi-util-tag/blob/master/src/test/java/Test7.java)
 * [List iTunes artwork](https://github.com/umjammer/vavi-util-tag/blob/master/src/test/java/vavi/util/itunes/artwork/ITCBoxFactoryTest.java)

## TODO

 * apply to `tritonus` mp3 (tritonus mp3 spi cannot deal tags)
 * use `vavi-util-serdes`
 * rename to `vavi-sound-tag`?
 * unit test
