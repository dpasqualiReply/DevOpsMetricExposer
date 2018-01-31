package it.reply.data.pasquali.metrics.model

class Metric(label : String,
             help : String,
             value : Double,
             job : String,
             instance : String) {

  var _value = value

    def getPrometheusMetrics() : String = {

      var body = s"# TYPE ${cleanDashes(label)} gauge\n"
      body += s"# HELP ${cleanDashes(label)} ${cleanDashes(help)}.\n"
      body += s"""${cleanDashes(label)}{job="${cleanDashes(job)}", instance="${cleanDashes(instance)}"} ${_value}\n"""

      body
    }

    def cleanDashes(str : String) : String = {
      str.replace("-", "_")
    }

}
