package com.mentalfrostbyte.jello.event;

import com.mentalfrostbyte.jello.Jello;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Annotation based event bus, same contract as the original Jello EventManager:
 * {@link #register(Object)} subscribes every {@link EventTarget} method, {@link #call(Event)} dispatches.
 * Exceptions thrown by a listener are logged and swallowed, like the original.
 */
public final class EventManager {
	private record Listener(Object owner, Method method, MethodHandle handle, byte priority) {
	}

	private static final Map<Class<? extends Event>, List<Listener>> REGISTRY = new ConcurrentHashMap<>();

	private EventManager() {
	}

	@SuppressWarnings("unchecked")
	public static void register(Object owner) {
		for (Method m : owner.getClass().getDeclaredMethods()) {
			EventTarget target = m.getAnnotation(EventTarget.class);
			if (target == null || m.getParameterCount() != 1 || !Event.class.isAssignableFrom(m.getParameterTypes()[0])) {
				continue;
			}
			Class<? extends Event> type = (Class<? extends Event>) m.getParameterTypes()[0];
			List<Listener> list = REGISTRY.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>());
			if (list.stream().anyMatch(l -> l.owner == owner && l.method.equals(m))) {
				continue;
			}
			list.add(new Listener(owner, m, handleOf(m, owner), target.priority()));
			List<Listener> sorted = new ArrayList<>(list);
			sorted.sort(Comparator.comparingInt(Listener::priority));
			list.clear();
			list.addAll(sorted);
		}
	}

	private static MethodHandle handleOf(Method m, Object owner) {
		try {
			m.setAccessible(true);
			return MethodHandles.lookup().unreflect(m).bindTo(owner);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	public static void unregister(Object owner) {
		for (List<Listener> list : REGISTRY.values()) {
			list.removeIf(l -> l.owner == owner);
		}
	}

	public static <T extends Event> T call(T event) {
		List<Listener> list = REGISTRY.get(event.getClass());
		if (list == null) {
			return event;
		}
		for (Listener l : list) {
			try {
				l.handle.invoke(event);
			} catch (Throwable t) {
				Jello.LOGGER.error("Listener {} failed on {}", l.owner.getClass().getSimpleName(), event.getClass().getSimpleName(), t);
			}
		}
		return event;
	}
}
