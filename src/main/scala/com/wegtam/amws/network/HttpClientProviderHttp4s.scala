/*
 * Copyright (c) 2017 Contributors as noted in the AUTHORS.md file
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.wegtam.amws.network

import java.net.URI

import cats.effect._
import org.http4s._
import org.http4s.client._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._

class HttpClientProviderHttp4s(implicit httpClient: Client[IO]) extends HttpClientProvider[IO] {

  /**
    * Execute a GET http request on the given url using the provided query (data).
    *
    * @param url     A url which is used for the request.
    * @param payload The payload for the request.
    * @return The response of the query.
    */
  override def get(url: URI)(payload: AmwsRequestPayload): IO[AmwsResponse] = {
    val getRequest = GET(payload.data.getOrElse(""), Uri.unsafeFromString(url.toString))
    httpClient.expect[String](getRequest).map(AmwsResponse)
  }

  /**
    * Execute a POST http request on the given url using the provided query (data).
    *
    * @param url     A url which is used for the request.
    * @param payload The payload for the request.
    * @return The response of the query.
    */
  override def post(url: URI)(payload: AmwsRequestPayload): IO[AmwsResponse] = {
    // IO[A] => IO[Either[E, A]] / EitherT[IO, E, A] ???
    val postRequest = POST(payload.data.getOrElse(""), Uri.unsafeFromString(url.toString))
    httpClient.expect[String](postRequest).map(AmwsResponse)
  }

}
