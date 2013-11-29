/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.sds.runway.exepl.keyhandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;

public class ExGraphicalViewerKeyHandler extends GraphicalViewerKeyHandler {

	public ExGraphicalViewerKeyHandler(GraphicalViewer viewer) {
		super(viewer);
	}

	protected GraphicalEditPart findSibling(List siblings, Point pStart, int direction,
			EditPart exclude) {
		GraphicalEditPart epCurrent;
		GraphicalEditPart epFinal = null;
		IFigure figure;
		Point pCurrent;
		int distance = Integer.MAX_VALUE;

		Iterator iter = getValidNavigationTargets(siblings).iterator();
		while (iter.hasNext()) {
			epCurrent = (GraphicalEditPart) iter.next();
			if (epCurrent == exclude)
				continue;
			figure = epCurrent.getFigure();
			pCurrent = getNavigationPoint(figure);
			figure.translateToAbsolute(pCurrent);
			if (pStart.getPosition(pCurrent) != direction)
				continue;

			int d = pCurrent.getDistanceOrthogonal(pStart);
			if (d < distance) {
				distance = d;
				epFinal = epCurrent;
			}
		}
		return epFinal;
	}

	private List getValidNavigationTargets(List candidateEditParts) {
		List validNavigationTargetEditParts = new ArrayList();
		for (int i = 0; i < candidateEditParts.size(); i++) {
			EditPart candidate = (EditPart) candidateEditParts.get(i);
			if (isValidNavigationTarget(candidate)) {
				validNavigationTargetEditParts.add(candidate);
			}
		}
		return validNavigationTargetEditParts;
	}

	protected Point getNavigationPoint(IFigure figure) {
		return figure.getBounds().getCenter();
	}

	private boolean isValidNavigationTarget(EditPart editPart) {
		return editPart.isSelectable();
	}
}
