object Main {

  def main(args: Array[String]): Unit = {

    val res = "big-brother                : ok=5    changed=1    unreachable=0    failed=0"
    val pattern =
      """([A-Za-z-0-9]+)( +): ok=(\d+)( +)changed=(\d+)( +)unreachable=(\d+)( +)failed=(\d+)""".r

    val pattern(name, s1, ok, s2, changed, s3, unreachable, s4, failed) = res

    println(name)
    println(ok)
    println(changed)
    println(unreachable)
    println(failed)
  }

}
