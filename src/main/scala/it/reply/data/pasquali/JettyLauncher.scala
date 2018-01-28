package it.reply.data.pasquali

import it.reply.data.pasquali.controller.Controller
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener


object JettyLauncher { // this is my entry object as specified in sbt project definition
  def main(args: Array[String]) {
    val port = if(System.getenv("PORT") != null) System.getenv("PORT").toInt else 10001

    val server = new Server(port)
    val context = new WebAppContext()
    context.setContextPath("/")
    context.setResourceBase("/opt/DevOpsMetricExposer/src/webapp")
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[Controller], "/")

    server.setHandler(context)

    server.start
    //server.join
    scala.io.Source.fromURL(s"http://localhost:$port/").mkString
  }
}