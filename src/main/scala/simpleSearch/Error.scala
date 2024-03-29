package simpleSearch

sealed trait Error


trait ReadFileError extends Error
case object MissingPathArg extends ReadFileError
case class NotDirectory(error: String) extends ReadFileError
case class FileNotFound(t: Throwable) extends ReadFileError