package org.unbrokendome.gradle.plugins.testsets.internal.conventions;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.gradle.api.internal.IConventionAware;

public final class ConventionHelper {
	
	
	private ConventionHelper() { }
	

	public static void applyConventionProperties(IConventionAware target, final Object propertySource) {
		Class<?> clazz = propertySource.getClass();
		for (final Method method : clazz.getMethods()) {
			ConventionProperty conventionProperty = method.getAnnotation(ConventionProperty.class);
			if (conventionProperty != null) {
				String propertyName = conventionProperty.value();
				method.setAccessible(true);
				target.getConventionMapping().map(propertyName, new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						return method.invoke(propertySource);
					}
				});
			}
		}
	}
}
