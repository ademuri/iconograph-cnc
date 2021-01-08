import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Kinematics {
	// TODO: create a builder for a class and make these parameters
	final double h3 = 25; // Center of mass distance from pen?
	final double r = 20 * 2 / (2 * Math.PI); // Pulley radius: 20 teeth, 2mm pitch
	final double h = 24.4;	// Distance from belt support center to pen center
	final double theta = 55; // Angle between belt support points and line to pen center
	
	private final double machineWidth;
	
	public Kinematics(double machineWidth) {
		this.machineWidth = machineWidth;
	}
	
	private double f1(double x, double y, double phi, double y1, double y2) {
		final double phi1 = theta - phi;
		final double phi2 = theta + phi;
		double tanGamma = (y + y1 - h * Math.sin(phi1)) / (x - h * Math.cos(phi1));
		double tanLambda = (y + y2 - h * Math.sin(phi2)) / (x - h * Math.cos(phi2));
		return h3 * Math.sin(phi) 
				+ Math.abs(h / (tanGamma + tanLambda)) * (Math.sin(phi2) - Math.sin(phi1) + tanGamma * Math.cos(phi1) - tanLambda * Math.cos(phi2));
	}
	
	private double f2(double x, double y, double phi, double y1, double y2) {
		final double phi1 = theta - phi;
		return Math.sqrt((Math.pow(y1, 2) - Math.pow(r, 2))) / r
				- (y + y1 - h * Math.sin(phi1)) / (x - h * Math.cos(phi1));
	}
	
	private double f3(double x, double y, double phi, double y1, double y2) {
		final double phi2 = theta + phi;
		return Math.sqrt((Math.pow(y2, 2) - Math.pow(r, 2))) / r
				- (y + y2 - h * Math.sin(phi2)) / (machineWidth - (x + h * Math.cos(phi2)));
	}
	
	private double[][] finiteDifferenceJacobian(double x, double y, double phi, double y1, double y2) {
		// See https://math.stackexchange.com/questions/728666/calculate-jacobian-matrix-without-closed-form-or-analytical-form
		final double DELTA = 0.0001;
		double[][] ret = new double[3][3];
		ret[0][0] = (f1(x, y, phi + DELTA, y1, y2) - f1(x, y, phi - DELTA, y1, y2)) / (DELTA * 2);
		ret[0][1] = (f1(x, y, phi, y1 + DELTA, y2) - f1(x, y, phi, y1 - DELTA, y2)) / (DELTA * 2);
		ret[0][2] = (f1(x, y, phi, y1, y2 + DELTA) - f1(x, y, phi, y1, y2 - DELTA)) / (DELTA * 2);
		ret[1][0] = (f2(x, y, phi + DELTA, y1, y2) - f2(x, y, phi - DELTA, y1, y2)) / (DELTA * 2);
		ret[1][1] = (f2(x, y, phi, y1 + DELTA, y2) - f2(x, y, phi, y1 - DELTA, y2)) / (DELTA * 2);
		ret[1][2] = (f2(x, y, phi, y1, y2 + DELTA) - f2(x, y, phi, y1, y2 - DELTA)) / (DELTA * 2);
		ret[2][0] = (f3(x, y, phi + DELTA, y1, y2) - f3(x, y, phi - DELTA, y1, y2)) / (DELTA * 2);
		ret[2][1] = (f3(x, y, phi, y1 + DELTA, y2) - f3(x, y, phi, y1 - DELTA, y2)) / (DELTA * 2);
		ret[2][2] = (f3(x, y, phi, y1, y2 + DELTA) - f3(x, y, phi, y1, y2 - DELTA)) / (DELTA * 2);
		return ret;
	}
	
	public Point computePoint(double x, double y) {
		// See https://robotics.stackexchange.com/a/11410
		final double DELTA = 0.001;
		double phi = 0;
		double y1 = r + 1;
		double y2 = r + 1;
		int n = 0;
		//System.out.format("\n\nPoint (%f, %f)\n", x, y);
		
		// Perform multivariate Newton-Raphson method
		// See example here: https://atozmath.com/example/CONM/NewtonRaphson2.aspx?he=e
		while (true) {
			double[][] jacobian_data = finiteDifferenceJacobian(x, y, phi, y1, y2);
			for (double[] row : jacobian_data) {
				for (double entry : row) {
					if (Double.isNaN(entry)) {
						throw new RuntimeException(String.format("No jacobian exists for point (%f, %f), params (%3.2f, %4.2f, %4.2f)", x, y, phi, y1, y2));
					}
				}
			}
			RealMatrix jacobian = MatrixUtils.createRealMatrix(finiteDifferenceJacobian(x, y, phi, y1, y2));
			RealMatrix jacobianInverse = new LUDecomposition(jacobian).getSolver().getInverse();
			for (double[] row : jacobianInverse.getData()) {
				for (double entry : row) {
					if (Double.isNaN(entry)) {
						throw new RuntimeException(String.format("No inverse exists for point (%f, %f), Jacobian: %s", x, y, jacobian));
					}
				}
			}
			
			double fx_n_data[] = {f1(x, y, phi, y1, y2), f2(x, y, phi, y1, y2), f3(x, y, phi, y1, y2)};
			RealVector fx_n = MatrixUtils.createRealVector(fx_n_data);
			
			RealVector deltaX = jacobianInverse.preMultiply(fx_n);
			double deltaPhi = deltaX.getEntry(0);
			double deltaY1 = deltaX.getEntry(1);
			double deltaY2 = deltaX.getEntry(2);
			
			//System.out.format("Round %3d: (%3.2f, %4.2f, %4.2f), %s, %s\n", n, deltaPhi, deltaY1, deltaY2, jacobian, jacobianInverse);
			
			n++;
			
			if (n > 1_000) {
				throw new RuntimeException("Iteration limit exceeded.");
			}
			
			if (Math.abs(deltaPhi) < DELTA && Math.abs(deltaY1) < DELTA && Math.abs(deltaY2) < DELTA) {
				break;
			}
			phi -= deltaPhi;
			y1 -= deltaY1;
			y2 -= deltaY2;
		}
		
		final double phi1 = theta - phi;
		final double phi2 = theta + phi;
		double tanGamma = (y + y1 - h * Math.sin(phi1)) / (x - h * Math.cos(phi1));
		double tanLambda = (y + y2 - h * Math.sin(phi2)) / (x - h * Math.cos(phi2));
		
		double gamma = Math.atan(tanGamma);
		double lambda = Math.atan(tanLambda);
		
		double left = Math.sqrt(Math.pow(x - h * Math.cos(phi1), 2) + Math.pow(y + y1 - h * Math.sin(phi1), 2))
				- r * tanGamma + r * gamma;
		double right = Math.sqrt(Math.pow(machineWidth - x + h * Math.cos(phi2), 2) + Math.pow(y + y2 - h * Math.sin(phi2), 2))
				- r * tanLambda + r * lambda;
		
		//System.out.format("Final: (phi: %3.2f) (y1: %3.2f) (y2: %3.2f); (phi1: %3.2f) (phi2: %3.2f)\n", phi, y1, y2, phi1, phi2);
		
		return new Point(left, right);
	}

}
