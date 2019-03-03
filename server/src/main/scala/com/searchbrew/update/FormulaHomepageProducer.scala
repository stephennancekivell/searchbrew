package com.searchbrew.update

import com.searchbrew.Formula
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

object FormulaHomepageProducer {
  private val url = "https://formulae.brew.sh/api/formula.json"

  private case class FormulaDto(name: String, desc: Option[String], homepage: Option[String])

  def doit()(implicit actorSystem: ActorSystem, actorMaterializer: ActorMaterializer): Future[Seq[Formula]] = {
    import actorSystem.dispatcher
    Http().singleRequest(HttpRequest(uri = url)).flatMap { resp =>
      resp.entity.getDataBytes()

      Unmarshal(resp).to[List[FormulaDto]]
    }.map { dtos =>
      println(s"found ${dtos.length} dtos")
        dtos.map { dto =>
          Formula(
            title = dto.name,
            description = dto.desc,
            homepage = dto.homepage
          )
        }

    }
  }
}
