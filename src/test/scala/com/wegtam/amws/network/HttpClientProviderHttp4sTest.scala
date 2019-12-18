package com.wegtam.amws.network

import java.net.{ServerSocket, URI}

import cats.effect._
import org.http4s._
import org.http4s.client._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, MustMatchers}

import scala.concurrent.Await
import scala.concurrent.duration._

class HttpClientProviderHttp4sTest
  extends AsyncWordSpec
  with MustMatchers
  with BeforeAndAfterAll {

  /**
    * Start a server socket and close it. The port number used by
    * the socket is considered free and returned.
    *
    * @param reuseAddress If set to `false` the returned port will not be useable for some time.
    * @return A port number.
    */
  private def findAvailablePort(reuseAddress: Boolean): Int = {
    val serverSocket = new ServerSocket(0)
    val freePort     = serverSocket.getLocalPort
    serverSocket.setReuseAddress(reuseAddress)
    serverSocket.close()
    freePort
  }


}
