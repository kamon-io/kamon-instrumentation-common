package kamon.instrumentation.http

import kamon.instrumentation.http.HttpMessage.Request

/**
  * Generates an operation name based on information available on an HTTP request message. Implementations of this
  * class might be used to generate client and/or server side operation names.
  */
trait HttpOperationNameGenerator {

  def name(request: Request): Option[String]

}

object HttpOperationNameGenerator {

  class Hostname extends HttpOperationNameGenerator {
    override def name(request: Request): Option[String] =
      Option(request.host)
  }
}