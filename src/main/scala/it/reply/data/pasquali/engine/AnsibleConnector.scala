package it.reply.data.pasquali.engine

import it.reply.data.pasquali.model.AnsibleResult
import org.slf4j.LoggerFactory

import scala.sys.process._

case class AnsibleConnector(ansibleHome : String,
                            SSHKeyFile : String,
                            ansibleSSHUser : String) {


  val logger = LoggerFactory.getLogger(getClass)

  def pingMachine(machineAddress : String) : Boolean = {

    logger.info(" .......................... ANSIBLE PING")

    val query = s"""ansible -i '$machineAddress,' all
                   |--private-key=$SSHKeyFile
                   |-e 'ansible_ssh_user=$ansibleSSHUser'
                   |-e 'host_key_checking=False'
                   |-m ping""".stripMargin

    logger.info(s" .......................... ANSIBLE COMMAND $query")

    var res =
      s"""ansible -i '$machineAddress,' all
        |--private-key=$SSHKeyFile
        |-e 'ansible_ssh_user=$ansibleSSHUser'
        |-e 'host_key_checking=False'
        |-m ping""".stripMargin !!

    res.contains(machineAddress) && res.contains("SUCCESS") && res.contains(""""ping": "pong"""")
  }

  def checkServiceRunning(machineAddress : String, service : String) : Boolean = {

    logger.info(" .......................... ANSIBLE CHECK SERVICE")

    val query = s"""ansible-playbook -i '$machineAddress,' all
                   |--private-key=$SSHKeyFile
                   |ansible/test-service.yml
                   |-e 'ansible_ssh_user=$ansibleSSHUser'
                   |-e 'host_key_checking=False'
                   |--extra-vars "service_pretty=$service service=$service"
                   || tail -n 2
                  """.stripMargin

    logger.info(s" .......................... ANSIBLE COMMAND $query")

    var res = s"""ansible-playbook -i '$machineAddress,' all
             |--private-key=$SSHKeyFile
             |ansible/test-service.yml
             |-e 'ansible_ssh_user=$ansibleSSHUser'
             |-e 'host_key_checking=False'
             |--extra-vars "service_pretty=$service service=$service"
             || tail -n 2
             """.stripMargin !!

    val ar = getAnsibleRunResult(res)

    ar.name.equals(machineAddress) &&
      (ar.ok >= 1) &&
      (ar.changed >= 0) &&
      (ar.unreachable == 0) &&
      (ar.failed == 0)
  }


  def getAnsibleRunResult(lastLine : String) : AnsibleResult = {
    val pattern =
      """([A-Za-z-0-9]+)( +): ok=(\d+)( +)changed=(\d+)( +)unreachable=(\d+)( +)failed=(\d+)""".r
    val pattern(name, s1, ok, s2, changed, s3, unreachable, s4, failed) = lastLine
    AnsibleResult(name, ok.toInt, changed.toInt, unreachable.toInt, failed.toInt)
  }



}
