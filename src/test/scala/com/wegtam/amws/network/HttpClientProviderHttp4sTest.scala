/*
 * Copyright (c) 2017 Contributors as noted in the AUTHORS.md file
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.wegtam.amws.network

import java.net.{ ServerSocket, URI }
import java.util.concurrent.Executors
import cats.effect._
import org.scalatest.{ AsyncWordSpec, BeforeAndAfterAll, MustMatchers }
import scala.concurrent.ExecutionContext
import org.http4s.client._

class HttpClientProviderHttp4sTest extends AsyncWordSpec with MustMatchers with BeforeAndAfterAll {

  /**
    * Start a server socket and close it. The port number used by
    * the socket is considered free and returned.
    *
    * @param reuseAddress If set to `false` the returned port will not be useable for some time.
    * @return A port number.
    */
  protected def findAvailablePort(reuseAddress: Boolean): Int = {
    val serverSocket = new ServerSocket(0)
    val freePort     = serverSocket.getLocalPort
    serverSocket.setReuseAddress(reuseAddress)
    serverSocket.close()
    freePort
  }

  "HttpClientProvider" when {
    "using Http4s" when {
      "using get" when {
        "endpoint is not found" must {
          "return the correct response data" in {
            val port = findAvailablePort(true)
//            val routes = HttpRoutes.of[IO] {
//              case r => Ok(r.headers.get(Host).map(_.value).getOrElse("None"))
//            }
//            val route: HttpRoutes[IO] = Router{
//            case GET =>
//            }
//
//            val _ = Http().andThen(route, "localhost", port)
            val uri = new URI(s"http://localhost:$port/get-me-wrong")
            val pld = AmwsRequestPayload(data = None)

            val blockingEC   = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))
            implicit val bar = scala.concurrent.ExecutionContext.Implicits.global
            implicit val foo = cats.effect.IO.contextShift(bar)
            implicit val httpClient: Client[IO] =
              JavaNetClientBuilder[IO](blockingEC).create

            val client = new HttpClientProviderHttp4s()
            val a      = client.get(uri)(pld)
            println(a)
            val b = a.unsafeRunSync()
            println(b.body)
            b.body must be("foo")
          }
        }



//        "endpoint is valid" must {
//          "return the correct response data" in {
//            val port  =findAvailablePort(true)
//            val expectedResponse = "I am the reponse!"
//            val route: HttpRoutes[IO] = Router{
//              case GET -> Root / "get-me" => "you should reach me"
//            }
//
//            val _ = Http().andThen(route, "localhost", port)
//
//            val uri = new URI(s"http://localhost:$port/get-me")
//            val pld = AmwsRequestPayload(data = None)
//
//            val client = new HttpClientProviderHttp4s()
//            client.get(uri)(pld).map{
//              case Left(e) => fail(s"Http request returned an error: $e")
//              case Right(d) => d.b
//            }
//
//          }
//        }

      }
    }
  }
}
