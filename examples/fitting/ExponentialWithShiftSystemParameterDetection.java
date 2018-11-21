package fitting;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;

import ui.graph.SimpleGraph;

public class ExponentialWithShiftSystemParameterDetection {

	private ExponentialWithShiftSystemParameterDetection() {}
	
	
	public static void main(String[] args) throws IOException {
				
		//Function to optimize
		File file = new File(System.getProperty("user.dir"),"data/exp-shift-data.txt");
		List<String> fileLines = Files.readAllLines(file.toPath()).stream().filter(l->!l.isEmpty()).map(String::trim).collect(Collectors.toList());		
		
		int rowsCount = fileLines.size();
		double[][] systemMatrix = new double[rowsCount][];
		
		for(int i=0; i<rowsCount;i++){
			String line=fileLines.get(i);
			String[] input = Arrays.stream(line.split(",")).filter(l->!l.isEmpty()).map(String::trim).toArray(String[]::new);
			systemMatrix[i] = Arrays.stream(input).mapToDouble(Double::parseDouble).toArray();
		}
		
//		DoubleUnaryOperator exponentialFunction = ExponentialFitting.expFit1(systemMatrix);
//		DoubleUnaryOperator exponentialFunction = ExponentialFitting.expFit2(systemMatrix);
		DoubleUnaryOperator exponentialFunction = ExponentialFitting.expFit3(systemMatrix);
				
		//Start UI
		SimpleGraph graph = new SimpleGraph(10,10);
		graph.addFunction(exponentialFunction, Color.BLUE);
		for(double[] row:systemMatrix){
			graph.addPoint(row[0], row[1]);
		}
		graph.display();
	}
}
