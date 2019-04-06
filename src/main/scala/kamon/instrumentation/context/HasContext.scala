package kamon
package instrumentation
package context

import kamon.context.Context
import kanela.agent.api.instrumentation.mixin.Initializer

/**
  * Mixin that exposes access to a Context instance captured by an instrumented instance. The interface exposes means of
  * getting and updating a Context member, but it does not prescribe any ordering or thread safety guarantees, please
  * refer to the available mixins for more details.
  */
trait HasContext {

  /**
    * Returns the context instance mixed into the instrumented instance.
    */
  def context: Context

  /**
    * Updates the context instance reference mixed into the instrumented instance
    */
  def setContext(context: Context): Unit

}

object HasContext {

  /**
    * HasContext implementation that keeps the Context reference in a mutable variable.
    */
  class Mixin extends HasContext {

    // NOTE: It doesn't really matter if we initialize this member here because the initialization code is not copied
    //       to the instrumented classes' constructor. The only way to ensure that a value is assigned to this member is
    //       to use the HasContextMixin.WithCurrentContextInitializer variant or to apply additional instrumentation that
    //       will assign the right Context instance using the setContext method.
    private var _context: Context = Context.Empty

    override def context: Context =
      if(_context != null) _context else Context.Empty

    override def setContext(context: Context): Unit =
      _context = context
  }


  /**
    * HasContext implementation that that keeps the ContextReference in a mutable variable and initializes it with the
    * current Context held by Kamon.
    */
  class MixinWithInitializer extends HasContext {
    private var _context: Context = Context.Empty

    override def context: Context =
      _context

    override def setContext(context: Context): Unit =
      _context = context

    @Initializer
    def initialize(): Unit =
      setContext(Kamon.currentContext())
  }

}