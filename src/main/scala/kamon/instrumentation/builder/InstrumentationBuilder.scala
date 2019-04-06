package kamon
package instrumentation
package builder

import java.util.function.Supplier
import java.util.logging.{Level, Logger}

import kanela.agent.api.instrumentation.{InstrumentationDescription, KanelaInstrumentation}
import kanela.agent.libs.io.vavr.Function1
import kanela.agent.libs.net.bytebuddy.description.`type`.TypeDescription
import kanela.agent.libs.net.bytebuddy.description.method.MethodDescription
import kanela.agent.libs.net.bytebuddy.matcher.ElementMatcher.Junction
import kanela.agent.libs.net.bytebuddy.matcher.{ElementMatcher, ElementMatchers => BBMatchers}

import scala.collection.immutable.Seq

trait InstrumentationBuilder extends KanelaInstrumentation {
  import InstrumentationDescription.Builder

  private val _logger = Logger.getLogger(classOf[InstrumentationBuilder].getName)
  private var _currentBuilder: Option[Builder] = None


  /**
    * Creates a Scope on which all transformations registered via the advise, mixin, bridge and intercept functions
    * are applied to a type exactly matching the provided type name.
    */
  def onType(typeName: String)(actions: => Unit): Unit =
    super.forTargetType(toSupplier(typeName), toJavaFunction1(b => withBuilder(b)(actions)))


  /**
    * Creates a Scope on which all transformations registered via the advise, mixin, bridge and intercept functions
    * are applied to a type exactly matching the provided type name.
    */
  def onType(typeNames: Seq[String])(actions: => Unit): Unit =
    onTypesMatching(anyTypes(typeNames: _*))(actions)


  /**
    * Creates a Scope on which all transformations registered via the advise, mixin, bridge and intercept functions
    * are applied any types satisfying the provided matcher.
    */
  def onTypesMatching(matcher: ElementMatcher[_ >: TypeDescription])(actions: => Unit): Unit =
    super.forRawMatching(toSupplier(matcher), toJavaFunction1(b => withBuilder(b)(actions)))


  /**
    * Creates a Scope on which all transformations registered via the advise, mixin, bridge and intercept functions
    * are applied to any sub type of the provided type.
    */
  def onSubTypesOf(typeName: String)(actions: => Unit): Unit =
    super.forSubtypeOf(toSupplier(typeName), toJavaFunction1(b => withBuilder(b)(actions)))


  /**
    * Creates a Scope on which all transformations registered via the advise, mixin, bridge and intercept functions
    * are applied to any sub type of the provided types.
    */
  def onSubTypesOf(typeNames: Seq[String])(actions: => Unit): Unit =
    onTypesMatching(BBMatchers.hasSuperType(anyTypes(typeNames: _*)))(actions)


  /**
    * Applies a mixin to the type(s) on Scope.
    */
  def mixin(implementation: Class[_]): Unit = {
    _currentBuilder.fold {
      _logger.log(Level.WARNING, s"Could not apply mixin [${implementation.getName}] because there was no type on Scope")
    } { builder =>
      builder.withMixin(toSupplier(implementation))
    }
  }


  /**
    * Applies an advice to the provided method(s). The advice implementation is expected to have a static method
    * annotated with @Advice.OnMethodEnter and/or a static method annotated with @Advice.OnMethodExit.
    */
  def advise(method: Junction[MethodDescription], advice: Class[_]): Unit = {
    _currentBuilder.fold {
      _logger.log(Level.WARNING, s"Could not apply advise [${advice.getName}] because there was no type on Scope")
    } { builder =>
      builder.withAdvisorFor(method, toSupplier(advice))
    }
  }

  /**
    * Applies an advice to the provided method(s). The advice implementation is expected to be a companion object with
    * a method annotated with @Advice.OnMethodEnter and/or a method annotated with @Advice.OnMethodExit. Note that the
    * underlying implementation is expecting a class with static methods, which can only be provided by plain companion
    * objects; any nested companion object will not be appropriately picked up.
    */
  def advise[A](method: Junction[MethodDescription], advice: A)(implicit singletonEvidence: A <:< Singleton): Unit = {
    // Companion object instances always have the '$' sign at the end of their class name, we must remove it to get
    // to the class that exposes the static methods.
    val className = advice.getClass.getName.dropRight(1)
    val adviseClass = Class.forName(className, true, advice.getClass.getClassLoader)

    advise(method, adviseClass)
  }


  /** Takes and builder and sets it as the receiver of actions on Scope */
  private def withBuilder(builder: Builder)(actions: => Unit): InstrumentationDescription = {
    _currentBuilder = Some(builder)
    actions
    _currentBuilder = None
    builder.build()
  }



  private def toSupplier[T](supplier: => T): Supplier[T] = new Supplier[T]() {
    override def get(): T = supplier
  }

  private def toJavaFunction1[A, B](f: (A) ⇒ B): Function1[A, B] = new Function1[A, B]() {
    def apply(t1: A): B = f(t1)
  }
}

object InstrumentationBuilder {

  /** Allows starting the "typeA" or "typeB" syntax */
  implicit class OrSyntax(left: String) {
    def or(right: String): Seq[String] = Seq(left, right)
  }

  /** Allows expanding the "typeA" or "typeB" syntax beyond one pair */
  implicit class MultipleOrSyntax(names: Seq[String]) {
    def or(name: String): Seq[String] = names ++ Seq(name)
  }
}


