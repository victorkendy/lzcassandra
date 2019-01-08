package com.github.victorkendy.lzcassandra;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AccessorFactory {
	private final Session session;
	private final ConcurrentMap<Class, Object> accessors;

	public AccessorFactory(Session session) {
		this.session = session;
		this.accessors = new ConcurrentHashMap<>();
	}

	public <T> T createAccessor(Class<T> clazz) {
		if(!clazz.isInterface() || clazz.getAnnotation(EnhancedAccessor.class) == null) {
			throw new AccessorValidationException("Accessor must be an interface annotated with @EnhancedAccessor");
		}
		ConcurrentMap<Method, QueryHandler> handlers = new ConcurrentHashMap<>();
		Method[] methods = clazz.getDeclaredMethods();
		for(Method method : methods) {
			QueryHandler handler = buildQueryHandler(method);
			handlers.put(method, handler);
		}

		return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return handlers.get(method).execute(args);
			}
		});
	}

	private QueryHandler buildQueryHandler(Method method) {
		Query queryAnnotation = method.getAnnotation(Query.class);
		String query = queryAnnotation.value();
		QueryStringProcessor.process(query);

		return new QueryHandler();
	}
}
