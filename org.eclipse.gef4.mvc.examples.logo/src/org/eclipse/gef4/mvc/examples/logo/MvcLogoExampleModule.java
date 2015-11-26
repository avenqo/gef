/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo;

import java.util.List;
import java.util.Map;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.examples.logo.behaviors.FXClickableAreaBehavior;
import org.eclipse.gef4.mvc.examples.logo.parts.FXCreateCurveHoverHandlePart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXDeleteHoverHandlePart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricShapePart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXLogoContentPartFactory;
import org.eclipse.gef4.mvc.examples.logo.parts.FXLogoCursorProvider;
import org.eclipse.gef4.mvc.examples.logo.parts.FXLogoHandlePartFactory;
import org.eclipse.gef4.mvc.examples.logo.policies.AbstractCloneContentPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.CloneCurvePolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.CloneShapePolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXBendCurvePolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXCloneOnClickPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXCreateCurveOnDragPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXCreationMenuItemProvider;
import org.eclipse.gef4.mvc.examples.logo.policies.FXCreationMenuOnClickPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXDeleteFirstAnchorageOnClickPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXRelocateLinkedOnDragPolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXResizeShapePolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXTransformCurvePolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.FXTransformShapePolicy;
import org.eclipse.gef4.mvc.examples.logo.policies.IFXCreationMenuItem;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.behaviors.FXCursorBehavior;
import org.eclipse.gef4.mvc.fx.parts.ChopBoxAnchorProvider;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXRectangleSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.parts.VisualBoundsGeometryProvider;
import org.eclipse.gef4.mvc.fx.parts.VisualOutlineGeometryProvider;
import org.eclipse.gef4.mvc.fx.policies.FXBendPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXDeleteSelectedOnTypePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeConnectionPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeTransformSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeTranslateOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRotateSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXRotateSelectedOnRotatePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXRotateTool;
import org.eclipse.gef4.mvc.fx.tools.FXTypeTool;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;

public class MvcLogoExampleModule extends MvcFxModule {

