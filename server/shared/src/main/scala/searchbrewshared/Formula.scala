package searchbrewshared

case class Formula(title: String, homepage: Option[String] = None, description: Option[String] = None)
case class SearchResult(query: Option[String], data: Seq[Formula])

object FormulaPickle {
  import upickle._


  def w(sr: SearchResult): String = {
    upickle.write(sr)
  }

  def r(str: String): SearchResult = {
    upickle.read[SearchResult](str)
  }
}