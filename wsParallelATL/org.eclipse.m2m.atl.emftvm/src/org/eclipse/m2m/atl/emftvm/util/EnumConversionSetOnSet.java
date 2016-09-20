/*******************************************************************************
 * Copyright (c) 2011-2013 Vrije Universiteit Brussel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Wagelaar, Vrije Universiteit Brussel - initial API and
 *         implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.m2m.atl.emftvm.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.emf.common.util.Enumerator;

/**
 * Converts {@link Enumerator}s to {@link EnumLiteral}s.
 * 
 * @author <a href="dwagelaar@gmail.com">Dennis Wagelaar</a>
 */
public class EnumConversionSetOnSet extends LazySet<Object> {

	/**
	 * {@link Iterator} for {@link EnumConversionList}.
	 * 
	 * @author <a href="dwagelaar@gmail.com">Dennis Wagelaar</a>
	 */
	public class EnumConversionIterator extends CachingIterator {

		/**
		 * Creates a new {@link EnumConversionIterator}.
		 */
		public EnumConversionIterator() {
			super(dataSource.iterator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object next() {
			final Object next = convert(inner.next());
			updateCache(next);
			return next;
		}
	}

	/**
	 * Creates a new {@link EnumConversionSetOnSet} around <code>dataSource</code>.
	 * 
	 * @param dataSource
	 *            the collection to wrap
	 */
	public EnumConversionSetOnSet(Set<Object> dataSource) {
		super(dataSource);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Object> iterator() {
		if (dataSource == null) {
			return Collections.unmodifiableCollection(cache).iterator();
		}
		return new EnumConversionIterator(); // extends CachingIterator
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		if (dataSource == null) {
			return cache.size();
		}
		return ((Collection<Object>)dataSource).size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		if (dataSource == null) {
			return cache.isEmpty();
		}
		return ((Collection<Object>) dataSource).isEmpty();
	}

	/**
	 * Forces cache completion.
	 * 
	 * @return this list
	 */
	public EnumConversionSetOnSet cache() {
		synchronized (cache) {
			if (dataSource != null) {
				cache.clear();
				for (Object o : dataSource) {
					cache.add(convert(o));
				}
				assert cache.size() == ((Collection<?>) dataSource).size();
				dataSource = null;
			}
		}
		return this;
	}

	/**
	 * Performs the element conversion.
	 * @param object the object to convert
	 * @return the converted object
	 */
	protected final Object convert(final Object object) {
		if (object instanceof Enumerator) {
			return new EnumLiteral(object.toString());
		}
		return object;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createCache() {
		if (dataSource == null) {
			this.cache = Collections.emptySet(); // dataSource == null; cache complete
			this.occurrences = Collections.emptyMap();
		} else {
			this.cache = new LinkedHashSet<Object>(((Collection<Object>) dataSource).size());
		}
		assert this.cache instanceof Set<?>;
	}
}