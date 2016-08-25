package com.searchbrew

import org.specs2.mutable.Specification
import play.api.test._

class IndexSpec extends Specification {

  val testFormula = Formula("test title", Some("homepage"), Some("desc"))

	"Index" should {
    "find by title" in new WithApplication {
      Index.insert(Seq(testFormula))

      Index.findByTitle(testFormula.title) === Some(testFormula)
      Index.findByTitle("test") === None
      Index.query(None) === Seq(testFormula)
    }

    "updates" in new WithApplication {
      Index.insert(Seq(testFormula))

      val updated = testFormula.copy(homepage = Some("new homepage"))

      Index.insert(Seq(updated))

      Index.query(None) === Seq(updated)
    }

    "adds desc" in new WithApplication {
      Index.insert(Seq(testFormula))

      val updated = testFormula.copy(description = Some("new desc"))

      Index.insertDescription(updated)

      Index.query(None) === Seq(updated)
    }
	}
}