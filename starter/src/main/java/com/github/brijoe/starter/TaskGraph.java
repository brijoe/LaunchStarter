package com.github.brijoe.starter;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
        private int inDegree;
        private T vertexNode;

        public Node(T vertexNode) {
            this.vertexNode = vertexNode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;
            return vertexNode != null ? vertexNode.equals(node.vertexNode) : node.vertexNode == null;
        }

        @Override
        public int hashCode() {
            return vertexNode != null ? vertexNode.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "inDegree=" + inDegree +
                    ", vertexNode=" + vertexNode +
                    '}';
        }
    }

    //所有顶点
    private Set<Node> nodes = new HashSet<>();
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
        }
    }


    public void printGraph() {
        StarterLog.d("printGraph");
        Iterator<Node> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            StarterLog.d(node.toString() + "，hashCode=" + node.hashCode());
        }
    }

    //获取0入度节点集合
    public Set<T> getZeroNodes() {
        synchronized (this) {
            Set<T> result = new HashSet<>();
            for (Node node : nodes) {
                if (node.inDegree == 0) {
                    result.add(node.vertexNode);
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
