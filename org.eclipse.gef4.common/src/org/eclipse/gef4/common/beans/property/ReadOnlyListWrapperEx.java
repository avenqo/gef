/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.common.beans.property;

import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.binding.ListExpressionHelper;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * A replacement for {@link ReadOnlyListWrapper} to fix the following JavaFX
 * issues:
 * <ul>
 * <li>https://bugs.openjdk.java.net/browse/JDK-8089557: fixed by not forwarding
 * listeners to the read-only property but rather keeping the lists of listeners
 * distinct.</li>
 * <li>https://bugs.openjdk.java.net/browse/JDK-8120138: fixed by overwriting
 * equals() and hashCode()</li>
 * </ul>
 *
 * @author anyssen
 *
 * @param <E>
 *            The element type of the wrapped {@link ObservableList}.
 */
public class ReadOnlyListWrapperEx<E> extends ReadOnlyListWrapper<E> {

	private ListExpressionHelper<E> helper = null;

	/**
	 * Creates a new unnamed {@link ReadOnlyListWrapperEx}.
	 */
	public ReadOnlyListWrapperEx() {
		super();
	}

	/**
	 * Creates a new named {@link ReadOnlyListWrapperEx} related to the given
	 * bean.
	 *
	 * @param bean
	 *            The bean to relate the to be created
	 *            {@link ReadOnlyListWrapperEx} to.
	 * @param name
	 *            The name for the to be created {@link ReadOnlyListWrapperEx}.
	 */
	public ReadOnlyListWrapperEx(Object bean, String name) {
		super(bean, name);
	}

	/**
	 * Creates a new named {@link ReadOnlyListWrapperEx}, related to the given
	 * bean and provided with the initial value.
	 *
	 * @param bean
	 *            The bean to relate the to be created
	 *            {@link ReadOnlyListWrapperEx} to.
	 * @param name
	 *            The name for the to be created {@link ReadOnlyListWrapperEx}.
	 * @param initialValue
	 *            The initial value of the to be created
	 *            {@link ReadOnlyListWrapperEx}.
	 */
	public ReadOnlyListWrapperEx(Object bean, String name,
			ObservableList<E> initialValue) {
		super(bean, name, initialValue);
	}

	/**
	 * Creates a new unnamed {@link ReadOnlyListWrapperEx} with the given
	 * initial value.
	 *
	 * @param initialValue
	 *            The initial value of the to be created
	 *            {@link ReadOnlyListWrapperEx}.
	 */
	public ReadOnlyListWrapperEx(ObservableList<E> initialValue) {
		super(initialValue);
	}

	@Override
	public void addListener(
			ChangeListener<? super ObservableList<E>> listener) {
		helper = ListExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		helper = ListExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void addListener(ListChangeListener<? super E> listener) {
		helper = ListExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void bindBidirectional(Property<ObservableList<E>> other) {
		try {
			super.bindBidirectional(other);
		} catch (IllegalArgumentException e) {
			if ("Cannot bind property to itself".equals(e.getMessage())
					&& this != other) {
				// XXX: The super implementation relies on equals() not on
				// object identity to infer whether a binding is valid. It thus
				// throw an IllegalArgumentException if two equal properties are
				// passed in, even if they are not identical. We have to
				// ensure they are thus unequal to establish the binding; as
				// our value will be initially overwritten anyway, we may adjust
				// the local value; to reduce noise, we only adjust the local
				// value if necessary
				if (other.getValue() == null) {
					if (getValue() == null) {
						// set to value != null
						setValue(FXCollections
								.observableList(new ArrayList<E>()));
					}
				} else {
					if (getValue().equals(other)) {
						// set to null value
						setValue(null);
					}
				}
				// try again
				super.bindBidirectional(other);
			} else {
				throw (e);
			}
		}
	}

	@Override
	public boolean equals(Object other) {
		// Overwritten here to compensate an inappropriate equals()
		// implementation on Java 7
		// (https://bugs.openjdk.java.net/browse/JDK-8120138)
		if (this == other) {
			return true;
		}
		if (other == null || !(other instanceof List)) {
			return false;
		}

		if (get() == null) {
			return false;
		}
		return get().equals(other);
	}

	@Override
	protected void fireValueChangedEvent() {
		ListExpressionHelper.fireValueChangedEvent(helper);
		super.fireValueChangedEvent();
	}

	@Override
	protected void fireValueChangedEvent(
			ListChangeListener.Change<? extends E> change) {
		ListExpressionHelper.fireValueChangedEvent(helper, change);
		super.fireValueChangedEvent(change);
	}

	@Override
	public int hashCode() {
		// Overwritten here to compensate an inappropriate hashCode()
		// implementation on Java 7
		// (https://bugs.openjdk.java.net/browse/JDK-8120138)
		// XXX: As we rely on equality to remove a binding again, we have to
		// ensure the hash code is the same for a pair of given properties.
		// We fall back to the very easiest case here (and use a constant).
		return 0;
	}

	@Override
	public void removeListener(
			ChangeListener<? super ObservableList<E>> listener) {
		helper = ListExpressionHelper.removeListener(helper, listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		helper = ListExpressionHelper.removeListener(helper, listener);
	}

	@Override
	public void removeListener(ListChangeListener<? super E> listener) {
		helper = ListExpressionHelper.removeListener(helper, listener);
	}

	@Override
	public void unbindBidirectional(Property<ObservableList<E>> other) {
		try {
			super.unbindBidirectional(other);
		} catch (IllegalArgumentException e) {
			if ("Cannot bind property to itself".equals(e.getMessage())
					&& this != other) {
				// XXX: The super implementation relies on equals() not on
				// object identity to infer whether a binding is valid. It thus
				// throw an IllegalArgumentException if two equal properties are
				// passed in, even if they are not identical. We have to
				// ensure they are thus unequal to remove the binding; we
				// have to restore the current value afterwards.
				ObservableList<E> oldValue = getValue();
				if (other.getValue() == null) {
					// set to value != null
					setValue(FXCollections.observableList(new ArrayList<E>()));
				} else {
					// set to null value
					setValue(null);
				}
				// try again
				super.unbindBidirectional(other);
				setValue(oldValue);
			} else {
				throw (e);
			}
		}
	}

}