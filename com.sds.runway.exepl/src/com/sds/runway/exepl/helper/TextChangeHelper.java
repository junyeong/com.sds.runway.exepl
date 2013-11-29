/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.sds.runway.exepl.helper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * TextChangeHelper notifies the listner of text lifecycle events on behalf of the widget(s) it listens to.
 *
 * @author Anthony Hunter
 */
public abstract class TextChangeHelper implements Listener {

	private boolean nonUserChange;

	/**
	 * Marks the start of a programmatic change to the widget contents. Clients must call startNonUserChange() before directly setting the widget
	 * contents to avoid unwanted lifecycle events.
	 *
	 * @throws IllegalArgumentException
	 *             if a programmatic change is already in progress.
	 */
	public void startNonUserChange() {
		if (nonUserChange)
			throw new IllegalStateException("we already started a non user change");//$NON-NLS-1$
		nonUserChange = true;
	}

	/**
	 * Clients who call startNonUserChange() should call finishNonUserChange() as soon as possible after the change is done.
	 *
	 * @throws IllegalArgumentException
	 *             if no change is in progress.
	 */
	public void finishNonUserChange() {
		if (!nonUserChange)
			throw new IllegalStateException("we are not in a non user change");//$NON-NLS-1$
		nonUserChange = false;
	}

	/**
	 * Returns true if a programmatic change is in progress.
	 */
	public boolean isNonUserChange() {
		return nonUserChange;
	}

	/**
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	public void handleEvent(Event event) {
		if (event.widget instanceof Text) {
			if (((Text) event.widget).getText().contains("]]>")) {
				((Text) event.widget).setText(((Text) event.widget).getText().replaceAll("\\]\\]>", ""));
			}
		}
		switch (event.type) {
		case SWT.KeyDown:
			//textChanged((Control) event.widget);
			break;
		case SWT.FocusIn:
			textChanged((Control) event.widget);
			break;
		case SWT.FocusOut:
			textChanged((Control) event.widget);
			break;
		case SWT.Modify:
			if ((Control) event.widget instanceof Combo) {
				textChanged((Control) event.widget);
			}
			break;
		}
	}

	/**
	 * Abstract method notified when a text field has been changed.
	 *
	 * @param control
	 */
	public abstract void textChanged(Control control);

	/**
	 * Registers this helper with the given control to listen for events which indicate that a change is in progress (or done).
	 */
	public void startListeningTo(Control control) {
		control.addListener(SWT.FocusOut, this);
		control.addListener(SWT.FocusIn, this);
		control.addListener(SWT.Modify, this);
	}

	/**
	 * Registers this helper with the given control to listen for the Enter key. When Enter is pressed, the change is considered done (this is only
	 * appropriate for single-line Text widgets).
	 */
	public void startListeningForEnter(Control control) {
		// NOTE: KeyDown rather than KeyUp, because of similar usage in CCombo.
		control.addListener(SWT.KeyDown, this);
	}

	/**
	 * Unregisters this helper from a control previously passed to startListeningTo() and/or startListeningForEnter().
	 */
	public void stopListeningTo(Control control) {
		control.removeListener(SWT.FocusOut, this);
		control.removeListener(SWT.FocusIn, this);
		control.removeListener(SWT.Modify, this);
	}

	public void stopListeningForEnter(Control control) {
		// NOTE: KeyDown rather than KeyUp, because of similar usage in CCombo.
		control.removeListener(SWT.KeyDown, this);
	}
}
