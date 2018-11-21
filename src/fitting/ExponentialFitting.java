package fitting;

import java.util.function.DoubleUnaryOperator;

public class ExponentialFitting {

	private ExponentialFitting() {}
	
	
//	public static DoubleUnaryOperator exponentialFit(double[][] systemMatrix) {
//
//		//====================================================================================
//		//Exponential fit 
//		//Ordinary least squares regression: minimizes the squared residuals
//		
//		double xSum = 0.0;
//		double xSquaredSum = 0.0;
//		double lnYSum = 0.0;
//		double xlnYSum = 0.0;
//		
//		for(double[] row:systemMatrix) {
//			xSum+=row[0];
//			xSquaredSum+=row[0]*row[0];
//			lnYSum+=Math.log(row[1]);
//			xlnYSum+=row[0]*Math.log(row[1]);
//		}
//		
//		double a = Math.exp((lnYSum*xSquaredSum-xSum*xlnYSum)/(systemMatrix.length*xSquaredSum-xSum*xSum));
//		double b = (systemMatrix.length*xlnYSum-xSum*lnYSum)/(systemMatrix.length*xSquaredSum-xSum*xSum);
//		return  x -> a*Math.exp(b*x);
//	}
	
	public static DoubleUnaryOperator expFit1(double[][] systemMatrix) {
		double xSum = 0.0;
		double xSquaredSum = 0.0;
		double lnYSum = 0.0;
		double xlnYSum = 0.0;
		
		for(double[] row:systemMatrix) {
			double x = row[0];
			double y = row[1];
			
			xSum+=x;
			xSquaredSum+=x*x;
			lnYSum+=Math.log(y);
			xlnYSum+=x*Math.log(y);
		}
		
		double xSumSquared = xSum*xSum;
		int n = systemMatrix.length;
		
		double a = Math.exp((lnYSum*xSquaredSum-xSum*xlnYSum)/(n*xSquaredSum-xSumSquared));
		double b = (n*xlnYSum-xSum*lnYSum)/(n*xSquaredSum-xSumSquared);
		DoubleUnaryOperator exponentialFunction = x -> a*Math.exp(b*x);
		return exponentialFunction;
	}

	public static DoubleUnaryOperator expFit2(double[][] systemMatrix) {
		double xSquaredYSum = 0.0;
		double ylnYSum = 0.0;
		double xySum = 0.0;
		double xYlnYSum = 0.0;
		double ySum = 0.0;
		
		for(double[] row:systemMatrix) {
			double x = row[0];
			double y = row[1];
		
			ySum+=y;
			xySum += x*y;
			xSquaredYSum += x*x*y;
			ylnYSum += y*Math.log(y);
			xYlnYSum += x*y*Math.log(y);
		}
		
		double a = Math.exp((xSquaredYSum*ylnYSum-xySum*xYlnYSum)/(ySum*xSquaredYSum-xySum*xySum));
		double b = (ySum*xYlnYSum-xySum*ylnYSum)/(ySum*xSquaredYSum-xySum*xySum);
				
		DoubleUnaryOperator exponentialFunction = x -> a*Math.exp(b*x);
		return exponentialFunction;
	}

	public static DoubleUnaryOperator expFit3(double[][] systemMatrix) {

		int n = systemMatrix.length;

		double[][] matrix1 = new double[2][2];
		double[] vector1 = new double[2]; 		
		
		double x1 = systemMatrix[0][0];
		double y1 = systemMatrix[0][1];
		double Sk = 0.0;
		
		for(int i=1; i<n; i++) {
			double x = systemMatrix[i][0];
			double y = systemMatrix[i][1];
			
			if(i>0) {
				double xp = systemMatrix[i-1][0];
				double yp = systemMatrix[i-1][1];
				Sk = Sk + 0.5*(y+yp)*(x-xp);
			}
			//System.out.println("Sk: " + Sk);
		
			matrix1[0][0] += Math.pow(x-x1, 2);
			matrix1[0][1] += (x-x1)*Sk;
			matrix1[1][0] += (x-x1)*Sk;
			matrix1[1][1] += Math.pow(Sk, 2);
			
			vector1[0] += (y-y1)*(x-x1);
			vector1[1] += (y-y1)*Sk;
		}
		

		invertMatrix2x2(matrix1);

		double[] A1B1Vector = multiplyMV2(matrix1,vector1);

		double c = A1B1Vector[1];

		double[][] matrix2 = new double[2][2];
		double[] vector2 = new double[2];
		
		for(int i=0; i<n;i++) {
			double x = systemMatrix[i][0];
			double y = systemMatrix[i][1];

			double ok = Math.exp(c*x);
			
			matrix2[0][0] = n;
			matrix2[0][1] += ok;
			matrix2[1][0] += ok;
			matrix2[1][1] += Math.pow(ok, 2);

			vector2[0] += y; 
			vector2[1] += y*ok; 
		}
		
		invertMatrix2x2(matrix2);
		
		double[] ab = multiplyMV2(matrix2, vector2);
		
		double a = ab[0];
		double b = ab[1];
		
		System.out.println("A: " + a);
		System.out.println("B: " + b);
		System.out.println("C: " + c);
		
		return x->a+b*Math.exp(c*x);
	}

	private static void invertMatrix2x2(double[][] matrix) {
		double a = matrix[0][0];
		double b = matrix[0][1];
		double c = matrix[1][0];
		double d = matrix[1][1];
		
		double factor = 1.0/(a*d-b*c);
		
		matrix[0][0] = factor*d;
		matrix[0][1] = factor*(-1.0*b);
		matrix[1][0] = factor*(-1.0*c);
		matrix[1][1] = factor*(a);
	}


	private static double[] multiplyMV2(double[][] matrix, double[] vector) {
		double[] result = new double[2];
		
		result[0] = matrix[0][0]*vector[0]+matrix[0][1]*vector[1];
		result[1] = matrix[1][0]*vector[0]+matrix[1][1]*vector[1];
		
		return result;
	}

}
