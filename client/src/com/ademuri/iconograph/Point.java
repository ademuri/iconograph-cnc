package com.ademuri.iconograph;
import java.util.ArrayList;
import java.util.List;

public class Point {
	public final double x;
	public final double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point translate(double xOffset, double yOffset) {
		return new Point(x + xOffset, y + yOffset);
	}

	public double distanceTo(Point other) {
		return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
	}

	public String toString() {
		return String.format("(%3.2f, %3.2f)", x, y);
	}

	public static List<Point> interpolatePoints(List<Point> points, double maxSegmentLength) {
		if (points.size() < 2) {
			throw new IllegalArgumentException("Can't interpolate over less than two points: " + points.toString());
		}
		List<Point> interpolated = new ArrayList<>();
		interpolated.add(points.get(0));
		for (int i = 1; i < points.size(); i++) {
			Point point = points.get(i);
			Point prevPoint = points.get(i - 1);
			double xDelta = point.x - prevPoint.x;
			double yDelta = point.y - prevPoint.y;
			double length = Math.sqrt(Math.pow(xDelta, 2) + Math.pow(yDelta, 2));
			double steps = Math.ceil(length / maxSegmentLength);
			for (int step = 1; step <= steps; step++) {
				Point interpolatedPoint = new Point(prevPoint.x + xDelta * step / steps,
						prevPoint.y + yDelta * step / steps);
				interpolated.add(interpolatedPoint);
			}
		}

		return interpolated;
	}

	public static List<List<Point>> interpolateLines(List<List<Point>> lines, double maxSegmentLength) {
		List<List<Point>> ret = new ArrayList<>();

		for (List<Point> points : lines) {
			ret.add(interpolatePoints(points, maxSegmentLength));
		}

		return ret;
	}
}
