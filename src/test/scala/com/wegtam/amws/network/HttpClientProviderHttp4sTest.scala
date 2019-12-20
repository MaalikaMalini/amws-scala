/*
 * Copyright (c) 2017 Contributors as noted in the AUTHORS.md file
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.wegtam.amws.network

import java.net.{ ServerSocket, URI }

import cats.effect._
import org.http4s._
import org.http4s.client._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._
import org.http4s.server.blaze._
import akka.http.scaladsl.server.{ Directives, Route }
import org.scalatest.{ AsyncWordSpec, BeforeAndAfterAll, MustMatchers }

import scala.concurrent.Await
import scala.concurrent.duration._

class HttpClientProviderHttp4sTest extends AsyncWordSpec with MustMatchers with BeforeAndAfterAll {

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
