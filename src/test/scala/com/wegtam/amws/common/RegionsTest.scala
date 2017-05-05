package com.wegtam.amws.common

import java.net.URI

import com.wegtam.amws.common.MarketPlaces._
import com.wegtam.amws.common.Regions._
import org.scalatest.prop.PropertyChecks
import org.scalatest.{ MustMatchers, WordSpec }

import scala.collection.immutable.Seq

class RegionsTest extends WordSpec with MustMatchers with PropertyChecks {
  private final val expectedEndpoints = Table(
    ("Region", "Endpoint"),
    (NorthAmerica, new URI("https://mws.amazonservices.com")),
    (Brazil, new URI("https://mws.amazonservices.com")),
    (Europe, new URI("https://mws-eu.amazonservices.com")),
    (India, new URI("https://mws.amazonservices.in")),
    (China, new URI("https://mws.amazonservices.com.cn")),
    (Japan, new URI("https://mws.amazonservices.jp"))
  )
  private final val expectedMarketplaces = Table(
    ("Region", "Marketplaces"),
    (NorthAmerica, Seq(CA, MX, US)),
    (Brazil, Seq(BR)),
    (Europe, Seq(DE, ES, FR, IT, UK)),
    (India, Seq(IN)),
    (China, Seq(CN)),
    (Japan, Seq(JP))
  )

  "endpoint" must {
    "return the correct region endpoint" in {
      forAll(expectedEndpoints) { (r: Region, u: URI) =>
        r.endPoint mustEqual u
      }
    }
  }

  "marketPlaces" must {
    "return the correct marketplaces for the region" in {
      forAll(expectedMarketplaces) { (r: Region, mps: Seq[MarketPlace]) =>
        r.marketPlaces mustEqual mps
      }
    }
  }

}
