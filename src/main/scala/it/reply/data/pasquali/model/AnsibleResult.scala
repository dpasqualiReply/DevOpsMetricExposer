package it.reply.data.pasquali.model

case class AnsibleResult(name : String,
                    ok : Int,
                    changed : Int,
                    unreachable : Int,
                    failed : Int) {

}
