/*******************************************************************************
 * Copyright (c) 2009, 2012 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - completion system
 *******************************************************************************/
package org.eclipse.m2m.atl.adt.ui.text.atl.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The ATL OrderedSet type.
 * 
 * @author <a href="mailto:william.piers@obeo.fr">William Piers</a>
 */
@SuppressWarnings("serial")
public class OrderedSetType extends CollectionType {

	/** The singleton instance. */
	private static OrderedSetType instance;

	private static List<Operation> operations;

	/**
	 * Constructor.
	 * 
	 * @param parameter
	 *            the OrderedSet parameter type
	 */
	public OrderedSetType(OclAnyType parameter) {
		super(parameter);
		oclType = new OclType(computeName("OrderedSet", parameter)); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.m2m.atl.adt.ui.text.atl.types.OclAnyType#getSupertypes()
	 */
	@Override
	public OclAnyType[] getSupertypes() {
		return new OclAnyType[] {new CollectionType(getParameterType())};
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.m2m.atl.adt.ui.text.atl.types.CollectionType#getCollectionType()
	 */
	@Override
	public String getCollectionType() {
		return "OrderedSet"; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.m2m.atl.adt.ui.text.atl.types.OclAnyType#getTypeOperations()
	 */
	@Override
	protected List<Operation> getTypeOperations() {
		if (operations == null) {
			operations = new ArrayList<Operation>() {
				{
					add(new Operation("indexOf", getInstance(), IntegerType.getInstance(), //$NON-NLS-1$
							new HashMap<String, OclAnyType>() {
								{
									put("o", OclAnyType.getInstance()); //$NON-NLS-1$
								}
							}));

					add(new Operation("flatten", getInstance(), null) { //$NON-NLS-1$
						@Override
						public OclAnyType getType(OclAnyType context, Object... parameters) {
							OclAnyType type = context;
							while (type instanceof CollectionType) {
								type = ((CollectionType)type).getParameterType();
							}
							return new OrderedSetType(type);
						}
					});

					add(new Operation("first", getInstance(), null) { //$NON-NLS-1$
						@Override
						public OclAnyType getType(OclAnyType context, Object... parameters) {
							if (context instanceof CollectionType) {
								CollectionType collection = (CollectionType)context;
								return collection.getParameterType();
							}
							return null;
						}
					});

					add(new Operation("last", getInstance(), null) { //$NON-NLS-1$
						@Override
						public OclAnyType getType(OclAnyType context, Object... parameters) {
							if (context instanceof CollectionType) {
								CollectionType collection = (CollectionType)context;
								return collection.getParameterType();
							}
							return null;
						}
					});

					add(new Operation("append", getInstance(), null, //$NON-NLS-1$
							new HashMap<String, OclAnyType>() {
								{
									put("o", OclAnyType.getInstance()); //$NON-NLS-1$
								}
							}));

					add(new Operation("prepend", getInstance(), null, //$NON-NLS-1$
							new HashMap<String, OclAnyType>() {
								{
									put("o", OclAnyType.getInstance()); //$NON-NLS-1$
								}
							}));

					add(new Operation("union", getInstance(), null, //$NON-NLS-1$
							new HashMap<String, OclAnyType>() {
								{
									put("c", CollectionType.getInstance()); //$NON-NLS-1$
								}
							}));

					add(new Operation("insertAt", getInstance(), null, //$NON-NLS-1$
							new LinkedHashMap<String, OclAnyType>() {
								{
									put("n", IntegerType.getInstance()); //$NON-NLS-1$
									put("o", OclAnyType.getInstance()); //$NON-NLS-1$
								}
							}));

					add(new Operation("at", getInstance(), null, //$NON-NLS-1$
							new HashMap<String, OclAnyType>() {
								{
									put("n", IntegerType.getInstance()); //$NON-NLS-1$
								}
							}) {
						@Override
						public OclAnyType getType(OclAnyType context, Object... parameters) {
							if (context instanceof CollectionType) {
								return ((CollectionType)context).getParameterType();
							}
							return OclAnyType.getInstance();
						};
					});

					add(new Operation("subOrderedSet", getInstance(), null, //$NON-NLS-1$
							new LinkedHashMap<String, OclAnyType>() {
								{
									put("lower", IntegerType.getInstance()); //$NON-NLS-1$
									put("upper", IntegerType.getInstance()); //$NON-NLS-1$
								}
							}));
				}
			};
		}
		return operations;
	}

	/**
	 * Returns the default OrderedSet type singleton.
	 * 
	 * @return the default OrderedSet type singleton
	 */
	public static OrderedSetType getInstance() {
		if (instance == null) {
			instance = new OrderedSetType(OclAnyType.getInstance());
		}
		return instance;
	}
}
