import java.util.*;

/**
 * 寻路算法
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
        Map<String, Node> closeMap = new HashMap<>();
        Node start = new Node(startX, startY);
        Node end = new Node(endX, endY);
        MapInfo mapInfo = new MapInfo(maps, start, end);
        List<Node> nodeList = searchWay(mapInfo, openList, closeMap);

        if (nodeList != null && !nodeList.isEmpty()) {
            // 删除起点(起点的下个点作为寻路的第一个点)
            nodeList.remove(0);
        }
        return nodeList;
    }

    /**
     * 开始算法，循环移动结点寻找路径，设定循环结束条件，Open表为空或者最终结点在Close表
     */
    private static List<Node> searchWay(MapInfo mapInfo, Queue<Node> openList, Map<String, Node> closeMap) {
        if (mapInfo.maps[mapInfo.start.coord.x][mapInfo.start.coord.y] == BAR ||
                mapInfo.maps[mapInfo.end.coord.x][mapInfo.end.coord.y] == BAR) {
            return new ArrayList<>();
        }
        if (mapInfo.start.coord == mapInfo.end.coord) {
            return new ArrayList<>();
        }
        openList.clear();
        closeMap.clear();
        // 将起点放入open列表
        openList.add(mapInfo.start);
        // 寻路
        List<Node> nodeList = moveNodes(mapInfo, openList, closeMap);
        return nodeList;
    }

    /**
     * 移动当前结点
     */
    private static List<Node> moveNodes(MapInfo mapInfo, Queue<Node> openList, Map<String, Node> closeMap) {
        List<Node> nodeList = new ArrayList<>();
        // 如果open列表不为空，则循环执行
        while (!openList.isEmpty()) {
            // 如果终点在close列表，说明已经找到了终点，则绘制路径
            if (isCoordInClose(mapInfo.end.coord, closeMap)) {
                // 回溯法绘制路径
                nodeList = drawPath(mapInfo.maps, mapInfo.end);
                break;
            }
            // 从open列表删除一个最优节点，并把这个节点加入到close列表
            Node current = openList.poll();
            closeMap.put(getCloseKey(current.coord), current);
            // 添加所有邻结点到open表
            addNeighborNodeInOpen(mapInfo, current, openList, closeMap);
        }
        return nodeList;
    }

    /**
     * 添加所有邻结点到open表
     */
    private static void addNeighborNodeInOpen(MapInfo mapInfo, Node current, Queue<Node> openList, Map<String, Node> closeMap) {
        int x = current.coord.x;
        int y = current.coord.y;
        // 左
        addNeighborNodeInOpen(mapInfo, current, x - 1, y, DIRECT_VALUE, openList, closeMap);
        // 上
        addNeighborNodeInOpen(mapInfo, current, x, y - 1, DIRECT_VALUE, openList, closeMap);
        // 右
        addNeighborNodeInOpen(mapInfo, current, x + 1, y, DIRECT_VALUE, openList, closeMap);
        // 下
        addNeighborNodeInOpen(mapInfo, current, x, y + 1, DIRECT_VALUE, openList, closeMap);
        // 右上
        addNeighborNodeInOpen(mapInfo, current, x - 1, y - 1, OBLIQUE_VALUE, openList, closeMap);
        // 右下
        addNeighborNodeInOpen(mapInfo, current, x + 1, y - 1, OBLIQUE_VALUE, openList, closeMap);
        // 左下
        addNeighborNodeInOpen(mapInfo, current, x + 1, y + 1, OBLIQUE_VALUE, openList, closeMap);
        // 左上
        addNeighborNodeInOpen(mapInfo, current, x - 1, y + 1, OBLIQUE_VALUE, openList, closeMap);
        // 最优结点排在前面
//        sort(openList);
    }


    /**
     * 添加一个邻结点到open表
     */
    private static void addNeighborNodeInOpen(MapInfo mapInfo, Node current, int x, int y, int value, Queue<Node> openList, Map<String, Node> closeMap) {
        // 如果结点能加入Open列表，则加入open列表
        if (canAddNodeToOpen(mapInfo, x, y, closeMap)) {
            Coord coord = new Coord(x, y);
            // 计算邻结点的G值
            int G = current.G + value;
            // Open列表中查找该结点
            Node child = findNodeInOpen(coord, openList);
            if (child == null) { // 如果结点不在open列表中，则创建结点并添加
                Node end = mapInfo.end;
                // 计算H值
                int H = calcH(end.coord, coord);
                // 如果是终点
                if (end.coord.equals(coord)) {
                    child = end;
                    child.parent = current;
                    child.G = G;
                    child.H = H;
                } else {
                    child = new Node(coord, current, G, H);
                }
                openList.add(child);
            } else if (child.G > G) { //如果在open中，判断两个节点的G值。如果原先的G值比现在大，则替换为小的
                child.G = G;
                child.parent = current;
                openList.add(child);
            }
        }
    }

    /**
     * 判断结点能否放入Open列表
     * 不能加入的情况：1.超出地图范围; 2.结点是障碍; 3.结点已经被访问过,即在close列表中存在;
     */
    private static boolean canAddNodeToOpen(MapInfo mapInfo, int x, int y, Map<String, Node> closeMap) {
        // 如果不在地图中，则不能加入open列表
        if (x < 0 || x >= mapInfo.width || y < 0 || y >= mapInfo.hight) {
            return false;
        }
        // 如果结点是障碍，则不能加入open列表
        if (mapInfo.maps[x][y] == BAR) {
            return false;
        }
        // 如果结点在close表，则不能加入open列表
        if (isCoordInClose(new Coord(x, y), closeMap)) {
            return false;
        }
        return true;
    }

    /**
     * 判断坐标是否在close表中
     */
    private static boolean isCoordInClose(Coord coord, Map<String, Node> closeMap) {
        if (coord == null || closeMap.isEmpty()) {
            return false;
        }
        return closeMap.containsKey(getCloseKey(coord));
    }

    /**
     * 生成closeMap的Key
     * 在40*40无障碍地图中，10w次最大耗时，Integer的key比String的key少2秒。
     * 但是找不出怎么将两个有序数字转为固定且唯一的数字
     */
    private static String getCloseKey(Coord coord) {
        return coord.x + "," + coord.y;
    }


    /**
     * 计算H值，“曼哈顿” 法，坐标分别取差值相加
     */
    private static int calcH(Coord end, Coord coord) {
        return DIRECT_VALUE * (Math.abs(end.x - coord.x) + Math.abs(end.y - coord.y));
    }

    /**
     * 从Open列表中查找结点
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
    private static List<Node> drawPath(byte[][] maps, Node end) {
        if (end == null || maps == null) {
            return new ArrayList<>();
        }
//        log.info("\n总代价：" + end.G);
        List<Node> nodeList = new ArrayList<>();
        while (end != null) {
            Coord c = end.coord;
            nodeList.add(0, end);
            end = end.parent;
        }
        return nodeList;
    }

}
