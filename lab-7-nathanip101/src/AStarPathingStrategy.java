import java.nio.file.Path;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.*;

public class AStarPathingStrategy implements PathingStrategy {
    public List<Point> computePath(Point start, Point end,
            Predicate<Point> canPassThrough,
            BiPredicate<Point, Point> withinReach,
            Function<Point, Stream<Point>> potentialNeighbors) {
        Queue<Node> openList = new PriorityQueue<>(Comparator.comparing(Node::getF));
        HashMap<Point, Node> openMap = new HashMap<>();
        Node startNode = new Node(0, Manhattan(start, end), null, start);
        openList.add(startNode);
        HashMap<Point, Node> closedList = new HashMap<>();
        List<Point> path = new ArrayList<>();

        while (!openList.isEmpty()) {
            Node current = openList.remove();
            if (withinReach.test(current.getPos(), end)) {
                pathCreator(path, current);
                break;
            }
            closedList.put(current.getPos(), current);
            potentialNeighbors.apply(current.getPos())
                    .filter(canPassThrough)
                    .filter(p -> !closedList.containsKey(p))
                    .forEach(n -> {
                        if (openMap.containsKey(n)) {
                            if (openMap.get(n).getG() > current.getG() + 1) {
                                Node temp = new Node(current.getG() + 1, Manhattan(n, end), current, n);
                                openList.add(temp);
                                openList.remove(openMap.get(n));
                                openMap.replace(n, temp);
                            }
                        } else {
                            Node temp = new Node(current.getG() + 1, Manhattan(n, end), current, n);
                            openList.add(temp);
                            openMap.put(n, temp);
                        }
                    });
        }
        return path;
    }

    private void pathCreator(List<Point> path, Node node) {
        if (node.getPrevNode() == null) {
            return;
        }
        path.add(0, node.getPos());
        if (node.getPrevNode() != null) {
            pathCreator(path, node.getPrevNode());
        }
    }

    private int Manhattan(Point start, Point end) {
        return Math.abs(start.getX() - end.getX()) + Math.abs(start.getY() - end.getY());
    }
}