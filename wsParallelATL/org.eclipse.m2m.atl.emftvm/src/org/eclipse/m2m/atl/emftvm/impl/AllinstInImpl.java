/*******************************************************************************
 * Copyright (c) 2011 Vrije Universiteit Brussel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Wagelaar, Vrije Universiteit Brussel - initial API and
 *         implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.m2m.atl.emftvm.impl;


import org.eclipse.emf.ecore.EClass;
import org.eclipse.m2m.atl.emftvm.AllinstIn;
import org.eclipse.m2m.atl.emftvm.EmftvmPackage;
import org.eclipse.m2m.atl.emftvm.Opcode;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Allinst in</b></em>'.
 * @author <a href="mailto:dennis.wagelaar@vub.ac.be">Dennis Wagelaar</a>
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class AllinstInImpl extends InstructionImpl implements AllinstIn {
	/**
	 * <!-- begin-user-doc -->
	 * Creates a new {@link AllinstInImpl}.
	 * <!-- end-user-doc -->
	 */
	protected AllinstInImpl() {
		super();
		opcode = Opcode.ALLINST_IN;
		stackConsumption = 2;
		stackProduction = 1;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Returns the {@link EClass} that correspond to this metaclass.
	 * @return the {@link EClass} that correspond to this metaclass.
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EmftvmPackage.Literals.ALLINST_IN;
	}

} //Allinst_inImpl
