package com.gu.arts.music.net.lastfm

import net.liftweb.json.JsonParser.parse

case class UserTopArtists(artist: List[ArtistProfileSimple])

abstract class UserApi extends LastfmApi {
  override val searchToken = "user"
}

object UserTopArtists extends UserApi {
  override val method = "user.gettopartists"
  def apply(username: String)(implicit lastfmApiKey: String) = (parse(retrieve(username, lastfmApiKey)) \ "topartists").extractOpt[UserTopArtists]
}