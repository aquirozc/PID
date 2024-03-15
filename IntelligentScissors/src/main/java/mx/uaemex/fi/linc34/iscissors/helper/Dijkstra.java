package mx.uaemex.fi.linc34.iscissors.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {

    public static class Node implements Comparable<Node> {
        public final int x, y, dist;

        public Node(int x, int y, int dist) {
            this.x = x;
            this.y = y;
            this.dist = dist;
        }

        public int compareTo(Node other) {
            return Integer.compare(this.dist, other.dist);
        }
    }

    public static List<int[]> dijkstra(double[][] matrix, int startX, int startY, int targetX, int targetY) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] dist = new double[rows][cols];
        boolean[][] visited = new boolean[rows][cols];
        Node[][] parent = new Node[rows][cols];
        for (double[] row : dist) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        dist[startX][startY] = matrix[startX][startY];

        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(startX, startY, (int) dist[startX][startY]));

        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        while (!pq.isEmpty()) {
            Node node = pq.poll();
            int x = node.x;
            int y = node.y;
            visited[x][y] = true;

            if (x == targetX && y == targetY) {
                List<int[]> path = new ArrayList<>();
                while (x != startX || y != startY) {
                    path.add(new int[]{x, y});
                    Node p = parent[x][y];
                    x = p.x;
                    y = p.y;
                }
                path.add(new int[]{startX, startY});
                return path;
            }

            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                if (nx >= 0 && nx < rows && ny >= 0 && ny < cols && !visited[nx][ny]) {
                    double newDist = dist[x][y] + matrix[nx][ny];
                    if (newDist < dist[nx][ny]) {
                        dist[nx][ny] = newDist;
                        pq.add(new Node(nx, ny, (int) newDist));
                        parent[nx][ny] = new Node(x, y, (int) dist[x][y]);
                    }
                }
            }
        }

        return null; //Una imagen es un grafo conexo, nunca se alcanza esta linea de codigo. 
    }
}
