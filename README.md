Simple In-Memory Search Engine

### Running the app
the program is a simple command line app. to run it 

```sh
sbt run
```

you will be asked to provide a path to a directory of text files which the app will use to read all contained files and index their content in an in-memory data structure.

you will be then asked to enter a search query repeatedly until existing using `ctrl-c`.

the data structure used is a scala `Map` which is used to resemble an inverted index where tokens are the keys and the value is a case class

```scala
case class Frequency(frequency: Int, docsFrequency: List[DocumentFrequency])
```
which is the how many times this token has appeared across all the documents and a list of `DocumentFrequency`.

```scala
case class DocumentFrequency(doc: Document, frequency: Int)
```
which is a document where this token appeared and how many times in this specific document.

these frequencies can be used to calculate the tf/idf score if needed. this was not implemented as the scoring system required was just a 0-100 % scale scoring system.