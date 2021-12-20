package com.github.brijoe.starter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * 有向无环图的定义
 *
 * @param <T>
 */
@SuppressWarnings("ALL")
class TaskGraph<T> {

    //顶点 定义
    private class Node {
        private T vertex;
        private int inDegree;
        private int edgeCount;//仅用于检查环,检查完不再使用

        public Node(T vertex) {
            this.vertex = vertex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;
            return vertex != null ? vertex.equals(node.vertex) : node.vertex == null;
        }

        @Override
        public int hashCode() {
            return vertex != null ? vertex.hashCode() : 0;
        }

        @Override
        public String toString() {
            return vertex.toString();
        }

        public String dump() {
            return "Node{" +
                    "inDegree=" + inDegree +
                    ", vertexNode=" + vertex +
                    '}';
        }

    }

    //所有顶点
    private HashSet<Node> nodes = new HashSet<>();
    //所有边
    private Map<Node, Set<Node>> edges = new HashMap<>();
    //映射
    private Map<T, Node> map = new HashMap<>();

    public void addNode(@NonNull T task) {
        synchronized (this) {
            Node node = new Node(task);
            //处理顶点
            nodes.add(node);
            map.put(task, node);
        }
    }

    public void addEdge(@NonNull T start, @NonNull T end) {
        synchronized (this) {
            Node startNode = map.get(start);
            Node endNode = map.get(end);

            //加入边
            if (edges.containsKey(startNode)) {
                edges.get(startNode).add(endNode);
            } else {
                Set<Node> tmp = new HashSet<>();
                tmp.add(endNode);
                edges.put(startNode, tmp);
            }
            endNode.inDegree++;
            endNode.edgeCount++;
        }
    }


    public void printGraph() {
        StarterLog.d("printGraph");
        Iterator<Node> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            StarterLog.d(node.dump());
        }
    }

    //判断是否是有效的taskGraph
    public void assertValidGraph() {
        Queue<Node> queue = new LinkedList<>();
        List<Node> cyclelist = new ArrayList<>();
        for (Node node : nodes) {
            if (node.edgeCount == 0) {
                queue.add(node);
            }
        }
        int count = 0;
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            count++;
            Set<Node> edgeSet = edges.get(node);
            if (edgeSet != null) {
                for (Node tmp : edgeSet) {
                    if (--tmp.edgeCount == 0) {
                        queue.offer(tmp);
                    }
                }
            }
        }
        // 存在环,提示检查
        if (count != nodes.size()) {
            for (Node node : nodes) {
                if (node.edgeCount > 0) {
                    cyclelist.add(node);
                }
            }
            throw new IllegalArgumentException(
                    String.format("TaskGraph has circle,please check %s !!!", cyclelist));
        }
    }

    //获取0入度节点集合
    public Set<T> getZeroNodes() {
        synchronized (this) {
            Set<T> result = new HashSet<>();
            for (Node node : nodes) {
                if (node.inDegree == 0) {
                    result.add(node.vertex);
                }
            }
            return result;
        }
    }

    //删除一个0入度的节点
    public void removeZeroNode(@NonNull T node) {
        synchronized (this) {
            Node target = map.get(node);
            if (target == null || target.inDegree != 0) {
                return;
            }
            Set<Node> edgeSet = edges.get(target);
            if (edgeSet != null) {
                for (Node tmp : edgeSet) {
                    tmp.inDegree--;
                }
            }
            nodes.remove(target);
            map.remove(node);
            edges.remove(target);
        }
    }

    public boolean isEmpty() {
        synchronized (this) {
            return nodes.size() == 0;
        }
    }
}


