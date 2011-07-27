package com.gu.arts.music.demo

import com.gu.arts.music.net.lastfm.{LastfmApiKey, ArtistProfile}
import com.gu.arts.music.net.musicmetric.{MusicMetricApiKey, MusicMetricSummary}

object Demo extends Application {

    implicit val lastfmApiKey = LastfmApiKey("bar")
    implicit val musicMetricApiKey = MusicMetricApiKey("bar")
    val artistProfile = ArtistProfile("foo")
    val summary = MusicMetricSummary("foo")
    println(artistProfile)
    println(summary)
}   