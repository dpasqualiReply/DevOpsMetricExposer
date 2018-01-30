import javax.servlet.ServletContext

import it.reply.data.pasquali.controller.Controller
import org.scalatra.LifeCycle

class ScalatraBootstrap extends LifeCycle{

  override def init(context: ServletContext) {
    context.mount(new Controller, "/*")
  }

}
