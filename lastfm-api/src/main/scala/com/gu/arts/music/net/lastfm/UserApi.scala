package com.gu.arts.music.net.lastfm

case class UserTopArtists(artists: List[ArtistProfileSimple])

abstract class UserApi extends LastfmApi {
  override val searchToken = "user"
}

object UserTopArtists extends UserApi {
  override val method = "user.gettopartists"
  def apply(username: String)(implicit lastfmApiKey: String) = retrieve(username, lastfmApiKey)
}