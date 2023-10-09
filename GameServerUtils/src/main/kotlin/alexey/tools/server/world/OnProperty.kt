package alexey.tools.server.world

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnProperty(val name: String = "")
