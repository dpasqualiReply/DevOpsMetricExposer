package it.reply.data.pasquali.metrics.model

class Metric(label : String,
             help : String,
             value : Double,
             job : String,
             instance : String) {

  var _value = value

    def getPrometheusMetrics() : String = {

      var body = s"# TYPE $label gauge\n"
      body += s"# HELP $label $help.\n"
      body += s"""$label{job="$job", instance="$instance"} ${_value}\n"""

      body
    }

}
