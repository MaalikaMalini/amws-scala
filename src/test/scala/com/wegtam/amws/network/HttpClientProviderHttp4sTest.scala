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
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._
import cats.effect._
import org.http4s.HttpRoutes
import org.scalatest.{ AsyncWordSpec, BeforeAndAfterAll, MustMatchers }

import scala.concurrent.ExecutionContext
import org.http4s.client._
import org.http4s.server.blaze.BlazeServerBuilder
import scala.concurrent.ExecutionContext.global

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
          "return an error" in {
            implicit val cs: ContextShift[IO] = IO.contextShift(global)
            implicit val timer: Timer[IO]     = IO.timer(global)
            val port                          = findAvailablePort(true)
            val route = HttpRoutes
              .of[IO] {
                case POST -> Root / "hello" / name =>
                  Ok(s"Hello, $name.")
//                case x =>
//                  Ok(s"FOO")
              }
              .orNotFound

            val server =
              BlazeServerBuilder[IO].bindHttp(port, "localhost").withHttpApp(route).resource
            val fiber = server.use(_ => IO.never).start.unsafeRunSync()

            val uri = new URI(s"http://localhost:$port/hello2/maali")
            val pld = AmwsRequestPayload(data = None)

            val blockingEC = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))
            implicit val httpClient: Client[IO] =
              JavaNetClientBuilder[IO](blockingEC).create

            val client = new HttpClientProviderHttp4s()
            val a      = client.get(uri)(pld)
            val b      = a.attempt.unsafeRunSync()
            val _      = fiber.cancel.unsafeRunSync()
            b match {
              case Left(e)  => e.getMessage must include("404 Not Found")
              case Right(r) => fail(s"missing end point - $r")
            }
          }
        }
        "endpoint is valid" must {
          "return the correct response data" in {
            implicit val cs: ContextShift[IO] = IO.contextShift(global)
            implicit val timer: Timer[IO]     = IO.timer(global)
            val expectedResponse              = "I am the response"
            val port                          = findAvailablePort(true)
            val route = HttpRoutes
              .of[IO] {
                case POST -> Root / "get-me" =>
                  Ok(expectedResponse)
                case x =>
                  Ok(s"$x")
              }
              .orNotFound

            val server =
              BlazeServerBuilder[IO].bindHttp(port, "localhost").withHttpApp(route).resource
            val fiber = server.use(_ => IO.never).start.unsafeRunSync()

            val uri = new URI(s"http://localhost:$port/get-me")
            val pld = AmwsRequestPayload(data = None)

            val blockingEC = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))
            implicit val httpClient: Client[IO] =
              JavaNetClientBuilder[IO](blockingEC).create

            val client                             = new HttpClientProviderHttp4s()
            val a: IO[AmwsResponse]                = client.get(uri)(pld)
            val b: Either[Throwable, AmwsResponse] = a.attempt.unsafeRunSync()
            val _                                  = fiber.cancel.unsafeRunSync()
            b match {
              case Left(e)  => fail(s"Http request returned an error: $e")
              case Right(d) => d.body must be(expectedResponse)
            }
          }
        }
      }
    }
  }
}
