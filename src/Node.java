/**
 * 路径节点
 * @author wushu
 * @create 2021-07-28 14:51
 */
public class Node implements Comparable<Node> {
    /**坐标*/
    public Coord coord;
    /**父节点*/
    public Node parent;
    /**起点到当前节点的代价，是一个准确的值*/
    public int G;
    /**当前节点到终点的估算代价，是一个估值*/
    public int H;

    public Node(int x, int y) {
        this.coord = new Coord(x, y);
    }

    public Node(Coord coord, Node parent, int g, int h) {
        this.coord = coord;
        this.parent = parent;
        G = g;
        H = h;
    }


    /**
     * 最优节点(F值最小的节点，F=G+H)排在前面
     * @param node
     * @return
     */
    @Override
    public int compareTo(Node node) {
        if (node == null) {
            return -1;
        }
        if (this.G + this.H > node.G + node.H) {
            return 1;
        } else if (this.G + this.H < node.G + node.H) {
            return -1;
        }
        // 当有相同F值的节点存在时。此处返回>0、<0得到的路线并不相同(但也差不了多少)
        // 此时不同的返回决定了优先使用后加入的节点还是先加入的节点，是否这样做取决于你自己的偏好
        // 实际上如果终点是可达的，无论你返回什么，都不影响是否能寻到路
        return 1;
    }

    @Override
    public String toString() {
        return String.format("[x=%s y=%s]", coord.x, coord.y);
    }
}

