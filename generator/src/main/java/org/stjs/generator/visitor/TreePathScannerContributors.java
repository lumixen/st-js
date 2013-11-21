package org.stjs.generator.visitor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreeScanner;

public class TreePathScannerContributors<R, P> extends TreeScanner<R, P> {
	private final Multimap<Class<?>, VisitorContributor<? extends Tree, R, P>> contributors = LinkedListMultimap.create();

	public <T extends VisitorContributor<? extends Tree, R, P>> TreePathScannerContributors<R, P> contribute(T contributor) {
		assert contributor != null;
		Class<?> treeNodeClass = getTreeNodeClass(contributor.getClass());
		if (treeNodeClass == null) {
			throw new RuntimeException("Cannot guess the tree node class from the contributor " + contributor + " ");
		}
		contributors.put(treeNodeClass, contributor);
		return this;
	}

	/**
	 * 
	 * @return
	 */
	private Class<?> getTreeNodeClass(Class<?> clazz) {
		Type[] interfaces = clazz.getGenericInterfaces();
		for (Type iface : interfaces) {
			if (iface instanceof ParameterizedType) {
				ParameterizedType ptype = (ParameterizedType) iface;
				if (ptype.getRawType().equals(VisitorContributor.class)) {
					// I need the class of the tree
					return (Class<?>) ptype.getActualTypeArguments()[0];
				}
			}
		}
		return null;
	}

	private Class<?> getTreeInteface(Class<?> clazz) {
		Type[] interfaces = clazz.getGenericInterfaces();
		for (Type iface : interfaces) {
			if (iface instanceof Class<?>) {
				Class<?> type = (Class<?>) iface;
				if (Tree.class.isAssignableFrom(type)) {
					return type;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T extends Tree> R visit(T node, P p, R r) {
		if (node == null) {
			return r;
		}
		Collection<VisitorContributor<? extends Tree, R, P>> nodeContributors = contributors.get(getTreeInteface(node.getClass()));
		if (nodeContributors.isEmpty()) {
			System.err.println("No contributors for node of type:" + getTreeInteface(node.getClass()));
		}
		R lastR = r;
		for (VisitorContributor<? extends Tree, R, P> vc : nodeContributors) {
			lastR = ((VisitorContributor<T, R, P>) vc).visit(this, node, p);
		}
		return lastR;
	}

	@Override
	public R scan(Tree tree, P p) {
		// R r = super.scan(tree, p);
		return visit(tree, p, null);
	}

}
