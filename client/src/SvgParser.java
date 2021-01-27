import java.util.ArrayList;
import java.util.List;

import org.apache.batik.anim.dom.SVGGraphicsElement;
import org.apache.batik.anim.dom.SVGOMCircleElement;
import org.apache.batik.anim.dom.SVGOMLineElement;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.dom.SVGOMPolylineElement;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGPointList;

public class SvgParser {
	private final double scaleX;
	private final double scaleY;
	private final double maxPathSegmentLength;
	private final double maxLineSegmentLength;
	
	public SvgParser(double scaleX, double scaleY, double maxPathSegmentLength, double maxLineSegmentLength) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.maxPathSegmentLength = maxPathSegmentLength;
		this.maxLineSegmentLength = maxLineSegmentLength;
	}
	
	List<Point> parse(SVGGraphicsElement element) {
		if (element instanceof SVGOMPathElement) {
			return pathToPoints((SVGOMPathElement) element);
		} else if (element instanceof SVGOMPolylineElement) {
			return  polylineToPoints((SVGOMPolylineElement) element);
		} else if (element instanceof SVGOMLineElement) {
			return lineToPoints((SVGOMLineElement) element);
		} else if (element instanceof SVGOMCircleElement) {
			return circleToPoints((SVGOMCircleElement) element);
		}
		
		return List.of();
	}

	private List<Point> pathToPoints(SVGOMPathElement path) {
		List<Point> points = new ArrayList<>();
		double length = path.getTotalLength();
		if (length <= 0) {
			return points;
		}

		double step = length
				/ Math.ceil(length * Math.sqrt(Math.pow(scaleX, 2) + Math.pow(scaleY, 2)) / maxPathSegmentLength);
		for (double i = 0; i <= length; i += step) {
			SVGPoint point = path.getPointAtLength((float) i);
			try {
				double x = point.getX() * scaleX;
				double y = point.getY() * scaleY;
				points.add(new Point(x, y));
			} catch (Exception e) {
				System.err.format(
						"Got exception while converting path (%s) with length %.3f to points at length %.3f: %s\n",
						path.getTextContent(), length, i, e.getMessage());
			}
		}
		return points;
	}

	private List<Point> polylineToPoints(SVGOMPolylineElement polyline) {
		List<Point> points = new ArrayList<>();
		SVGPointList pointList = polyline.getPoints();
		for (int i = 0; i < pointList.getNumberOfItems(); i++) {
			SVGPoint svgPoint = pointList.getItem(i);
			points.add(new Point(svgPoint.getX() * scaleX, svgPoint.getY() * scaleY));
		}
		return Point.interpolatePoints(points, maxLineSegmentLength);
	}
	
	private List<Point> lineToPoints(SVGOMLineElement line) {
		Point start = new Point(line.getX1().getBaseVal().getValue() * scaleX, line.getY1().getBaseVal().getValue() * scaleY);
		Point end = new Point(line.getX2().getBaseVal().getValue() * scaleX, line.getY2().getBaseVal().getValue() * scaleY);
		return Point.interpolatePoints(List.of(start, end), maxLineSegmentLength);
	}
	
	private List<Point> circleToPoints(SVGOMCircleElement circle) {
		List<Point> points = new ArrayList<>();
		double x = circle.getCx().getBaseVal().getValue();
		double y = circle.getCy().getBaseVal().getValue();
		double r = circle.getR().getBaseVal().getValue();
		
		double minScale = Math.min(scaleX, scaleY);
		double minCircumference = minScale * 2 * r * Math.PI;
		double angleStep = 2 * Math.PI * maxPathSegmentLength / minCircumference;
		
		for (double angle = 0; angle <= 2 * Math.PI; angle += angleStep) {
			points.add(new Point(scaleX * (x + r * Math.cos(angle)), scaleY * (y + r * Math.sin(angle))));
		}
		
		return points;
	}
}
