package com.gu.arts.music.net.lastfm

import net.liftweb.json.JsonParser.parse
import java.net.URLEncoder

case class ArtistProfile(name: String, url: String, bio: ArtistBiography, tags: ArtistTags, stats: ArtistStats, image: List[ArtistImage]) {
  def getImage(imageSize: String, default: String) = {
    val flattened = image flatMap { artistImage =>
      if (imageSize == artistImage.size) Some(artistImage)
      else None
    }
    val artistImage = flattened.headOption getOrElse ArtistImage(default, "")
    artistImage.text
  }
}
case class ArtistProfileSimple(name: String, url: String, mbid: Option[String], image: List[ArtistImage]) {
  lazy val trimmedMbid = mbid map { _.trim } filter { !_.isEmpty }
  lazy val trimmedName = Trimmer.trimName(name, 12)
  lazy val trimmedNameWide = Trimmer.trimName(name, 16)
}
case class ArtistTags(tag: List[ArtistTag])
case class ArtistTag(name: String, url: String)
case class ArtistBiography(summary: String, content: String)
case class ArtistStats(listeners: String, playcount: String)
case class ArtistImage(text: String, size: String) { lazy val uri = text }
case class ArtistAlbums(album: List[ArtistAlbum])
case class ArtistAlbum(name: String, mbid: Option[String], url: String, image: List[ArtistImage]) {
  lazy val trimmedName = Trimmer.trimName(name, 12)
}
case class ArtistTracks(track: List[ArtistTrack])
case class ArtistTrack(name: String, url: String)
case class SimilarArtists(artist: List[ArtistProfileSimple])

abstract class ArtistApi extends LastfmApi {
  override val searchToken = "mbid"
}

object ArtistProfileByName extends ArtistApi {
  override val method = "artist.getinfo"
  override val searchToken = "artist"
  def apply(name: String)(implicit lastfmApiKey: String) = (parse(retrieve(URLEncoder.encode(name, "utf-8"), lastfmApiKey)) \ "artist").extractOpt[ArtistProfile]
}

object ArtistProfile extends ArtistApi {
  override val method = "artist.getinfo"
  def apply(mbid: String)(implicit lastfmApiKey: String) = (parse(retrieve(mbid, lastfmApiKey)) \ "artist").extractOpt[ArtistProfile]
}

object ArtistTracks extends ArtistApi {
  override val method = "artist.getTopTracks"
  def apply(mbid: String)(implicit lastfmApiKey: String) = (parse(retrieve(mbid, lastfmApiKey)) \ "toptracks").extractOpt[ArtistTracks]
}

object SimilarArtists extends ArtistApi {
  override val method = "artist.getSimilar"
  def apply(mbid: String)(implicit lastfmApiKey: String) = (parse(retrieve(mbid, lastfmApiKey)) \ "similarartists").extractOpt[SimilarArtists]
}

object ArtistLastFmApiError extends ArtistApi {
  override val method = "artist.getinfo"
  def apply(mbid: String)(implicit lastfmApiKey: String) = (parse(retrieve(mbid, lastfmApiKey))).extractOpt[LastFmApiError]
}

object ArtistAlbums extends ArtistApi {

  override val method = "artist.getTopAlbums"

  def apply(mbid: String)(implicit lastfmApiKey: String): Option[ArtistAlbums] = {
    val result = retrieve(mbid, lastfmApiKey)
    val json = (parse(result) \ "topalbums").extractOpt[ArtistAlbums]
    json match {
      case Some(j) => json
      case None => {
        val album = (parse(result) \ "topalbums" \ "album").extractOpt[ArtistAlbum]
        album match {
          case None => None
          case Some(album) => Some(ArtistAlbums(List(album)))
        }
      }
    }
  }

}
