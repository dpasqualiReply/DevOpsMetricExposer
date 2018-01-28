package it.reply.data.pasquali.metrics

import it.reply.data.pasquali.metrics.model.{CounterMetric, Metric, TimerMetric}
import org.slf4j.LoggerFactory

import scala.collection.mutable

object RecMetricsCollector {

  val metrics : mutable.HashMap[String, Metric] =
    mutable.HashMap[String, Metric]()

  val logger = LoggerFactory.getLogger(getClass)

  def addMetric(label : String, metric: Metric) = {
    metrics.put(label, metric)
  }

  def dropMetric(label : String) = {
    metrics.remove(label)
  }

  def getPrometheusMetrics() : String = {

    var body = ""

    for(el <- metrics.values)
    {
      body += el.getPrometheusMetrics()
      body += "\n"
    }
    body
  }

  def set(label : String, value : Double) = {

    try{
      metrics(label)._value = value
    }catch{
      case _ : Exception=> logger.warn(s"Unable to find label $label")
    }

  }

  def startTimer(label : String) = {

    try{
      metrics(label).asInstanceOf[TimerMetric].startTimer()
    }catch{
      case _ : Exception => logger.warn(s"Unable to find label $label")
    }

  }

  def stopTimer(label : String) = {

    try{
      metrics(label).asInstanceOf[TimerMetric].stopTimer()
    }catch{
      case _ : Exception => logger.warn(s"Unable to find label $label")
    }

  }

  def inc(label : String) = {

    try{
      metrics(label).asInstanceOf[CounterMetric].inc()
    }catch{
      case _ : Exception => logger.warn(s"Unable to find label $label")
    }

  }

}
