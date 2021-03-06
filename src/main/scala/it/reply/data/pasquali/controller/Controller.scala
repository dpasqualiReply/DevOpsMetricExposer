package it.reply.data.pasquali.controller

import it.reply.data.pasquali.engine.{AnsibleConnector, ClouderaConnector}
import it.reply.data.pasquali.metrics.RecMetricsCollector
import it.reply.data.pasquali.metrics.model.Metric
import org.scalatra.scalate.ScalateSupport
import org.scalatra.{FlashMapSupport, ScalatraServlet}
import org.slf4j.LoggerFactory


class Controller extends ScalatraServlet with FlashMapSupport with ScalateSupport{

  var cloudera : ClouderaConnector = null
  var ansible : AnsibleConnector = null

  val logger = LoggerFactory.getLogger(getClass)

  def initConnectors() : Unit = {

    logger.info(" ******************* Init Connectors")

    cloudera = ClouderaConnector("35.229.22.3", "7051")
    cloudera.init()

    logger.info(" ******************* Init Cloudera")

    ansible = AnsibleConnector(
      "/opt/DevOpsProduction-Orchestrator/ansible/s",
      "/home/xxpasquxx/.ssh/ansible_rsa_key",
      "xxpasquxx"
    )

    logger.info(" ******************* ")
  }

  get("/") {
    if(cloudera == null)
      initConnectors()

    logger.info(" ******************* HOME")

    "This webapp only collect and provides metrics for DevOps echosystem\n"+
    "The collection process is periodically run in order to provide fresh values\n\n"+
    "Use /metrics to get actual metrics\n"+
    "Use /metrics/fresh to get refreshed metrics"


  }

  // *********************** MACHINE STATUS *******************

  get("/isonline") {

    logger.info(" ******************* IS ONLINE")

    "/isonline/big-brother\n" +
      "/isonline/cloudera-vm\n" +
      "/isonline/devops-worker"
  }

  get("/isonline/:machine") {

    val vm = params.getOrElse("machine", "0.0.0.0")

    logger.info(s" ******************* IS ONLINE ${vm}")

    s"$vm is online? ${if(collectMachineStatus(vm)) "yes" else "no"}"
  }

  // *********************** DATABASE STATE *******************

  get("/count/hive/:database/:table") {

    /*
    Numero movies hive
    Numero links hive
    Numero tags hive
    Numero ratings hive
    Numero gtags hive
     */

    val db = params.getOrElse("database", "default")
    val table = params.getOrElse("table", "test")

    logger.info(s" ******************* Count $db.$table")

    val count = collectCountsHive(db, table)

    s"Found $count elements in $db.$table"
  }

  get("/count/kudu/:database/:table") {

    /*
    Numero movies kudu
    Numero tags kudu
    Numero ratings kudu
    Numero gtags kudu
     */

    val db = params.getOrElse("database", "default")
    val table = params.getOrElse("table", "test")

    logger.info(s" ******************* Count $db.$table")

    val count = collectCountsKudu(db, table)

    s"Found $count elements in $db.$table"
  }


  // *********************** SERVICE STATUS ********************

  get("/service/status") {

    logger.info(" ******************* SERVICE STATUS")

    var body = "/service/status/cloudera-vm/cloudera-scm-server\n"
    body += "/service/status/cloudera-vm/cloudera-scm-agent\n"
    body += "\n"
    body += "/service/status/big-brother/jenkins\n"
    body += "/service/status/big-brother/prometheus\n"
    body += "/service/status/big-brother/node_exporter\n"
    body += "/service/status/big-brother/grafana-server\n"
    body += "/service/status/big-brother/pushgateway\n"
    body += "\n"
    body += "/service/status/devops-worker/node_exporter\n"
    body += "/service/status/devops-worker/devops-kafka-jdbc-connector\n"
    body += "/service/status/devops-worker/devops-rtetl-ratings\n"
    body += "/service/status/devops-worker/devops-rtetl-tags\n"
    body += "/service/status/devops-worker/devops-rtml\n"
    body
  }

