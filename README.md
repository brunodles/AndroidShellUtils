# AndroidShellUtils
Multiple Tools that can be used from CLI to help android devs life

## About this project
This project will be implemented in these submodules:
* [x] utils - broad helpers for other modules
* [x] tables builder - a helper for building content aware tables for monospace text
* [x] shell - library wrapper around native shell app
* [x] shell adb wrapper - library that wraps around 'adb shell' using 'shell' module
* [x] droid shell - application for 'shell adb wrapper'
* [x] ~~droid file sync - a tool to sync remote files on android~~ _simplified version directly in bash_
* [x] droid device - a tool to listen to connected devices, like 'adb devices' with more information
* [ ] ~~local script evaluator that allows to send commands directly to 'adb shell run-as'~~ _won't do_
* [x] file query - a library to build query-like search on files
* [x] file query interpreter groovy - a groovy application to act as a shell interpreter for query-scripts using file query
* [x] query interpreter - a groovy application working as shell interpreter using the GQ instead of file query

## Inspiration / motivation
The overall idea came from [other repository](https://github.com/brunodles/linux-second-screen)
where I did start to have these ideas, or they are implemented using just bash.

## Download
Not published yet.
May require to clone this repo and install locally.

## Usage
Jump into submodule folder to check how use each of them.

