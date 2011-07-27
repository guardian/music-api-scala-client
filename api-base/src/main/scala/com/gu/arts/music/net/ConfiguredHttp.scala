package com.gu.arts.music.net

import dispatch._
import dispatch.thread.ThreadSafeHttpClient
import org.apache.http.params.HttpParams
import org.apache.http.conn.params.ConnRouteParams
import org.apache.http.HttpHost
import org.slf4j.LoggerFactory

object ConfiguredHttp extends Http {
  val logger = LoggerFactory getLogger getClass
  override def make_client = new ThreadSafeHttpClient(new Http.CurrentCredentials(None), maxConnections = 50, maxConnectionsPerRoute = 50) {
    override protected def configureProxy(params: HttpParams) = {
      val httpProxyPort = System.getProperty("http.proxyPort", "")
      val httpProxyHost = System.getProperty("http.proxyHost", "")

      if (httpProxyHost != "" && httpProxyPort != ""){
        logger.info("Using proxy settings for HTTP request %s:%s".format(httpProxyHost, httpProxyPort))
        ConnRouteParams.setDefaultProxy(params, new HttpHost(httpProxyHost, httpProxyPort.toInt))
      }

      params
    }
  }
}