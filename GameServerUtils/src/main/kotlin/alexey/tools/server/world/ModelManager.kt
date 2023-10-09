package alexey.tools.server.world

import alexey.tools.common.collections.ObjectList
import alexey.tools.common.context.ImmutableVariable
import alexey.tools.common.misc.Injector
import alexey.tools.common.misc.decapitalize
import alexey.tools.server.misc.getOrPut
import alexey.tools.server.models.EntityModel
import com.artemis.Component
import com.artemis.ComponentMapper
import com.artemis.ComponentType
import com.artemis.ComponentTypeFactory.ComponentTypeListener
import com.artemis.utils.Bag
import com.badlogic.gdx.utils.IdentityMap
import com.badlogic.gdx.utils.ObjectMap
import java.lang.reflect.Field
import java.lang.reflect.Method

class ModelManager(private val autoRegister: Boolean = true): DisabledSystem(), Injector {

    private val propertyFactories = ObjectMap<String, (EntityModel, Int, ImmutableVariable) -> Unit>()
    private val entityFactories = ObjectList<EntityFactory>()
    private val converters = IdentityMap<Class<*>, (EntityModel, Int, ImmutableVariable) -> Any>()



    override fun initialize() {
        setConverter(Float::class.java) { _, _, v -> v.toFloat() }
        setConverter(Double::class.java) { _, _, v -> v.toDouble() }

        setConverter(Int::class.java) { _, _, v -> v.toInt() }
        setConverter(Short::class.java) { _, _, v -> v.toShort() }
        setConverter(Long::class.java) { _, _, v -> v.toLong() }
        setConverter(Byte::class.java) { _, _, v -> v.toByte() }

        setConverter(Boolean::class.java) { _, _, v -> v.toBoolean() }
        setConverter(String::class.java) { _, _, v -> v.toString() }

        if (!autoRegister) return
        world.systems.forEach { inject(it) }
        world.componentManager.typeFactory.register(Listener())
    }

    override fun inject(target: Any) {
        var type = target.javaClass
        while (type !== Any::class.java) {
            for (method in target.javaClass.declaredMethods) {
                registerOnProperty(target, method)
                registerOnModel(target, method)
            }
            type = type.superclass
        }
    }



    fun createEntity(model: EntityModel, entityId: Int = world.create()) {
        entityFactories.forEach { it.accept(model, entityId) }
        for (entry in model.properties.asMap())
            (propertyFactories.get(entry.key) ?: continue).invoke(model, entityId, entry.value)
    }

    fun <T: Any> setConverter(type: Class<T>, converter: (EntityModel, Int, ImmutableVariable) -> T) {
        converters.put(type, converter)
    }

    fun <T: Any> setConverter(type: Class<T>, converter: (ImmutableVariable) -> Any) {
        converters.put(type) { _, _, v -> converter(v) }
    }

    fun setOnProperty(name: String, factory: (EntityModel, Int, ImmutableVariable) -> Unit) {
        propertyFactories.put(name, factory)
    }

    fun addOnModel(factory: EntityFactory) {
        entityFactories.add(factory)
    }

    fun <T: Component> setOnPropertyCreated(name: String, type: Class<T>, factory: (EntityModel, T, ImmutableVariable) -> Unit) {
        propertyFactories.put(name, MapperFactory(factory, world.getMapper(type)))
    }

    fun register(type: Class<out Component>) {
        val mapper = world.getMapper(type)
        for (field in type.declaredFields) {
            if (propertyFactories.containsKey(field.name)) continue
            val converter = converters.get(field.type) ?: continue
            field.isAccessible = true
            propertyFactories.put(field.name, FieldFactory(field, mapper, converter))
        }
        propertyFactories.getOrPut(type.simpleName.decapitalize()) { BooleanFactory(mapper) }
    }

    inline fun <reified T: Any> setConverter(noinline converter: (EntityModel, Int, ImmutableVariable) -> T) {
        setConverter(T::class.java, converter)
    }

    inline fun <reified T: Component> setOnPropertyCreated(name: String, noinline factory: (EntityModel, T, ImmutableVariable) -> Unit) {
        setOnPropertyCreated(name, T::class.java, factory)
    }



    @Suppress("unchecked_cast")
    private fun registerOnProperty(target: Any, method: Method) {
        val onProperty = method.getDeclaredAnnotation(OnProperty::class.java) ?: return
        val name = onProperty.name.ifEmpty { method.name }
        if (propertyFactories.containsKey(name)) return
        val args = method.parameterTypes
        if (args.size > 3) return
        var eIndex = -1
        var mIndex = -1
        var vIndex = -1
        var cIndex = -1
        for (i in args.indices) when (val arg = args[i]) {
            EntityModel::class.java -> mIndex = i
            Int::class.java -> eIndex = i
            ImmutableVariable::class.java -> vIndex = i
            else -> if (Component::class.java.isAssignableFrom(arg))
                cIndex = i else
                throw IllegalStateException()
        }
        method.isAccessible = true
        if (cIndex == -1) {
            val returnType = method.returnType
            if (returnType === Void.TYPE)
                setOnProperty(name, ReflectionFactory(method, target, args.size, eIndex, mIndex, vIndex)) else
                setConverter(returnType, ReflectionConverter(method, target, args.size, eIndex, mIndex, vIndex))
        } else {
            setOnPropertyCreated(name, args[cIndex] as Class<out Component>,
                ReflectionTypedFactory(method, target, args.size, cIndex, mIndex, vIndex))
        }
    }