  get("/service/status/:machine/:service") {
    val vm = params.getOrElse("machine", "0.0.0.0")
    val service = params.getOrElse("service", "")
    val running = collectServiceStatus(vm, service)

    s"$service on $vm state : ${if(running) "running" else "stop :("}"
  }

  // ********************** EXPOSE METRICS *************************

  get("/metrics/fresh") {
    if(cloudera == null)
      initConnectors()

    logger.info(" ******************* REFRESH METRICS ")

    // MACHINE STATUS
    val cvm = collectMachineStatus("cloudera-vm")
    val dw = collectMachineStatus("devops-worker")

    // CLOUDERA WM
    if(cvm){

      // DB CONTENT
      collectCountsHive("datalake", "movies")
      collectCountsHive("datalake", "links")
      collectCountsHive("datalake", "ratings")
      collectCountsHive("datalake", "tags")

      collectCountsKudu("datamart", "movies")
      collectCountsKudu("datamart", "ratings")
      collectCountsKudu("datamart", "tags")

      // SERVICES
      collectServiceStatus("cloudera-vm", "cloudera-scm-server")
      collectServiceStatus("cloudera-vm", "cloudera-scm-agent")
    }
    else println("[ ERROR ] Cloudera VM is offline!!")

    // DEVOPS WORKER
    if(dw){
      collectServiceStatus("devops-worker", "node_exporter")
      collectServiceStatus("devops-worker", "devops-kafka-jdbc-connector")
      collectServiceStatus("devops-worker", "devops-rtetl-ratings")
      collectServiceStatus("devops-worker", "devops-rtetl-tags")
      collectServiceStatus("devops-worker", "devops-rtml")
    }
    else println("[ ERROR ] DevOps worker is offline!!")

    // BIG-BROTHER
    //This will run on it, i'll assume is online
    collectServiceStatus("big-brother", "node_exporter")
    collectServiceStatus("big-brother", "prometheus")
    collectServiceStatus("big-brother", "pushgateway")
    collectServiceStatus("big-brother", "grafana-server")
    collectServiceStatus("big-brother", "jenkins")

    RecMetricsCollector.getPrometheusMetrics()
  }

  get("/metrics") {
    if(cloudera == null)
      initConnectors()

    logger.info(" ******************* GET METRICS")

    RecMetricsCollector.getPrometheusMetrics()
  }

  // ********************** COLLECTORS *****************************

  def collectMachineStatus(vm : String) : Boolean = {
    val online = ansible.pingMachinePy(vm)

    RecMetricsCollector.addMetric(
      s"is_online_${vm}",
      new Metric(s"is_online_${vm}", s"1 if $vm is online, 0 otherwise",
        if(online) 1 else 0, "devops_exporter", ""))

    logger.info(s" ******************* Metrics update, now ${RecMetricsCollector.metrics.size}")
    online
  }

  def collectCountsHive(db : String, table : String) : Long = {

    val count = cloudera.countHive(db, table)

    RecMetricsCollector.addMetric(
      s"hive_${table}_number",
      new Metric(s"hive_${table}_number", s"number of ${table} in the ${db}",
        count, "devops_exporter", ""))

    logger.info(s" ******************* Metrics update, now ${RecMetricsCollector.metrics.size}")
    count
  }

  def collectCountsKudu(db : String, table : String) : Long = {

    val count = cloudera.countKudu(db, table)

    RecMetricsCollector.addMetric(
      s"kudu_${table}_number",
      new Metric(s"kudu_${table}_number", s"number of ${table} in the ${db}",
        count, "devops_exporter", ""))

    logger.info(s" ******************* Metrics update, now ${RecMetricsCollector.metrics.size}")
    count
  }

  def collectServiceStatus(vm: String, service: String) : Boolean = {
    val running = ansible.checkServiceRunningPy(vm, service)

    RecMetricsCollector.addMetric(
      s"service_status_$service",
      new Metric(s"is_online_${vm}_$service", s"1 if $vm.$service is running, 0 otherwise",
        if(running) 1 else 0, "devops_exporter", vm))

    logger.info(s" ******************* Metrics update, now ${RecMetricsCollector.metrics.size}")
    running
  }
}

