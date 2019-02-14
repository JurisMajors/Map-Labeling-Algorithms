package algorithms;

import interfaces.models.LabelInterface;
import interfaces.models.SquareInterface;
import Parser.DataRecord;
import models.BoundingBox;
import models.DirectionEnum;
import models.PositionLabel;
import models.Square;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public class TwoPositionBinarySearcher extends BinarySearcher {


    // variable for dfs to keep track of visited nodes
    private boolean[] visited;
    private Stack<Integer> s;

    // edges of implication graph and its inverse
    private ArrayList<Integer>[] adj;
    private ArrayList<Integer>[] adjInv;


    // stores for each node the component it is in
    private int[] scc;

    // keep track of current component
    private int counter;


    // keeps track of the rect height of last created graph
    private float heightLastGraph = -1;
    private float heightLastComp = -1;


    // keeps track of which rectangles have been set in getSolution()
    private boolean[] isSet;


    @Override
    boolean isSolvable(DataRecord record, float height) {
        //System.out.println("running is solvable: " + height);
        if (heightLastGraph != height) {
            createGraph(record, height);
        }

//        for (int i = 0; i < adj.length; i++) {
//            for (int j : adj[i]) {
//                System.out.println(i + " " + j);
//            }
//        }
        return Kosaraju(record.points.size());

    }

    @Override
    void getSolution(DataRecord record, float height) {
        if (heightLastGraph != height) {
            createGraph(record, height);
        }

        int noPoints = record.points.size();
        if (heightLastComp != height) {
            createComponents(noPoints);
        }


        isSet = new boolean[noPoints];

        for (int i = 0; i < noPoints; i++) {
            if (!isSet[i]) {
                assignTrue(record, i, noPoints);
            }
        }
    }


    private void createGraph(DataRecord record, float height) {

        heightLastGraph = height;

        int noPoints = record.points.size();

        // ------------ initialize variables ------------
        visited = new boolean[noPoints * 2];
        s = new Stack<>();
        scc = new int[noPoints * 2];
        counter = 0;

        adj = new ArrayList[noPoints * 2];
        adjInv = new ArrayList[noPoints * 2];


        for (int i = 0; i < noPoints * 2; i++) {
            adj[i] = new ArrayList<>();
            adjInv[i] = new ArrayList<>();
        }


        // ------------ adding edges to graph ------------
        // loop over every point and for both rectangles check overlaps

        for (LabelInterface point : record.points) {
            double x = point.getXMin();
            double y = point.getYMin();

            // label NE of point intersects with NE lables
            Collection<SquareInterface> collection = record.collection.query2D(new BoundingBox(x - height, y - height, x + height, y + height));

            if (collection != null) {
                for (SquareInterface square : collection) {
                    addEdgeAndInverse(point.getID(), ((LabelInterface) square).getID() + noPoints, noPoints);
                }
            }

            // label NE of point intersects with NW lables
            collection = record.collection.query2D(new BoundingBox(x, y - height, x + 2 * height, y + height));
            if (collection != null) {
                for (SquareInterface square : collection) {
                    addEdgeAndInverse(point.getID(), ((LabelInterface) square).getID(), noPoints);
                }
            }

            // label NW of point intersects with NE lables
            collection = record.collection.query2D(new BoundingBox(x - 2 * height, y - height, x, y + height));
            if (collection != null) {
                for (SquareInterface square : collection) {
                    addEdgeAndInverse(point.getID() + noPoints, ((LabelInterface) square).getID() + noPoints, noPoints);
                }
            }

            // label NW of point intersects with NW lables
            collection = record.collection.query2D(new BoundingBox(x - height, y, x, y + height));
            if (collection != null) {
                for (SquareInterface square : collection) {
                    addEdgeAndInverse(point.getID() + noPoints, ((LabelInterface) square).getID(), noPoints);
                }
            }
        }

    }

    private boolean Kosaraju(int noInputs) {
        createComponents(noInputs);


//        System.out.println("final check");
//        for (int i = 0; i < 2 * noInputs; i++) {
//            System.out.println(i + " " + scc[i]);
//
//        }
        for (int i = 0; i < noInputs; i++) {
            if (scc[i] == scc[i + noInputs]) {
                return false;
            }
        }
        return true;
    }

    private void createComponents(int noInputs) {
        heightLastComp = heightLastGraph;
        // Step 1: dfs on original graph
        for (int i = 0; i < noInputs * 2; i++) {
            if (!visited[i]) {
                dfsFirst(i);
            }
        }

        // Step 2: traverse inverse graph based on s
        while (!s.empty()) {
            int n = s.pop();

            if (visited[n]) {
                dfsSecond(n);
                counter++;

            }
        }
    }


    private void addEdgeAndInverse(int a, int b, int noNodes) {
        if (a == b || a % noNodes == b % noNodes) {
            return;
        }
        adj[a].add(b);
        adjInv[b].add(a);
    }


    private void dfsFirst(int start) {
        if (visited[start]) {
            return;
        }

        visited[start] = true;

        for (int i : adj[start]) {
            dfsFirst(i);
        }

        s.push(start);
    }

    private void dfsSecond(int start) {
        if (!visited[start]) {
            return;
        }
        visited[start] = false;

        for (int i : adjInv[start]) {
            dfsSecond(i);
        }

        scc[start] = counter;
    }

    private void assignTrue(DataRecord record, int node, int noNodes) {
        if (node < noNodes) {
            isSet[node] = true;
            ((PositionLabel) record.points.get(node)).setDirection(DirectionEnum.NE);
        } else {
            isSet[node - noNodes] = true;
            ((PositionLabel) record.points.get(node % noNodes)).setDirection(DirectionEnum.NW);
        }

        for (int i : adj[node]) {
            if (!isSet[i % noNodes] && scc[node % noNodes] == scc[i % noNodes]) {
                assignTrue(record, i, noNodes);

            }
        }
    }
}
