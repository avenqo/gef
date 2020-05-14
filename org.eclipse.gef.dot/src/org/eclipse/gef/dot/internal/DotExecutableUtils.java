/********************************************************************************************
 * Copyright (c) 2009, 2017 Fabian Steeg, and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Fabian Steeg                 - initial API and implementation (see bug #277380)
 *     Alexander Nyßen (itemis AG)  - Fixed NPE (see bug #473011)
 *     Tamas Miklossy (itemis AG)   - Refactoring of preferences (bug #446639)
 *                                  - Exporting *.dot files in different formats (bug #446647)
 *                                  - Naming of output file (bug #484198)
 *     Darius Jockel (itemis AG)    - Fixed problems when calling dot on windows with large
 *                                    files (#492395)
 *     Matthias Wienand (itemis AG) - Remove sysouts and return exception message (#521230)
 *
 *********************************************************************************************/
package org.eclipse.gef.dot.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for drawing dot graphs by calling the dot executable.
 *
 * @author Fabian Steeg (fsteeg)
 * @author Alexander Nyßen (anyssen)
 * @author Darius Jockel
 * @author Matthias Wieannd (mwienand)
 */
final public class DotExecutableUtils {

	private DotExecutableUtils() {
		// should not be instantiated by clients
	}

	/**
	 * @param dotExecutablePath
	 *            The path of the local Graphviz 'dot' executable, e.g.
	 *            "C:\\Program Files (x86)\\Graphviz2.38\\bin\\dot.exe"
	 * @param dotInputFile
	 *            The DOT content to render
	 * @param format
	 *            The image format to export the graph to (e.g. 'pdf' or 'png')
	 * @param outputFile
	 *            The output file or <code>null</code> if the input file name
	 *            and location should be used (where only the file extension is
	 *            changed dependent on the format)
	 * @param outputs
	 *            A String array with two Strings, where the first contains the
	 *            output of the input stream and the second contains the output
	 *            of the error stream.
	 * @return The image file generated by rendering the dotInputFile with
	 *         Graphviz, using the specified format
	 */
	public static File renderImage(final File dotExecutablePath,
			final File dotInputFile, final String format, File outputFile,
			String[] outputs) {
		if (outputFile == null) {
			String dotFile = dotInputFile.getName();
			outputFile = new File(dotInputFile.getParent() + File.separator
					+ dotFile.substring(0, dotFile.lastIndexOf('.') + 1)
					+ format);
		}
		String[] localOutputs = executeDot(dotExecutablePath, false,
				dotInputFile, outputFile, format);
		// make the local outputs array visible to the caller
		if (outputs != null && outputs.length == 2) {
			outputs[0] = localOutputs[0];
			outputs[1] = localOutputs[1];
		}
		return outputFile;
	}

	/**
	 * Calls the Graphviz 'dot' executable with the given arguments.
	 *
	 * @param dotExecutablePath
	 *            The path of the local Graphviz 'dot' executable, e.g.
	 *            "C:\\Program Files (x86)\\Graphviz2.38\\bin\\dot.exe"
	 * @param invertYAxis
	 *            Whether to invert the y-axis or not.
	 * @param dotInputFile
	 *            The input file to pass to 'dot'.
	 * @param outputFile
	 *            The output file to pass to 'dot' via the -o option. May be
	 *            <code>null</code>.
	 * @param outputFormat
	 *            The output format to pass to 'dot' via the -T option. May be
	 *            <code>null</code>.
	 * @return A String array with two Strings, where the first contains the
	 *         output of the input stream and the second contains the output of
	 *         the error stream.
	 */
	public static String[] executeDot(final File dotExecutablePath,
			final boolean invertYAxis, final File dotInputFile,
			final File outputFile, final String outputFormat) {
		File buffer = null;
		boolean hasBuffer = false;
		List<String> commands = new ArrayList<>();
		commands.add(dotExecutablePath.getAbsolutePath());
		if (invertYAxis) {
			commands.add("-y");
		}
		if (outputFormat != null) {
			commands.add("-T" + outputFormat);
		}
		if (outputFile != null) {
			buffer = outputFile;
		} else {
			try {
				buffer = File.createTempFile("tmpResult", ".dot");
				hasBuffer = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		commands.add("-o" + buffer.toPath().toString());
		commands.add(dotInputFile.toPath().toString());
		String[] call = call(commands.toArray(new String[] {}));
		if (hasBuffer) {
			FileInputStream input;
			try {
				input = new FileInputStream(buffer);
				call[0] = read(input);
				input.close();
			} catch (Exception e) {
				System.out.println("failed to read temp dot file");
			} finally {
				buffer.delete();
			}
			return call;
		} else {
			return call;
		}
	}

	/***
	 * @param dotExecutable
	 *            path to the dot executable
	 * @return String array of the supported export formats
	 */
	public static String[] getSupportedExportFormats(String dotExecutable) {
		String[] commands = { dotExecutable, "-T?" };
		String[] outputs = call(commands);
		String output = outputs[1];
		if (!output.isEmpty()) {
			String supportedFormats = output
					.substring(output.lastIndexOf(": ") + 2);
			supportedFormats = supportedFormats.trim();
			return supportedFormats.split(" ");
		} else {
			return new String[] {};
		}
	}

	/***
	 * @param commands
	 *            commands to be executed
	 * @return String array with two Strings The first String contains the
	 *         output of the input stream The second String contains the output
	 *         of the error stream
	 */
	private static String[] call(final String[] commands) {
		// System.out.print("Calling '" + Arrays.asList(commands) + "'");
		// //$NON-NLS-1$ //$NON-NLS-2$
		String[] outputs = { "", "" };
		Runtime runtime = Runtime.getRuntime();
		Process p = null;
		try {
			p = runtime.exec(commands);
			p.waitFor();
		} catch (Throwable e) {
			String exitValue = p != null ? Integer.toString(p.exitValue())
					: "?";
			String errorMessage = e.getMessage();
			outputs[1] = "Cannot execute program: " + exitValue + ": "
					+ errorMessage;
		}
		// handle input and error stream only if process succeeded.
		if (p != null) {
			String output = read(p.getInputStream());
			outputs[0] = output;
			String errors = read(p.getErrorStream());
			outputs[1] = errors;
		}
		return outputs;
	}

	private static String read(InputStream is) {
		try {
			return DotFileUtils.read(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
