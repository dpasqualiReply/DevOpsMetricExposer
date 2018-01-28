package it.reply.data.pasquali.metrics.model

case class TimerMetric(label : String,
                       help : String,
                       job : String,
                       instance : String)
  extends Metric(label, help, 0, job, instance) {

  def startTimer() = {
    _value = System.currentTimeMillis()
  }

  def stopTimer() = {
    _value = System.currentTimeMillis() - _value
  }

}
