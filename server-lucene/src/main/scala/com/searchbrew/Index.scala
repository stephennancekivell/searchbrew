package com.searchbrew

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document._
import org.apache.lucene.index._
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.sandbox.queries.FuzzyLikeThisQuery
import org.apache.lucene.search._
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version

object Index {

  val analyzer = new StandardAnalyzer(Version.LUCENE_4_9)

  val index = new RAMDirectory()

  init()

  def init() {
    val config = new IndexWriterConfig(Version.LUCENE_4_9, analyzer)
    val writer = new IndexWriter(index, config)
    writer.commit()
    writer.close()
  }

  def insertHomepages(formula: Seq[Formula]): Unit = {
    formula.foreach(insertHomepage)
  }

  def insertHomepage(formula: Formula): Unit = {
    val found = findByTitle(formula.title).getOrElse(formula)

    insert(Seq(found.copy(homepage = formula.homepage)))
  }

  def insertDescriptions(formula: Seq[Formula]): Unit = {
    formula.foreach(insertDescription)
  }

  def insertDescription(formula: Formula): Unit = {
    val found = findByTitle(formula.title).getOrElse(formula)

    insert(Seq(found.copy(description = formula.description)))
  }

  def insert(formulas: Seq[Formula]): Unit = {
    val config = new IndexWriterConfig(Version.LUCENE_4_9, analyzer)
    val writer = new IndexWriter(index, config)

    formulas.foreach {f =>
      updateDoc(f.title, formulaToDocument(f), writer)
    }

    writer.close
  }

  def updateDoc(title: String, doc: Document, writer: IndexWriter): Unit = {
    writer.updateDocument(new Term("title", title), doc)
  }

  def findByTitle(title: String): Option[Formula] = {
    val q = new QueryParser(Version.LUCENE_4_9, "title", analyzer).parse(s"""(title:$title)""")

    runQuery(q).headOption.map {
      docToFormula
    }
  }

  def docToFormula(doc: Document): Formula = {
    val title = doc.get("title")
    val h = doc.get("homepage")
    val d = doc.get("description")

    Formula(title,
      if(h == null) None else Some(h),
      if(d == null) None else Some(d)
    )
  }

  def formulaToDocument(formula: Formula): Document = {
    val doc = new Document()
    doc.add(new TextField("title", formula.title, Field.Store.YES))

    formula.homepage.map { homepage =>
      doc.add(new TextField("homepage", homepage, Field.Store.YES))
    }

    formula.description.map { description =>
      doc.add(new TextField("description", description, Field.Store.YES))

    }

    doc
  }

  def runQuery(q: Query): Seq[Document] = {
    val reader = DirectoryReader.open(index)
    val searcher = new IndexSearcher(reader)

    val collector = TopScoreDocCollector.create(10000, true)

    searcher.search(q, collector)

    collector.topDocs().scoreDocs.map { sDoc =>
      searcher.doc(sDoc.doc)
    }
  }

  def query(q: Option[String]): Seq[Formula] = {
    query(
      q.map { q =>
        new QueryParser(Version.LUCENE_4_9, "title", analyzer).
          parse(
            s"""
               | (title:$q~)^100 OR
               | (title:$q)^100 OR
               | (title:$q*)^100 OR
               | description:$q~ OR
               | description:$q OR
               | description:$q*
               | """.stripMargin)
      }.getOrElse(new MatchAllDocsQuery())
    )
  }

  def query(q: Query): Seq[Formula] = {
    runQuery(q).map(docToFormula)
  }
}
