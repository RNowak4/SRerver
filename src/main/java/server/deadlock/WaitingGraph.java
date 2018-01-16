package server.deadlock;

import java.util.*;

public class WaitingGraph {
    private Map<String, Map<String, List<GraphEdge>>> graph = new HashMap<>();

    void addEdge(final GraphEdge e) {
        if (graph.containsKey(e.getWaitingUser())) {
            final Map<String, List<GraphEdge>> map = graph.get(e.getWaitingUser());
            if (map.containsKey(e.getLockingUser())) {
                map.get(e.getLockingUser()).add(e);
            } else {
                final List<GraphEdge> list = new ArrayList<>();
                list.add(e);
                map.put(e.getLockingUser(), list);
            }
        } else {
            final HashMap<String, List<GraphEdge>> map = new HashMap<>();
            final List<GraphEdge> list = new ArrayList<>();
            list.add(e);
            map.put(e.getLockingUser(), list);
            graph.put(e.getWaitingUser(), map);
        }
    }

    public void removeEdge(final GraphEdge e) {
        final String wUser = e.getWaitingUser();
        final String lUser = e.getLockingUser();
        graph.get(wUser).get(lUser).remove(e);

        if (graph.get(wUser).get(lUser).isEmpty()) {
            graph.get(wUser).remove(lUser);
            if (graph.get(wUser).isEmpty()) {
                graph.remove(wUser);
            }
        }
    }

    List<GraphEdge> findCycle() {
        if (graph.keySet().size() < 2) {
            return new ArrayList<>();
        }

        final Set<String> visited = new HashSet<>();
        final Deque<String> path = new ArrayDeque<>();
        final Set<String> pathSet = new HashSet<>();
        final Deque<List<String>> stack = new ArrayDeque<>();
        stack.add(new ArrayList<>(graph.keySet()));

        while (!stack.isEmpty()) {
            for (String v : stack.getLast()) {
                if (pathSet.contains(v)) {
                    return mapUsersCycleToEdged(path);
                } else if (!visited.contains(v)) {
                    visited.add(v);
                    path.add(v);
                    pathSet.add(v);
                    stack.addLast(new ArrayList<>(graph.get(v).keySet()));
                } else {
                    pathSet.remove(path.pop());
                    stack.pop();
                }
            }

        }

        return new ArrayList<>();
    }

    private List<GraphEdge> mapUsersCycleToEdged(final Deque<String> path) {
        final List<String> circle = new ArrayList<>(path);
        final List<GraphEdge> result = new ArrayList<>();
        final int circleSize = circle.size();

        for (int i = 0; i < circleSize; i++) {
            final String u1 = circle.get(i);
            final String u2 = circle.get((i+1)%circleSize);
            final List<GraphEdge> edges = graph.get(u1).get(u2);
            final GraphEdge e = getYoungest(edges);

            result.add(e);
        }


        return result;
    }

    static GraphEdge getYoungest(final List<GraphEdge> edges) {
        return edges.stream()
                .sorted(Comparator.comparing(GraphEdge::getTimestamp))
                .findFirst()
                .get();
    }
}