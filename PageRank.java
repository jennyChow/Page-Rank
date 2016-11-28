import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PageRank {

    //the threshold to converge
    private static final double CONVERGE_EPSILON = 0.000000000001;
    private boolean useRationalNumber = true;
    private Number graph[][];

    PageRank() {
        useRationalNumber = true;
        graph = null;
    }

    //read graph from input
    public void readGraphFromInput() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = null;

        //read the matrix dimension
        while (true) {
            try {
                line = br.readLine();
            } catch (IOException e) {
            }
            if (line == null) {
                System.out.println("Wrong input with empty row! Exit the program!");
                System.exit(1);
            } else if (line.trim().startsWith("#") || line.trim().isEmpty()) {
            } else {
                break;
            }
        }
        String[] row_column = line.split("X|x");
        if (row_column.length != 2) {
            System.out.println("Wrong input format for matrix dimension. The input dimention should be " +
                    "something similar to 5X5!");
            System.exit(1);
        }
        int row = Integer.parseInt(row_column[0]);
        int column = Integer.parseInt(row_column[1]);
        if (row != column) {
            System.out.println("Wrong row and column. The matrix row number and column number should be the same!");
            System.exit(1);
        }

        graph = new Number[row][column];

        // read matrix values
        for (int i = 0; i < row; i++) {
            while (true) {
                try {
                    line = br.readLine();
                } catch (IOException e) {
                }
                if (line == null) {
                    System.out.println("Wrong input with empty row! Exit the program!");
                    System.exit(1);
                } else if (line.trim().startsWith("#")) {
                } else {
                    break;
                }
            }

            String[] splitedLine = line.split("\\s+");
            if (splitedLine.length != column) {
                System.out.println("Wrong number of items in one row! Exit the program!");
                System.exit(1);
            }

            for (int j = 0; j < splitedLine.length; j++) {
                String valueStr = splitedLine[j];
                Number number;

                //represents a rational number if f the string contains '/'
                if (valueStr.contains("/")) {
                    String[] rational_number_arr = valueStr.split("/");
                    Rational rationalValue = new Rational(
                            Integer.parseInt(rational_number_arr[0]),
                            Integer.parseInt(rational_number_arr[1])
                    );
                    number = new Number(rationalValue);
                } else {
                    double doubleValue = Double.parseDouble(valueStr);
                    if (doubleValue - (int)doubleValue > 0) {
                        // store as a double if it's a real number
                        useRationalNumber = false;
                        number = new Number(doubleValue);
                    } else {
                        // Store as a rational number if it's a integer
                        number = new Number(new Rational((int)doubleValue, 1));
                    }
                }

                if (number.isNegative()) {
                    System.out.println("Wrong input: negative input detected! Exit the program!");
                    System.exit(1);
                }
                graph[i][j] = number;
            }
        }
    }

    private void printGraph() {
        for (Number[] graph1 : graph) {
            for (int j = 0; j < graph.length; j++) {
                System.out.print(graph1[j] + "\t");
            }
            System.out.println();
        }
    }

    // remove sink nodes from the graph
    public void removeSinkNode() {
        int nodeCount = graph.length;
        int[] outDegree = new int[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            outDegree[i] = 0;
        }

        // build outDegree
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < nodeCount; j++) {
                if (graph[j][i].isPositive() && j != i) {
                    outDegree[i]++;
                }
            }
        }

        while (true) {
            // find dead node
            boolean foundDeadNode = false;
            int dead_node = -1;
            for (int i = 0; i < nodeCount; i++) {
                if (outDegree[i] == 0) {
                    dead_node = i;
                    foundDeadNode = true;
                    outDegree[i] = -1;
                    break;
                }
            }
            //if cannot find dead node，exit while loop
            if (!foundDeadNode) {
                break;
            }
            //update graph
            for (int i = 0; i < nodeCount; i++) {
                if (graph[dead_node][i].isPositive() && i != dead_node) {
                    graph[dead_node][i] = new Number(new Rational(0, 1));
                    outDegree[i]--;
                }
                graph[i][dead_node] = new Number(new Rational(0, 1));
            }
        }

        //build a new graph
        int newGraphNodeCount = 0;
        for (int i = 0; i < nodeCount; i++) {
            if (outDegree[i] != -1) {
                newGraphNodeCount++;
            }
        }
        Number [][] newGraph = new Number[newGraphNodeCount][newGraphNodeCount];
        int newRow = 0;
        for(int i = 0; i < nodeCount; i++)  {
            if (outDegree[i] == -1) {
                continue;
            }
            int newCol = 0;
            for (int j = 0; j < nodeCount; j++) {
                if (outDegree[j] == -1) {
                    continue;
                }
                newGraph[newRow][newCol] = graph[i][j];
                newCol++;
            }
            newRow++;
        }
        graph = newGraph;

        //adjust weight for every node
        for (int i = 0; i < graph.length; i++) {
            int outEdgeCount = 0;
            for (Number[] graph1 : graph) {
                if (graph1[i].isPositive()) {
                    outEdgeCount++;
                }
            }

            for (Number[] graph1 : graph) {
                if (graph1[i].isPositive()) {
                    if (useRationalNumber) {
                        graph1[i] = new Number(new Rational(1, outEdgeCount));
                    } else {
                        graph1[i] = new Number(1.0 / (double)outEdgeCount);
                    }
                }
            }
        }

        // build output string
        String removedNodeStr = "";
        int removeNodeIndex = 0;
        for (int i=0; i<outDegree.length; i++) {
            if (outDegree[i] == -1) {
                if (removeNodeIndex == 0) {
                    removedNodeStr += i;
                } else {
                    removedNodeStr += "," + i;
                }
                removeNodeIndex ++;
            }
        }
        String outputStr;
        if (removeNodeIndex == 0) {
            outputStr = "Input matrix with dimension " + graph.length + "x" + graph.length + " after re-weighting:";
        } else {
            outputStr = "Input matrix with dimension " + graph.length + "x" + graph.length +
                    " after removing sink nodes " + removedNodeStr + " after re-weighting:";
        }
        System.out.println(outputStr);
        this.printGraph();
    }

    //compute page rank values
    public void computePageRank () {
        int m = graph.length;
        Number[] vectorOld = new Number[m];
        for (int i = 0 ; i < m; i++){
            vectorOld[i] = new Number(new Rational(1, m));
        }
        Number[] vectorNew = new Number[m];
        Number beta = new Number(new Rational(7, 8));
        Number leakedWeight = new Number(new Rational(1, 8 * m));  // leakedWeight = (1 - beta)/m;

        int currentIteration = 1;
        while(true) {
            for (int i = 0; i < m; i++) {
                vectorNew[i] = new Number(new Rational(0, 1));
                for (int j = 0; j < m; j++) {
                    vectorNew[i] = vectorNew[i].plus(graph[i][j].times(vectorOld[j]));
                }
                vectorNew[i] = beta.times(vectorNew[i]).plus(leakedWeight);
            }

            System.out.println("--- Iteration number " + currentIteration + ":");
            for (int i=0; i<m; i++) {
                System.out.print(vectorNew[i] + "\t");
            }
            System.out.println("\n");

            if (currentIteration > 5) {
                double absoluteDiff = 0.0;
                for (int i = 0; i < m; i++) {
                    absoluteDiff += Math.abs(vectorOld[i].getDoubleValue() - vectorNew[i].getDoubleValue());
                }
                if (absoluteDiff < CONVERGE_EPSILON) {
                    break;
                }
            }

            System.arraycopy(vectorNew, 0, vectorOld, 0, m);

            currentIteration ++;
            // If this is already after 5 iteration, we need to change all rational number to real number
            if (currentIteration > 5) {
                useRationalNumber = false;
                for (Number[] graph1 : graph) {
                    for (int j = 0; j<graph.length; j++) {
                        graph1[j].changeToDoubleVersion();
                    }
                }
                for (int i=0; i<vectorOld.length; i++) {
                    vectorOld[i].changeToDoubleVersion();
                    vectorNew[i].changeToDoubleVersion();
                }
                beta.changeToDoubleVersion();
                leakedWeight.changeToDoubleVersion();
            }
        }
    }

}
