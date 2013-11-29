package com.sds.runway.exepl.layout;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

public class ExToolbarLayout extends ToolbarLayout {

	protected Insets margin;
	protected Dimension maxChildSize;

	protected boolean adjustWidth;
	protected boolean adjustHeight;

	protected boolean fitParentWidth;

	public ExToolbarLayout(boolean isHorizontal) {
		super(isHorizontal);

		if (isHorizontal) {
			adjustHeight = true;
		} else {
			adjustWidth = true;
		}

		margin = new Insets(0);
		maxChildSize = new Dimension(0, 0);
	}

	@Override
	protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint) {
		calculateMaximumChildSize(container.getChildren(), wHint, hHint, true);
		return super.calculatePreferredSize(container, wHint, hHint).expand(margin.getWidth(), margin.getHeight());
	}

	@Override
	protected Dimension calculateMinimumSize(IFigure container, int wHint, int hHint) {
		return super.calculateMinimumSize(container, wHint, hHint).expand(margin.getWidth(), margin.getHeight());
	}

	private Dimension calculateChildrenSize(List children, int wHint,
			int hHint, boolean preferred) {
		Dimension childSize;
		IFigure child;
		int height = 0, width = 0;
		for (int i = 0; i < children.size(); i++) {
			child = (IFigure) children.get(i);

			if (!child.isVisible()) {
				continue;
			}

			childSize = transposer.t(preferred ? getChildPreferredSize(child,
					wHint, hHint) : getChildMinimumSize(child, wHint, hHint));
			height += childSize.height;
			width = Math.max(width, childSize.width);
		}
		return new Dimension(width, height);
	}

	protected void calculateMaximumChildSize(List<IFigure> children, int wHint, int hHint, boolean preferred) {
		maxChildSize.setSize(0, 0);

		Dimension childSize;
		IFigure child;
		int height = 0, width = 0;

		for (int i = 0; i < children.size(); i++) {
			child = children.get(i);
			childSize = transposer.t(preferred ? getChildPreferredSize(child,
					wHint, hHint) : getChildMinimumSize(child, wHint, hHint));

			height = Math.max(height, childSize.height);
			width = Math.max(width, childSize.width);
		}

		maxChildSize.setSize(width, height);
	}

	@Override
	public void layout(IFigure parent) {
		List children = parent.getChildren();
		int numChildren = children.size();
		Rectangle clientArea = transposer.t(parent.getClientArea());
		int x = clientArea.x + margin.left;
		int y = clientArea.y + margin.top;
		int availableHeight = clientArea.height;

		Dimension prefSizes[] = new Dimension[numChildren];
		Dimension minSizes[] = new Dimension[numChildren];

		int wHint = -1;
		int hHint = -1;
		if (isHorizontal()) {
			hHint = parent.getClientArea(Rectangle.SINGLETON).height;
		} else {
			wHint = parent.getClientArea(Rectangle.SINGLETON).width;
		}

		IFigure child;
		int totalHeight = 0;
		int totalMinHeight = 0;
		int prefMinSumHeight = 0;

		for (int i = 0; i < numChildren; i++) {
			child = (IFigure) children.get(i);

			prefSizes[i] = transposer.t(getChildPreferredSize(child, wHint,
					hHint));
			minSizes[i] = transposer
					.t(getChildMinimumSize(child, wHint, hHint));

			totalHeight += prefSizes[i].height;
			totalMinHeight += minSizes[i].height;
		}
		totalHeight += (numChildren - 1) * spacing;
		totalMinHeight += (numChildren - 1) * spacing;
		prefMinSumHeight = totalHeight - totalMinHeight;

		int amntShrinkHeight = totalHeight
				- Math.max(availableHeight, totalMinHeight);

		if (amntShrinkHeight < 0) {
			amntShrinkHeight = 0;
		}

		for (int i = 0; i < numChildren; i++) {
			int amntShrinkCurrentHeight = 0;
			int prefHeight = prefSizes[i].height;
			int minHeight = minSizes[i].height;
			int prefWidth = prefSizes[i].width;
			int minWidth = minSizes[i].width;
			Rectangle newBounds = new Rectangle(x, y, prefWidth, prefHeight);

			child = (IFigure) children.get(i);

			if (!child.isVisible()) {
				continue;
			}

			if (prefMinSumHeight != 0)
				amntShrinkCurrentHeight = (prefHeight - minHeight)
						* amntShrinkHeight / (prefMinSumHeight);

			int width = Math.min(prefWidth,
					transposer.t(child.getMaximumSize()).width);
			if (isStretchMinorAxis())
				width = transposer.t(child.getMaximumSize()).width;
			width = Math.max(minWidth, Math.min(clientArea.width, width));
			newBounds.width = width;

			int adjust = clientArea.width - width;
			switch (getMinorAlignment()) {
			case ALIGN_TOPLEFT:
				adjust = 0;
				break;
			case ALIGN_CENTER:
				adjust /= 2;
				break;
			case ALIGN_BOTTOMRIGHT:
				break;
			}
			newBounds.x += adjust;
			newBounds.height -= amntShrinkCurrentHeight;

			child.setBounds(transposer.t(newBounds));

			if (adjustWidth) {
				child.getBounds().width = maxChildSize.width;
			}

			if (adjustHeight) {
				child.getBounds().height = maxChildSize.height;
			}

			if (fitParentWidth) {
				child.getBounds().width = parent.getClientArea().width;
			}

			amntShrinkHeight -= amntShrinkCurrentHeight;
			prefMinSumHeight -= (prefHeight - minHeight);
			y += newBounds.height + spacing;
		}
	}

	public Insets getMargin() {
		return margin;
	}

	public void setMargin(Insets margin) {
		this.margin = margin;
	}

	public boolean isAdjustWidth() {
		return adjustWidth;
	}

	public void setAdjustWidth(boolean adjustWidth) {
		this.adjustWidth = adjustWidth;
	}

	public boolean isFitParentWidth() {
		return fitParentWidth;
	}

	public void setFitParentWidth(boolean fitParentWidth) {
		this.fitParentWidth = fitParentWidth;
	}

}
