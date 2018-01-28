package it.reply.data.pasquali.metrics.model

class CounterMetric(label : String,
                    help : String,
                    job : String,
                    instance : String)
  extends Metric(label, help, 0, job, instance) {

  def inc() = {
    _value += 1
  }

}
