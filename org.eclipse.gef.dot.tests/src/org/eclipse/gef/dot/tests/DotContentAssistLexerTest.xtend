/*******************************************************************************
 * Copyright (c) 2018, 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import com.google.inject.Inject
import com.google.inject.name.Named
import org.eclipse.gef.dot.tests.ui.DotUiInjectorProvider
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.Lexer
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.ui.LexerUIBindings
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(DotUiInjectorProvider)
class DotContentAssistLexerTest extends AbstractDotLexerTest {

	@Inject @Named(LexerUIBindings.CONTENT_ASSIST) Lexer lexer

	override lexer() {
		lexer
	}

}