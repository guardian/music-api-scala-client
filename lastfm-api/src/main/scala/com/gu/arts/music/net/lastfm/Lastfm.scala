package com.gu.arts.music.net.lastfm

import dispatch._
import org.slf4j.LoggerFactory

case class LastFmApiError(error: Int, message: String)
class LastFmApiException(msg: String, errorCode: Int) extends RuntimeException(msg)

trait ApiConfiguration {

  lazy val maxCacheAge = System.getProperty("http.cache.max-age", "")
  lazy val cacheControlValue = "max-age=%s".format(maxCacheAge)

  val headers = {
    if (maxCacheAge != "") Map("Cache-Control" -> cacheControlValue)
    else Map[String, String]()
  }
}

abstract class LastfmApi extends ApiConfiguration {
  implicit val formats = net.liftweb.json.DefaultFormats
  val log = LoggerFactory getLogger getClass
  val searchToken: String
  val method: String
  val host = "ws.audioscrobbler.com/2.0"

  def retrieve(token: String, apiKey: String) = {
    val query = Map("method" -> method, "api_key" -> apiKey, searchToken -> token, "format" -> "json")
    val request = :/(host) <:< headers <<? query
    log.info("Retrieving resource http://ws.audioscrobbler.com/2.0%s" format request.path)
    val h = new Http
    h(request as_str).replace("#text", "text").replace("@attr", "attr")
  }
}

object Trimmer {

  def trimName(name: String, length: Int) = {
    name.split(" ").map(word =>
      if (word.length > length) word.substring(0, length) + "... "
      else word + " ") mkString
  }

}
