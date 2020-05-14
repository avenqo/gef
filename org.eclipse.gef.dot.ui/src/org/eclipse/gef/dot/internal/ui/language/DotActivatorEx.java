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
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.gef.dot.internal.language.color.DotColors;
import org.eclipse.gef.dot.internal.ui.DotUiMessages;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.Preferences;

/**
 * This class extends the DotActivator class generated by the Xtext framework to
 * add additional functionality (e.g: Access to the DOT UI Preference Store,
 * Image Registry initialization). The DotActivatorEx class should be registered
 * in the MANIFEST.MF file as the activator of the <i>org.eclipse.gef.dot.ui</i>
 * plugin.
 */
public class DotActivatorEx extends DotActivator {

	public static Preferences dotUiPreferences() {
		return ConfigurationScope.INSTANCE
				.getNode(getInstance().getBundle().getSymbolicName());
	}

	public static IPreferenceStore dotUiPreferenceStore() {
		return new ScopedPreferenceStore(ConfigurationScope.INSTANCE,
				getInstance().getBundle().getSymbolicName());
	}

	/**
	 * Creates an entry on the Eclipse Error Log view.
	 */
	public static void logError(Exception exception) {
		String message = DotUiMessages.DotErrorPrefix;
		String exceptionMessage = exception.getMessage();
		if (exceptionMessage != null) {
			message += " - " + exceptionMessage; //$NON-NLS-1$
		}
		String pluginID = getInstance().getBundle().getSymbolicName();
		IStatus status = new Status(IStatus.ERROR, pluginID, message,
				exception);
		getInstance().getLog().log(status);
		exception.printStackTrace();
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		for (String colorScheme : DotColors.getColorSchemes()) {
			for (String colorName : DotColors.getColorNames(colorScheme)) {
				String hex = DotColors.get(colorScheme, colorName);
				/*
				 * The same hex color code can belong to more than one color
				 * names (synonyms) within one color scheme
				 */
				if (reg.get(hex) == null) {
					Image image = createImage(hex);
					reg.put(hex, image);
				}
			}
		}
	}

	/**
	 * Creates an SWT Image (16x16) that's background color corresponds to the
	 * given <i>colorCode</i>.
	 *
	 * @param colorCode
	 *            The color code in hexadecimal format.
	 * @return The SWT Image (16x16) that's background color corresponds to the
	 *         given <i>colorCode</i>.
	 */
	private Image createImage(String colorCode) {
		Display display = Display.getDefault();
		Image image = new Image(display, 16, 16);
		GC gc = new GC(image);
		Color color = hex2Rgb(display, colorCode);

		gc.setBackground(color);
		gc.fillRectangle(1, 1, 14, 14);
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		// draw border
		gc.drawLine(0, 0, 15, 0); // top border
		gc.drawLine(15, 0, 15, 15); // right border
		gc.drawLine(15, 15, 0, 15); // bottom border
		gc.drawLine(0, 15, 0, 0); // left border

		gc.dispose();
		return image;
	}

	private Color hex2Rgb(Display display, String colorStr) {
		return new Color(display, Integer.valueOf(colorStr.substring(1, 3), 16),
				Integer.valueOf(colorStr.substring(3, 5), 16),
				Integer.valueOf(colorStr.substring(5, 7), 16));
	}
}