    private fun registerOnModel(target: Any, method: Method) {
        if (!method.isAnnotationPresent(OnModel::class.java)) return
        val args = method.parameterTypes
        if (args.size != 2) return
        var eIndex = -1
        var mIndex = -1
        for (i in args.indices) when (args[i]) {
            EntityModel::class.java -> mIndex = i
            Int::class.java -> eIndex = i
            else -> throw IllegalStateException()
        }
        if (eIndex == -1 || mIndex == -1) throw IllegalStateException()
        method.isAccessible = true
        entityFactories.add(ReflectionEntityFactory(method, target, eIndex, mIndex))
    }



    private class MapperFactory <T: Component> (private val factory: (EntityModel, T, ImmutableVariable) -> Unit,
                                                private val mapper: ComponentMapper<T>): (EntityModel, Int, ImmutableVariable) -> Unit {

        override fun invoke(model: EntityModel, entityId: Int, variable: ImmutableVariable) {
            factory.invoke(model, mapper.create(entityId), variable)
        }
    }

    private class ReflectionEntityFactory (private val method: Method,
                                           private val target: Any,
                                           private val eIndex: Int,
                                           private val mIndex: Int): EntityFactory {

        private val data = arrayOfNulls<Any>(2)

        override fun accept(model: EntityModel, entityId: Int) {
            data[eIndex] = entityId
            data[mIndex] = model
            method.invoke(target, *data)
        }
    }

    private class ReflectionFactory (private val method: Method,
                                     private val target: Any,
                                     count: Int,
                                     private val eIndex: Int,
                                     private val mIndex: Int,
                                     private val vIndex: Int): (EntityModel, Int, ImmutableVariable) -> Unit {

        private val data = arrayOfNulls<Any>(count)

        override fun invoke(model: EntityModel, entityId: Int, variable: ImmutableVariable) {
            if (mIndex != -1) data[mIndex] = model
            if (eIndex != -1) data[eIndex] = entityId
            if (vIndex != -1) data[vIndex] = variable
            method.invoke(target, *data)
        }
    }

    private class ReflectionConverter <T> (private val method: Method,
                                           private val target: Any,
                                           count: Int,
                                           private val eIndex: Int,
                                           private val mIndex: Int,
                                           private val vIndex: Int): (EntityModel, Int, ImmutableVariable) -> T {

        private val data = arrayOfNulls<Any>(count)

        @Suppress("unchecked_cast")
        override fun invoke(model: EntityModel, entityId: Int, variable: ImmutableVariable): T {
            if (mIndex != -1) data[mIndex] = model
            if (eIndex != -1) data[eIndex] = entityId
            if (vIndex != -1) data[vIndex] = variable
            return method.invoke(target, *data) as T
        }
    }

    private class ReflectionTypedFactory <T: Component> (private val method: Method,
                                                         private val target: Any,
                                                         count: Int,
                                                         private val cIndex: Int,
                                                         private val mIndex: Int,
                                                         private val vIndex: Int): (EntityModel, T, ImmutableVariable) -> Unit {

        private val data = arrayOfNulls<Any>(count)

        override fun invoke(model: EntityModel, component: T, variable: ImmutableVariable) {
            if (mIndex != -1) data[mIndex] = model
            if (cIndex != -1) data[cIndex] = component
            if (vIndex != -1) data[vIndex] = variable
            method.invoke(target, *data)
        }
    }

    private class BooleanFactory <T: Component>(private val mapper: ComponentMapper<T>): (EntityModel, Int, ImmutableVariable) -> Unit {

        override fun invoke(model: EntityModel, entityId: Int, variable: ImmutableVariable) {
            if (variable.toBoolean()) mapper.create(entityId)
        }
    }

    private class FieldFactory <T: Component>(private val field: Field,
                                              private val mapper: ComponentMapper<T>,
                                              private val converter: (EntityModel, Int, ImmutableVariable) -> Any): (EntityModel, Int, ImmutableVariable) -> Unit {

        override fun invoke(model: EntityModel, entityId: Int, variable: ImmutableVariable) {
            field.set(mapper.create(entityId), converter(model, entityId, variable))
        }
    }

    private inner class Listener: ComponentTypeListener {

        override fun initialize(registered: Bag<ComponentType>) {
            registered.forEach { onCreated(it) }
        }

        override fun onCreated(type: ComponentType) {
            register(type.type)
        }
    }



    interface EntityFactory {
        fun accept(model: EntityModel, entityId: Int)
    }
}