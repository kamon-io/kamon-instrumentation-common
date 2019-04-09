package kamon
package instrumentation
package context

import kanela.agent.libs.net.bytebuddy.asm.Advice

/**
  * Advise that copies the current Context from Kamon into a HasContext instance.
  */
object CaptureCurrentContext {

  @Advice.OnMethodExit
  def exit(@Advice.This hasContext: HasContext): Unit =
    hasContext.setContext(Kamon.currentContext())
}
