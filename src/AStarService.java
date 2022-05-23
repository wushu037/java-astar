import java.util.*;

/**
 * 寻路算法
 * 推荐文章：[A星算法详解(个人认为最详细,最通俗易懂的一个版本)](https://blog.csdn.net/hitwhylz/article/details/23089415)
 * @author wushu
 * @create 2021-07-28 14:49
 */
public class AStarService {
    /**
     * 横竖移动代价
     */
    public final static int DIRECT_VALUE = 10;
    /**
     * 斜移动代价
     */
    public final static int OBLIQUE_VALUE = 14;
    /**
     * 障碍值 1  可行走为0
     */
    public final static int BAR = 1;

    /**
     * 寻路调用
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param maps
     * @return
     */
    public static List<Node> searchWay(int startX, int startY, int endX, int endY, byte[][] maps) {
        Queue<Node> openList = new PriorityQueue<Node>(); // 优先队列(升序)
        Map<Long, Node> closeMap = new HashMap<>();
        Node start = new Node(startX, startY);
        Node end = new Node(endX, endY);
        MapInfo mapInfo = new MapInfo(maps, start, end);
        List<Node> path = searchWay(mapInfo, openList, closeMap);

        // 删除起点(起点的下个点是寻路的第一个点。如果不需要这么处理可以注释掉)
//        if (path != null && !path.isEmpty()) {
//            path.remove(0);
//        }

        // todo 可以吧closeMap打印出来看试探到的路径

        return path;
    }

    /**
     * 算法开始。循环移动节点寻找路径，设定循环结束条件:OpenList为空或者最终节点在CloseList
     */
    private static List<Node> searchWay(MapInfo mapInfo, Queue<Node> openList, Map<Long, Node> closeMap) {
        if (mapInfo.maps[mapInfo.start.coord.x][mapInfo.start.coord.y] == BAR ||
                mapInfo.maps[mapInfo.end.coord.x][mapInfo.end.coord.y] == BAR) {
            return new ArrayList<>();
        }
        if (mapInfo.start.coord == mapInfo.end.coord) {
            return new ArrayList<>();
        }
        openList.clear();
        closeMap.clear();
        // 将起点放入openList
        openList.add(mapInfo.start);
        // 寻路
        return moveNodes(mapInfo, openList, closeMap);
    }

    /**
     * 通过不断地移动节点来寻找路径
     */
    private static List<Node> moveNodes(MapInfo mapInfo, Queue<Node> openList, Map<Long, Node> closeMap) {
        List<Node> path = new ArrayList<>();
        // closeMap中保存每次遍历时的最优节点(current)，这些路径点不会再被关注
        while (!openList.isEmpty()) { // 循环，直到openList为空
            // 如果终点在closeList，说明已经找到了终点，则绘制路径
            if (isCoordInClose(mapInfo.end.coord, closeMap)) {
                // 回溯法绘制路径
                path = drawPath(mapInfo.maps, mapInfo.end);
                break;
            }
            // 从openList取出并删除一个最优节点作为要处理的节点，并把这个节点加入到closeList。(closeList中的节点不再被关注)
            Node current = openList.poll();
            closeMap.put(getCloseKey(current.coord), current);
            // 添加所有相邻节点到openList
            addNeighborNodeToOpen(mapInfo, current, openList, closeMap);
        }
        return path;
    }

    /**
     * 把相邻节点添加到openList，这些节点会通过 {@link Node#compareTo(Node)} 排序
     */
    private static void addNeighborNodeToOpen(MapInfo mapInfo, Node current, Queue<Node> openList, Map<Long, Node> closeMap) {
        int x = current.coord.x;
        int y = current.coord.y;
        // 左
        addNeighborNodeToOpen(mapInfo, current, x - 1, y, DIRECT_VALUE, openList, closeMap);
        // 上
        addNeighborNodeToOpen(mapInfo, current, x, y - 1, DIRECT_VALUE, openList, closeMap);
        // 右
        addNeighborNodeToOpen(mapInfo, current, x + 1, y, DIRECT_VALUE, openList, closeMap);
        // 下
        addNeighborNodeToOpen(mapInfo, current, x, y + 1, DIRECT_VALUE, openList, closeMap);
        // 右上
        addNeighborNodeToOpen(mapInfo, current, x - 1, y - 1, OBLIQUE_VALUE, openList, closeMap);
        // 右下
        addNeighborNodeToOpen(mapInfo, current, x + 1, y - 1, OBLIQUE_VALUE, openList, closeMap);
        // 左下
        addNeighborNodeToOpen(mapInfo, current, x + 1, y + 1, OBLIQUE_VALUE, openList, closeMap);
        // 左上
        addNeighborNodeToOpen(mapInfo, current, x - 1, y + 1, OBLIQUE_VALUE, openList, closeMap);
    }


