package fitting;

import java.util.function.DoubleUnaryOperator;

public class LinearFitting {

	private LinearFitting() {}

	public static DoubleUnaryOperator linearFit(double[][] systemMatrix) {
		//====================================================================================
		//Linear fit 
		//Ordinary least squares regression: minimizes the squared residuals
		double xAverage = 0.0;
		double yAverage = 0.0;
		
		for(double[] row:systemMatrix) {
			xAverage+=row[0];
			yAverage+=row[1];
		}
		xAverage/=systemMatrix.length;
		yAverage/=systemMatrix.length;

		double sum1=0;
		double sum2=0;
		
		for(double[] row:systemMatrix) {
			double xDifference = row[0]-xAverage;
			double yDifference = row[1]-yAverage;
			sum1+=xDifference*yDifference;
			sum2+=xDifference*xDifference;
		}
		
		double k=sum1/sum2;
		double l=yAverage-k*xAverage;
		DoubleUnaryOperator linearFunction = x->k*x+l;
		return linearFunction;
	}
	
}
