package com.searchbrew

import play.api.libs.json._

case class Formula(filename: String, title: String, homepage: String)
object Formula {
  val format = Json.format[Formula]
}

case class FormulaList(list: List[Formula])

case class FormulaHomepage(title: String, homepageText: String)

case class FormulaDescription(title: String, description: String)
case class FormulaDescriptions(list: List[FormulaDescription])
