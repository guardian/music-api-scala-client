package com.gu.arts.music.net.lastfm

import dispatch._
import org.joda.time.DateTime
import net.liftweb.json.JsonParser.parse
import org.slf4j.LoggerFactory
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
  lazy val trimmedName = Trimmer.trimName(name)
}
case class ArtistTags(tag: List[ArtistTag])
case class ArtistTag(name: String, url: String)
case class ArtistBiography(summary: String, content: String)
case class ArtistStats(listeners: String, playcount: String)
case class ArtistImage(text: String, size: String) { lazy val uri = text }
case class ArtistAlbums(album: List[ArtistAlbum])
case class ArtistAlbum(name: String, mbid: Option[String], url: String, image: List[ArtistImage]) {
  lazy val trimmedName = Trimmer.trimName(name)
}
case class ArtistTracks(track: List[ArtistTrack])
case class ArtistTrack(name: String, url: String)
case class SimilarArtists(artist: List[ArtistProfileSimple])
case class LastFmApiError(error: Int, message: String)
class LastFmApiException(msg: String, errorCode: Int) extends RuntimeException(msg)

trait LastfmProperties {
    val apiKey: String
}

abstract class LastfmApi {
  implicit val formats = net.liftweb.json.DefaultFormats
  val log = LoggerFactory getLogger getClass
  val lastfmApiUri = "http://ws.audioscrobbler.com/2.0/?method=%s&api_key=%s&mbid=%s&format=json"
  val method = ""

  def retrieve(token: String, apiKey: String) = {
    val uri = lastfmApiUri.format(method, apiKey, token)
    log.info("Retriving web resource %s" format uri)
    val h = new Http
    h(url(uri) as_str).replace("#text", "text")
  }
}

object Trimmer {
  def trimName(name: String) = {
    name.split(" ").map(word =>
      if (word.length > 12) word.substring(0, 12) + "... "
      else word + " ") mkString
  }
}

object ArtistProfileByName extends LastfmApi {
  override val method = "artist.getinfo"
  override val lastfmApiUri = "http://ws.audioscrobbler.com/2.0/?method=%s&api_key=%s&artist=%s&format=json"
  def apply(name: String)(implicit lastfmApiKey: String) = (parse(retrieve(URLEncoder.encode(name, "utf-8"), lastfmApiKey)) \ "artist").extractOpt[ArtistProfile]
}

object ArtistProfile extends LastfmApi {
  override val method = "artist.getinfo"
  def apply(mbid: String)(implicit lastfmApiKey: String) = (parse(retrieve(mbid, lastfmApiKey)) \ "artist").extractOpt[ArtistProfile]
}

object ArtistTracks extends LastfmApi {
  override val method = "artist.getTopTracks"
  def apply(mbid: String)(implicit lastfmApiKey: String) = (parse(retrieve(mbid, lastfmApiKey)) \ "toptracks").extractOpt[ArtistTracks]
}

object SimilarArtists extends LastfmApi {
  override val method = "artist.getSimilar"
  def apply(mbid: String)(implicit lastfmApiKey: String) = (parse(retrieve(mbid, lastfmApiKey)) \ "similarartists").extractOpt[SimilarArtists]
}

object LastFmApiError extends LastfmApi {
  override val method = "artist.getinfo"
  def apply(mbid: String)(implicit lastfmApiKey: String) = (parse(retrieve(mbid, lastfmApiKey))).extractOpt[LastFmApiError]
}

object ArtistAlbums extends LastfmApi {

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
