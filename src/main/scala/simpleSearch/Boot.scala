package simpleSearch

import java.io.File
import scala.util.Try

import simpleSearch.SimpleSearch

object Boot extends App {
  val simplesearch = new SimpleSearch()

  val path = scala.io.StdIn.readLine("enter dir path: ")
  Program
   .readFile(path.trim())
   .fold(
     println,
     file => simplesearch.index(file)
   )
  while (true) {
      val query = scala.io.StdIn.readLine("enter search query (press ctrl-c to quit): ")
      simplesearch.search(query) match {
        case Nil => println("No Matches Found")
        case results => println(results)
      }
  }
}

object Program {
 import scala.io.StdIn.readLine

 def readFile(path: String): Either[ReadFileError, File] = {
   for {
     file <- Try(new java.io.File(path))
       .fold(
         throwable => Left(FileNotFound(throwable)),
         file =>
           if (file.isDirectory) Right(file)
           else Left(NotDirectory(s"Path [$path] is not a directory"))
       )
   } yield file
 }
}