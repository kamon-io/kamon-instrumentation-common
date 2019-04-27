package kamon.instrumentation.context.java;

import kamon.Kamon;
import kamon.context.Storage;
import kamon.instrumentation.context.HasContext;
import kanela.agent.libs.net.bytebuddy.asm.Advice;

/**
 * Advice that sets the Context from a HasContext instance as the current Context while the advised method is invoked.
 */
public final class InvokeWithCapturedContext {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(@Advice.This HasContext hasContext, @Advice.Local("scope") Storage.Scope scope) {
        scope = Kamon.storeContext(hasContext.context());
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void onExit(@Advice.Local("scope") Storage.Scope scope) {
        scope.close();
    }
}

