package it.reply.data.pasquali.engine

import it.reply.data.pasquali.Storage

case class ClouderaConnector(clouderaAddress : String, kuduPort : String) {

  var storage : Storage = null

  def init() = {

    storage = Storage()
      .init(master = "local", appName = "MetricCollector", withHive = true)
      .initKudu(master = "cloudera-vm", port = "7051", baseTable = "impala::")
  }

  def countKudu(database : String, table : String) : Long = {
    storage.readKuduTable(s"$database.$table").count()
  }

  def countHive(database : String, table : String) : Long = {
   storage.readHiveTable(s"$database.$table").count()
  }

}
