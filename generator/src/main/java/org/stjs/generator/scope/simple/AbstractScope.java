package org.stjs.generator.scope.simple;

import static com.google.common.collect.Iterables.concat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.stjs.generator.scope.classloader.ClassWrapper;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public abstract class AbstractScope implements Scope {

	private final Scope parent;
	private final List<Scope> children;

	AbstractScope(Scope parent) {
		this.parent = parent;
		if (parent != null) {
			parent.addChild(this);
		}
		children = Lists.newArrayList();
	}

	private Map<String, Variable> variables = Maps.newHashMap();

	private Multimap<String, Method> methods = ArrayListMultimap.create();

	private Map<String, ClassWrapper> types = Maps.newHashMap();

	public void addField(Field field) {
		variables.put(field.getName(), new FieldVariable(field));
	}

	public void addMethods(String alias, List<Method> methods) {
		this.methods.putAll(alias, methods);
	}

	public void addMethod(Method method) {
		methods.put(method.getName(), method);
	}

	public void addType(ClassWrapper clazz) {
		types.put(clazz.getSimpleName(), clazz);
	}

	public TypeWithScope resolveType(String name) {
		ClassWrapper classWrapper = types.get(name);
		if (classWrapper != null) {
			return new TypeWithScope(this, classWrapper);
		}
		if (parent != null) {
			return parent.resolveType(name);
		}
		return null;
	}

	public Scope addChild(Scope scope) {
		children.add(scope);
		return scope;
	}

	public Scope getParent() {
		return parent;
	}

	@SuppressWarnings("unchecked")
	public <T extends Scope> T closest(Class<T> scopeType) {
		Scope currentScope = this;
		while (currentScope != null && currentScope.getClass() != scopeType) {
			currentScope = currentScope.getParent();
		}
		return (T) currentScope;
	}

	@Override
	public MethodsWithScope resolveMethods(String name) {
		Collection<Method> methodList = methods.get(name);
		if (parent != null) {
			MethodsWithScope parentMethods = parent.resolveMethods(name);
			if (parentMethods != null) {
				// TODO : remove overridden definitions!
				// XXX: in fact is a scope per method
				return new MethodsWithScope(parentMethods.getScope(), methodList != null ? ImmutableList.copyOf(concat(
						parentMethods.getMethods(), methodList)) : parentMethods.getMethods());
			}
		}
		return methodList != null ? new MethodsWithScope(this, methodList) : null;
	}

	@Override
	public List<Scope> getChildren() {
		return ImmutableList.copyOf(children);
	}

	@Override
	public VariableWithScope resolveVariable(String name) {
		Variable variable = variables.get(name);
		if (variable != null) {
			return new VariableWithScope(this, variable);
		}

		if (parent != null) {
			return parent.resolveVariable(name);
		}
		return null;
	}

	@VisibleForTesting
	public Collection<Method> getMethods(String name) {
		return methods.get(name);
	}

	@VisibleForTesting
	public ClassWrapper getType(String name) {
		return types.get(name);
	}

	public void addVariable(Variable variable) {
		variables.put(variable.getName(), variable);
	}

}