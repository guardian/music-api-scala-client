package com.gu.arts.music.net.musicmetric

import com.gu.arts.music.net.ConfiguredHttp
import dispatch.url
import dispatch.HandlerVerbs._
import net.liftweb.json.JsonParser.parse
import org.slf4j.LoggerFactory

case class Score(current: Int, total: Int, previous: Int)
case class Fans(twitter: Score, myspace: Score, youtube: Score, facebook: Score, lastfm: Score, total: Score)
case class Plays(lastfm: Score, total: Score, myspace: Score, youtube: Score, radio: Score)
case class Comments(total: Score)
case class Views(total: Score)
case class MusicMetricSummary(fans: Fans, plays: Plays, comments: Comments, views: Views)

case class ChartData(value: Option[Int], rank: Option[Int])
case class ArtistChart(musicbrainz: Option[String], name: String, ordering: ChartData)
case class FestivalChart(entities: List[ArtistChart], id: String, name: String)

case class MusicMetricApiKey(key: String)

abstract class MusicMetricApi {
  val log = LoggerFactory getLogger getClass
  implicit val formats = net.liftweb.json.DefaultFormats
  val baseUri = "http://apib2.semetric.com"

  def retrieve(partialUri: String, apiKey: MusicMetricApiKey) = {
    val uri = "%s%s?token=%s".format(baseUri, partialUri, apiKey.key)
    log.info("Retriving web resource %s" format uri)
    (parse(ConfiguredHttp(url(uri) as_str)) \ "response")
  }
}

object MusicMetricSummary extends MusicMetricApi {
  val summaryApi = "/artist/musicbrainz:%s/kpi"
  def apply(mbid: String)(implicit musicMetricApiKey: MusicMetricApiKey) = retrieve(summaryApi format mbid, musicMetricApiKey).extractOpt[MusicMetricSummary]
}

object FestivalChart extends MusicMetricApi {
  val festivalChartApi = "/chart/%s"
  def apply(festivalId: String)(implicit musicMetricApiKey: MusicMetricApiKey): Option[FestivalChart] =
    retrieve(festivalChartApi format festivalId, musicMetricApiKey).extractOpt[FestivalChart]

}