	@SuppressWarnings("serial")
	@Override
	protected void bindAbstractContentPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		// register (default) interaction policies (which are based on viewer
		// models and do not depend on transaction policies)
		adapterMapBinder
				.addBinding(
						AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY))
				.to(FXFocusAndSelectOnClickPolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY))
				.to(FXHoverOnHoverPolicy.class);
		// geometry provider for selection feedback
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<Provider<IGeometry>>() {
				}, FXDefaultFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
				.to(VisualBoundsGeometryProvider.class);
		// geometry provider for selection handles
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<Provider<? extends IGeometry>>() {
				}, FXDefaultHandlePartFactory.SELECTION_HANDLES_GEOMETRY_PROVIDER))
				.to(VisualBoundsGeometryProvider.class);
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<Provider<? extends IGeometry>>() {
				}, FXDefaultFeedbackPartFactory.SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER))
				.to(VisualOutlineGeometryProvider.class);
		// geometry provider for hover feedback
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<Provider<? extends IGeometry>>() {
				}, FXDefaultFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
				.to(VisualBoundsGeometryProvider.class);
	}

	@SuppressWarnings("serial")
	@Override
	protected void bindAbstractRootPartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractRootPartAdapters(adapterMapBinder);
		adapterMapBinder
				.addBinding(
						AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY,
								"FXCreationMenuOnClick"))
				.to(FXCreationMenuOnClickPolicy.class);
		adapterMapBinder.addBinding(AdapterKey
				.get(new TypeToken<Provider<List<IFXCreationMenuItem>>>() {
				}, FXCreationMenuOnClickPolicy.MENU_ITEM_PROVIDER_ROLE))
				.to(FXCreationMenuItemProvider.class);
		// interaction policy to delete on key type
		adapterMapBinder.addBinding(AdapterKey.get(FXTypeTool.TOOL_POLICY_KEY))
				.to(FXDeleteSelectedOnTypePolicy.class);
	}

	protected void bindFXCreateCurveHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY, "create"))
				.to(FXCreateCurveOnDragPolicy.class);
	}

	protected void bindFXDeleteHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey
						.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY, "delete"))
				.to(FXDeleteFirstAnchorageOnClickPolicy.class);
	}

	protected void bindFXGeometricCurvePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// transaction policy for resize + transform
		adapterMapBinder.addBinding(AdapterKey.get(FXResizePolicy.class))
				.to(FXResizeConnectionPolicy.class);

		adapterMapBinder.addBinding(AdapterKey.get(FXBendPolicy.class))
				.to(FXBendCurvePolicy.class);

		// interaction policy to relocate on drag
		adapterMapBinder
				.addBinding(AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY,
						"translateSelectedOnDrag"))
				.to(FXTranslateSelectedOnDragPolicy.class);

		// interaction policy to delete on key type
		adapterMapBinder.addBinding(AdapterKey.get(FXTypeTool.TOOL_POLICY_KEY))
				.to(FXDeleteSelectedOnTypePolicy.class);

		adapterMapBinder.addBinding(AdapterKey.get(FXTransformPolicy.class))
				.to(FXTransformCurvePolicy.class);

		// cloning
		adapterMapBinder
				.addBinding(AdapterKey.get(AbstractCloneContentPolicy.class))
				.to(CloneCurvePolicy.class);
		adapterMapBinder
				.addBinding(AdapterKey.get(
						FXClickDragTool.CLICK_TOOL_POLICY_KEY, "cloneOnClick"))
				.to(FXCloneOnClickPolicy.class);

		// clickable area resizing
		adapterMapBinder
				.addBinding(AdapterKey.get(FXClickableAreaBehavior.class))
				.to(FXClickableAreaBehavior.class);

	}

	@SuppressWarnings("serial")
	protected void bindFXGeometricShapePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// register resize/transform policies (writing changes also to model)
		adapterMapBinder.addBinding(AdapterKey.get(FXTransformPolicy.class))
				.to(FXTransformShapePolicy.class);
		adapterMapBinder.addBinding(AdapterKey.get(FXResizePolicy.class))
				.to(FXResizeShapePolicy.class);

		// relocate on drag (including anchored elements, which are linked)
		adapterMapBinder
				.addBinding(AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY,
						"translateSelectedOnDrag"))
				.to(FXTranslateSelectedOnDragPolicy.class);
		adapterMapBinder
				.addBinding(AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY,
						"relocateLinked"))
				.to(FXRelocateLinkedOnDragPolicy.class);

		// clone on click
		adapterMapBinder
				.addBinding(AdapterKey.get(AbstractCloneContentPolicy.class))
				.to(CloneShapePolicy.class);
		adapterMapBinder
				.addBinding(AdapterKey.get(
						FXClickDragTool.CLICK_TOOL_POLICY_KEY, "cloneOnClick"))
				.to(FXCloneOnClickPolicy.class);

		// delete on key type
		adapterMapBinder.addBinding(AdapterKey.get(FXTypeTool.TOOL_POLICY_KEY))
				.to(FXDeleteSelectedOnTypePolicy.class);

		// bind chopbox anchor provider
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<Provider<? extends IAnchor>>() {
				})).to(ChopBoxAnchorProvider.class);
	}

	@SuppressWarnings("serial")
	protected void bindFXRectangleSegmentHandlePartAdapters(
			MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// single selection: resize relocate on handle drag without modifier
		adapterMapBinder
				.addBinding(AdapterKey
						.get(FXResizeTranslateOnHandleDragPolicy.class))
				.to(FXResizeTranslateOnHandleDragPolicy.class);
		// rotate on drag + control
		adapterMapBinder.addBinding(
				AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY, "rotate"))
				.to(FXRotateSelectedOnHandleDragPolicy.class);
		// rotate via touch
		adapterMapBinder
				.addBinding(
						AdapterKey.get(FXRotateTool.TOOL_POLICY_KEY, "rotate"))
				.to(FXRotateSelectedOnRotatePolicy.class);
		// multi selection: scale relocate on handle drag without modifier
		adapterMapBinder
				.addBinding(AdapterKey
						.get(FXResizeTransformSelectedOnHandleDragPolicy.class))
				.to(FXResizeTransformSelectedOnHandleDragPolicy.class);
		// change cursor for rotation
		adapterMapBinder.addBinding(AdapterKey.get(FXCursorBehavior.class))
				.to(FXCursorBehavior.class);
		adapterMapBinder.addBinding(
				AdapterKey.get(new TypeToken<Provider<Map<KeyCode, Cursor>>>() {
				}, FXCursorBehavior.CURSOR_PROVIDER_ROLE))
				.to(FXLogoCursorProvider.class);
	}

	protected void bindIContentPartFactory() {
		binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
		}).toInstance(new FXLogoContentPartFactory());
	}

	@Override
	protected void bindIHandlePartFactory() {
		binder().bind(new TypeLiteral<IHandlePartFactory<Node>>() {
		}).toInstance(new FXLogoHandlePartFactory());
	}

	@Override
	protected void configure() {
		super.configure();

		bindIContentPartFactory();

		// contents
		bindFXGeometricShapePartAdapters(AdapterMaps
				.getAdapterMapBinder(binder(), FXGeometricShapePart.class));
		bindFXGeometricCurvePartAdapters(AdapterMaps
				.getAdapterMapBinder(binder(), FXGeometricCurvePart.class));

		// rectangle handles
		bindFXRectangleSegmentHandlePartAdapters(
				AdapterMaps.getAdapterMapBinder(binder(),
						FXRectangleSegmentHandlePart.class));

		// hover handles
		bindFXDeleteHandlePartAdapters(AdapterMaps.getAdapterMapBinder(binder(),
				FXDeleteHoverHandlePart.class));
		bindFXCreateCurveHandlePartAdapters(AdapterMaps.getAdapterMapBinder(
				binder(), FXCreateCurveHoverHandlePart.class));
	}

}