/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.fx.jface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.layout.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.jface.IGraphNodeContentProvider;
import org.eclipse.gef4.zest.fx.jface.IGraphNodeLabelProvider;
import org.eclipse.gef4.zest.fx.jface.INestedGraphContentProvider;
import org.eclipse.gef4.zest.fx.jface.INestedGraphLabelProvider;
import org.eclipse.gef4.zest.fx.jface.ZestContentViewer;
import org.eclipse.gef4.zest.fx.jface.ZestFxJFaceModule;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.reflect.TypeToken;

import javafx.scene.Node;

public class ZestContentViewerTests {

	static class EmptyContentProvider implements IGraphNodeContentProvider {
		@Override
		public void dispose() {
		}

		@Override
		public Object[] getConnectedTo(Object node) {
			return null;
		}

		@Override
		public Object[] getNodes() {
			return null;
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	static class MyContentProvider implements INestedGraphContentProvider {
		public static String alpha() {
			return "alpha";
		}

		public static String beta() {
			return "beta";
		}

		public static String first() {
			return "First";
		}

		public static String gamma() {
			return "gamma";
		}

		public static String second() {
			return "Second";
		}

		public static String third() {
			return "Third";
		}

		private Object input;

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getChildren(Object node) {
			if (node.equals(first())) {
				return new Object[] { alpha(), beta(), gamma() };
			}
			return new Object[] {};
		}

		@Override
		public Object[] getConnectedTo(Object entity) {
			if (entity.equals(first())) {
				return new Object[] { second() };
			}
			if (entity.equals(second())) {
				return new Object[] { third() };
			}
			if (entity.equals(third())) {
				return new Object[] { first() };
			}
			if (entity.equals(alpha())) {
				return new Object[] { beta() };
			}
			if (entity.equals(beta())) {
				return new Object[] { gamma() };
			}
			if (entity.equals(gamma())) {
				return new Object[] { alpha() };
			}
			return null;
		}

		@Override
		public Object[] getNodes() {
			if (input == null) {
				return new Object[] {};
			}
			return new Object[] { first(), second(), third() };
		}

		@Override
		public boolean hasChildren(Object node) {
			return node.equals(first());
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			input = newInput;
		}
	}

	static class MyLabelProvider extends LabelProvider implements IColorProvider, IFontProvider, IToolTipProvider,
			IGraphNodeLabelProvider, INestedGraphLabelProvider {
		private static Image image = new Image(display, 10, 10);

		@Override
		public Color getBackground(Object element) {
			return display.getSystemColor(SWT.COLOR_GREEN);
		}

		@Override
		public Map<String, Object> getEdgeAttributes(Object sourceNode, Object targetNode) {
			return Collections.singletonMap("edge", (Object) true);
		}

		@Override
		public Font getFont(Object element) {
			return element.toString().startsWith("F") ? new Font(display, "Times New Roman", 12, SWT.BOLD)
					: new Font(display, "Times New Roman", 8, SWT.ITALIC);
		}

		@Override
		public Color getForeground(Object element) {
			return display.getSystemColor(SWT.COLOR_BLACK);
		}

		@Override
		public Image getImage(Object element) {
			return image;
		}

		@Override
		public Map<String, Object> getNestedGraphAttributes(Object nestingNode) {
			return Collections.singletonMap("nested", (Object) true);
		}

		@Override
		public Map<String, Object> getNodeAttributes(Object node) {
			return Collections.singletonMap("node", (Object) true);
		}

		@Override
		public Map<String, Object> getRootGraphAttributes() {
			return Collections.singletonMap("root", (Object) true);
		}

		@Override
		public String getText(Object element) {
			if (element instanceof String) {
				return element.toString();
			}
			return null;
		}

		@Override
		public String getToolTipText(Object element) {
			return element.toString().toUpperCase();
		}
	}

	static class NullContentProvider implements INestedGraphContentProvider {
		@Override
		public void dispose() {
		}

		@Override
		public Object[] getChildren(Object node) {
			return "2".equals(node) ? new String[] { "2.1", "2.2" } : null;
		}

		@Override
		public Object[] getConnectedTo(Object node) {
			return null;
		}

		@Override
		public Object[] getNodes() {
			return new String[] { "1", "2", "3" };
		}

		@Override
		public boolean hasChildren(Object node) {
			return "2".equals(node);
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	static class NullLabelProvider extends LabelProvider implements IColorProvider, IFontProvider, IToolTipProvider,
			IGraphNodeLabelProvider, INestedGraphLabelProvider {
		@Override
		public Color getBackground(Object element) {
			return null;
		}

		@Override
		public Map<String, Object> getEdgeAttributes(Object sourceNode, Object targetNode) {
			return null;
		}

		@Override
		public Font getFont(Object element) {
			return null;
		}

		@Override
		public Color getForeground(Object element) {
			return null;
		}

		@Override
		public Map<String, Object> getNestedGraphAttributes(Object nestingNode) {
			return null;
		}

		@Override
		public Map<String, Object> getNodeAttributes(Object node) {
			return null;
		}

		@Override
		public Map<String, Object> getRootGraphAttributes() {
			return null;
		}

		@Override
		public String getToolTipText(Object element) {
			return null;
		}
	}

	private static Display display;

	@AfterClass
	public static void cleanUpClass() {
		display.dispose();
		display = null;
	}

	@BeforeClass
	public static void setUpClass() {
		display = new Display();
	}

	private ZestContentViewer viewer;
	private Shell shell;

	@After
	public void cleanUp() {
		shell.dispose();
		shell = null;
		assertTrue(viewer.getControl().isDisposed());
		viewer = null;
	}

	@Before
	public void setUp() {
		shell = new Shell(display);
		shell.setSize(400, 400);

		viewer = new ZestContentViewer(new ZestFxJFaceModule());
		viewer.createControl(shell, SWT.NONE);
		viewer.setContentProvider(new MyContentProvider());
		viewer.setLabelProvider(new MyLabelProvider());
	}

	@Test
	public void test_colorProvider() {
		viewer.setInput(new Object());
		Map<Object, org.eclipse.gef4.graph.Node> contentNodeMap = viewer.getContentNodeMap();
		org.eclipse.gef4.graph.Node firstNode = contentNodeMap.get(MyContentProvider.first());
		// green background, black foreground
		String rectCssStyle = ZestProperties.getNodeRectCssStyle(firstNode);
		assertEquals("-fx-fill: rgb(0,255,0);-fx-stroke: rgb(0,0,0);", rectCssStyle);
		// => black label
		String labelCssStyle = ZestProperties.getNodeLabelCssStyle(firstNode);
		assertTrue(labelCssStyle.startsWith("-fx-fill: rgb(0,0,0);"));
	}

	@Test
	public void test_fontProvider() {
		viewer.setInput(new Object());
		Map<Object, org.eclipse.gef4.graph.Node> contentNodeMap = viewer.getContentNodeMap();
		org.eclipse.gef4.graph.Node firstNode = contentNodeMap.get(MyContentProvider.first());
		String labelCssStyle = ZestProperties.getNodeLabelCssStyle(firstNode);
		// -fx-fill due to IColorProvider
		assertTrue(labelCssStyle.startsWith("-fx-fill: rgb(0,0,0);"));
		assertTrue(labelCssStyle.contains("-fx-font-family: \"Times New Roman\";"));
		assertTrue(labelCssStyle.contains("-fx-font-size: 12pt;"));
		assertTrue(labelCssStyle.contains("-fx-font-weight: bold;"));
		// check second style (italic)
		org.eclipse.gef4.graph.Node secondNode = contentNodeMap.get(MyContentProvider.second());
		labelCssStyle = ZestProperties.getNodeLabelCssStyle(secondNode);
		assertTrue(labelCssStyle.startsWith("-fx-fill: rgb(0,0,0);"));
		assertTrue(labelCssStyle.contains("-fx-font-family: \"Times New Roman\";"));
		assertTrue(labelCssStyle.contains("-fx-font-size: 8pt;"));
		assertTrue(labelCssStyle.contains("-fx-font-style: italic;"));
	}

	@Test
	public void test_labelProvider() {
		viewer.setInput(new Object());
		Map<Object, org.eclipse.gef4.graph.Node> contentNodeMap = viewer.getContentNodeMap();
		org.eclipse.gef4.graph.Node firstNode = contentNodeMap.get(MyContentProvider.first());
		String label = ZestProperties.getLabel(firstNode);
		assertEquals(MyContentProvider.first(), label);
		javafx.scene.image.Image icon = ZestProperties.getIcon(firstNode);
		assertEquals(10, (int) icon.getWidth());
		assertEquals(10, (int) icon.getHeight());
	}

	@Test
	public void test_nestedGraphContentProvider() {
		viewer.setInput(new Object());
		Map<Object, org.eclipse.gef4.graph.Node> contentNodeMap = viewer.getContentNodeMap();
		org.eclipse.gef4.graph.Node firstNode = contentNodeMap.get(MyContentProvider.first());
		Graph nestedGraph = firstNode.getNestedGraph();
		assertNotNull(nestedGraph);
		org.eclipse.gef4.graph.Node alphaNode = contentNodeMap.get(MyContentProvider.alpha());
		assertEquals(nestedGraph, alphaNode.getGraph());
	}

	@Test
	public void test_nestedLabelProvder_nestedGraphAttributes() {
		viewer.setInput(new Object());
		Map<Object, org.eclipse.gef4.graph.Node> contentNodeMap = viewer.getContentNodeMap();
		org.eclipse.gef4.graph.Node alphaNode = contentNodeMap.get(MyContentProvider.alpha());
		Graph nestedGraph = alphaNode.getGraph();
		assertTrue(nestedGraph.attributesProperty().containsKey("nested"));
		assertTrue((Boolean) nestedGraph.attributesProperty().get("nested"));
		// ensure nested does not get root attributes
		assertFalse(nestedGraph.attributesProperty().containsKey("root"));
	}

	@Test
	public void test_nodeLabelProvder_edgeAttributes() {
		viewer.setInput(new Object());
		Map<Object, org.eclipse.gef4.graph.Node> contentNodeMap = viewer.getContentNodeMap();
		org.eclipse.gef4.graph.Node alphaNode = contentNodeMap.get(MyContentProvider.alpha());
		Graph nestedGraph = alphaNode.getGraph();
		Edge edge = nestedGraph.getEdges().get(0);
		assertTrue(edge.attributesProperty().containsKey("edge"));
		assertTrue((Boolean) edge.attributesProperty().get("edge"));
		// ensure edge does not get node attributes
		assertFalse(edge.attributesProperty().containsKey("node"));
	}

	@Test
	public void test_nodeLabelProvder_nodeAttributes() {
		viewer.setInput(new Object());
		Map<Object, org.eclipse.gef4.graph.Node> contentNodeMap = viewer.getContentNodeMap();
		org.eclipse.gef4.graph.Node alphaNode = contentNodeMap.get(MyContentProvider.alpha());
		assertTrue(alphaNode.attributesProperty().containsKey("node"));
		assertTrue((Boolean) alphaNode.attributesProperty().get("node"));
		// ensure node does not get edge attributes
		assertFalse(alphaNode.attributesProperty().containsKey("edge"));
	}

	@Test
	public void test_nodeLabelProvder_rootGraphAttributes() {
		viewer.setInput(new Object());
		Map<Object, org.eclipse.gef4.graph.Node> contentNodeMap = viewer.getContentNodeMap();
		org.eclipse.gef4.graph.Node firstNode = contentNodeMap.get(MyContentProvider.first());
		Graph rootGraph = firstNode.getGraph();
		assertTrue(rootGraph.attributesProperty().containsKey("root"));
		assertTrue((Boolean) rootGraph.attributesProperty().get("root"));
		// ensure root does not get nested attributes
		assertFalse(rootGraph.attributesProperty().containsKey("nested"));
	}

	@Test
	public void test_provideEmptyNull() {
		viewer.setContentProvider(new EmptyContentProvider());
		viewer.setInput(new Object());
	}

	@Test
	public void test_provideNull() {
		viewer.setContentProvider(new NullContentProvider());
		viewer.setLabelProvider(new NullLabelProvider());
		viewer.setInput(new Object());
	}

	@SuppressWarnings("serial")
	@Test
	public void test_selectionModel() {
		final List<Object> expectation = new ArrayList<>();
		ISelectionChangedListener expectingSelectionListener = new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection structuredSelection = (StructuredSelection) event.getSelection();
				assertEquals(expectation, structuredSelection.toList());
				expectation.clear();
			}
		};
		viewer.addSelectionChangedListener(expectingSelectionListener);
		viewer.setInput(new Object());

		// determine "First" node
		FXViewer fxViewer = viewer.getFXViewer();
		org.eclipse.gef4.graph.Node firstNode = viewer.getContentNodeMap().get(MyContentProvider.first());

		// select "First" node
		expectation.add(firstNode);
		IContentPart<Node, ? extends Node> firstPart = fxViewer.getContentPartMap().get(firstNode);
		fxViewer.getAdapter(new TypeToken<SelectionModel<Node>>() {
		}).prependToSelection(Collections.singletonList(firstPart));
	}

	@Test
	public void test_setLayoutAlgorithm() {
		assertNull(viewer.getLayoutAlgorithm());
		RadialLayoutAlgorithm layoutAlgorithm = new RadialLayoutAlgorithm();
		viewer.setLayoutAlgorithm(layoutAlgorithm);
		assertEquals(layoutAlgorithm, viewer.getLayoutAlgorithm());
		viewer.setInput(new Object());
		Graph rootGraph = (Graph) viewer.getFXViewer().getAdapter(ContentModel.class).getContents().get(0);
		assertEquals(layoutAlgorithm, ZestProperties.getLayout(rootGraph));
	}

	@SuppressWarnings("serial")
	@Test
	public void test_setSelection() {
		viewer.setInput(new Object());
		org.eclipse.gef4.graph.Node firstNode = viewer.getContentNodeMap().get(MyContentProvider.first());
		viewer.setSelection(new StructuredSelection(Arrays.asList(firstNode)));
		List<IContentPart<Node, ? extends Node>> selected = viewer.getFXViewer()
				.getAdapter(new TypeToken<SelectionModel<Node>>() {
				}).getSelectionUnmodifiable();
		assertEquals(1, selected.size());
		IContentPart<Node, ? extends Node> selectedPart = selected.get(0);
		assertEquals(firstNode, selectedPart.getContent());
	}

	@Test
	public void test_toolTipProvider() {
		viewer.setInput(new Object());
		org.eclipse.gef4.graph.Node node = viewer.getContentNodeMap().get(MyContentProvider.first());
		assertEquals(MyContentProvider.first().toUpperCase(), ZestProperties.getTooltip(node));
		node = viewer.getContentNodeMap().get(MyContentProvider.second());
		assertEquals(MyContentProvider.second().toUpperCase(), ZestProperties.getTooltip(node));
	}

}
