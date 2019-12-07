# Music-DL
[![Join the chat at https://gitter.im/Music-DL-KWoC/community](https://badges.gitter.im/Music-DL-KWoC/community.svg)](https://gitter.im/Music-DL-KWoC/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

An android application that let's you download YouTube videos in mp3 and mp4 format. You can download the android
application [here](https://github.com/CaptainDaVinci/Music-DL/releases/tag/v1.0).


## Working

The application searches for the song on youtube by using the youtube API. The youtube url is then handed over
to backend [flask application](https://github.com/CaptainDaVinci/Music-DL-Server) which converts the video to mp3 
or mp4 file and then returns the link to the file back to the application.


## Contibuting

To start contributing you can clone the project in android studio from GitHub 
([how to?](https://www.londonappdeveloper.com/how-to-clone-a-github-project-on-android-studio/)), 
if you find any issues or want to suggest features, you can do so under the issues tab or on the gitter channel.
