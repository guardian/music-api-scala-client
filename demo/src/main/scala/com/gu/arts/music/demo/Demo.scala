package com.gu.arts.music.demo

import com.gu.arts.music.net.lastfm.ArtistProfile

object Demo extends Application {

    implicit val lastfmApiKey = "bar"
    val artistProfile = ArtistProfile("foo")
    
}