    /**
     * 把节点添加到openList(如果可以的话)
     */
    private static void addNeighborNodeToOpen(MapInfo mapInfo, Node current, int x, int y, int value, Queue<Node> openList, Map<Long, Node> closeMap) {
        if (canAddNodeToOpen(mapInfo, x, y, closeMap)) {
            Coord coord = new Coord(x, y);
            // 计算邻节点的G值(=当前节点的G值+当前节点到该相邻节点的G值)
            int G = current.G + value;
            // OpenList中查找该节点
            Node node = findNodeInOpen(coord, openList);
            if (node == null) { // 如果节点不在openList中，则创建节点并添加
                // 计算H值 (当前节点到终点的估算代价)
                Node end = mapInfo.end;
                int H = calcH(end.coord, coord);
                if (end.coord.equals(coord)) { // 如果是终点，则不需要重复创建对象
                    node = end;
                    node.parent = current;
                    node.G = G;
                    node.H = H;
                } else {
                    node = new Node(coord, current, G, H);
                }
                openList.add(node);
            } else if (node.G > G) { // 如果已经在openList中，则判断原G值和现有G值，采用最小的
                node.G = G;
                node.parent = current;
                openList.add(node);
            }
        }
    }

    /**
     * 判断节点能否放入OpenList
     * 不能加入的情况：1.超出地图范围; 2.节点是障碍; 3.节点已经被访问过(即在closeList中存在);
     */
    private static boolean canAddNodeToOpen(MapInfo mapInfo, int x, int y, Map<Long, Node> closeMap) {
        // 如果不在地图中，则不能加入openList
        if (x < 0 || x >= mapInfo.width || y < 0 || y >= mapInfo.hight) {
            return false;
        }
        // 如果节点是障碍，则不能加入openList
        if (mapInfo.maps[x][y] == BAR) {
            return false;
        }
        // 如果节点在closeList，则不能加入openList
        if (isCoordInClose(new Coord(x, y), closeMap)) {
            return false;
        }
        return true;
    }

    /**
     * 判断坐标是否在closeList中
     */
    private static boolean isCoordInClose(Coord coord, Map<Long, Node> closeMap) {
        if (coord == null || closeMap.isEmpty()) {
            return false;
        }
        return closeMap.containsKey(getCloseKey(coord));
    }

    /**
     * 生成closeMap的Key
     * tips：为了提高效率，将x、y拼接为long作为key
     */
    private static long getCloseKey(Coord coord) {
        return (long) coord.x << 32 | coord.y;
    }


    /**
     * 计算H值 (“曼哈顿距离”，坐标分别取差值相加)
     */
    private static int calcH(Coord end, Coord coord) {
        return DIRECT_VALUE * (Math.abs(end.x - coord.x) + Math.abs(end.y - coord.y));
    }

    /**
     * 从OpenList中查找节点
     */
    private static Node findNodeInOpen(Coord coord, Queue<Node> openList) {
        if (coord == null || openList.isEmpty()) {
            return null;
        }
        for (Node node : openList) {
            if (node.coord.equals(coord)) {
                return node;
            }
        }
        return null;
    }


    /**
     * 回溯法绘制路径
     */
    private static List<Node> drawPath(byte[][] maps, Node last) {
        if (last == null || maps == null) {
            return new ArrayList<>();
        }
//        System.out.println("\n总代价：" + end.G);
        List<Node> nodeList = new ArrayList<>();
        while (last != null) {
            nodeList.add(0, last);
            last = last.parent;
        }
        return nodeList;
    }

}
