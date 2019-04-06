package kamon
package instrumentation
package context


import kamon.context.Storage
import kanela.agent.libs.net.bytebuddy.asm.Advice

/**
  * Advice that sets the Context from a HasContext instance as the current Context while a method on the instrumented
  * instance runs.
  */
object RunWithContextAdvice {

  @Advice.OnMethodEnter
  def enter(@Advice.This hasContext: HasContext): Storage.Scope =
    Kamon.storeContext(hasContext.context)

  @Advice.OnMethodExit(onThrowable = classOf[Throwable])
  def exit(@Advice.Enter scope: Storage.Scope): Unit =
    scope.close()
}
