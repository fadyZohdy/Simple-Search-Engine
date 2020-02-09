package simpleSearch

import simpleSearch.SimpleSearch._
import scala.util.Try
import java.io.File
import java.util.UUID
import scala.io.Source

class SimpleSearch {

    private val invertedIndex = scala.collection.mutable.Map.empty[String, Frequency]
    private var documents = List.empty[Document]

    def tokenize(str: String): List[String] = str.split(" ").toList

    def indexToken(token: String, doc: Document): Unit = 
        invertedIndex.get(token) match {
            case Some(freq) => 
                freq.docsFrequency.indexWhere(_.doc.id == doc.id) match {
                    case -1 => 
                        val newFreq = freq.copy(
                            frequency = freq.frequency + 1,
                            docsFrequency = freq.docsFrequency :+ DocumentFrequency(doc, 1)
                        )
                        invertedIndex.update(token, newFreq)
                    case idx => 
                        val oldDocFreq = freq.docsFrequency(idx)
                        val newFreq = freq.copy(
                            frequency = freq.frequency + 1,
                            docsFrequency = freq.docsFrequency.updated(
                                idx, oldDocFreq.copy(frequency = oldDocFreq.frequency + 1)
                            )
                        ) 
                        invertedIndex.update(token, newFreq)
                }
            case None => invertedIndex += (token -> Frequency(1, List(DocumentFrequency(doc, 1))))
        }

    def index(dir: File): Unit = 
        for (file <- dir.listFiles()) {
            val doc = Document(UUID.randomUUID(), file.getName())
            documents = documents :+ doc
            for (line <- Source.fromFile(file).getLines()) {
                for (token <- tokenize(line)) {
                    if (token.length() > 0) {
                       indexToken(token, doc) 
                    }
                }
            }
        }

    def search(query: String): List[SearchResult] = {
        val tokens = tokenize(query)
        documents.map(doc => {
            val score = tokens.foldLeft(0)((z, token) => {
                invertedIndex.get(token) match {
                    case None => z
                    case Some(freq) => 
                        if(freq.docsFrequency.find(_.doc.id == doc.id).isDefined) z + 1 else z
                }
            }) 
            SearchResult(doc.name, (score.toFloat / tokens.length.toFloat) * 100)
        })
    } 
}

object SimpleSearch {
    case class Document(id: UUID, name: String)
    case class DocumentFrequency(doc: Document, frequency: Int) {
        override def toString = s"${doc.name} -> ${frequency}"
    }
    case class Frequency(frequency: Int, docsFrequency: List[DocumentFrequency])
    case class SearchResult(doc_name: String, score: Float) {
        override def toString = s"${doc_name} -> ${score.round} % "
    }
}