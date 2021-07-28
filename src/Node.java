/**
 * 路径节点
 * @author wushu
 * @create 2021-07-28 14:51
 */
public class Node implements Comparable<Node> {
    /**坐标*/
    public Coord coord;
    /**父结点*/
    public Node parent;
    /**是个准确的值，是起点到当前结点的代价*/
    public int G;
    /**是个估值，当前结点到目的结点的估计代价*/
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
     * 最优结点排在前面
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
        return 0;
    }

    @Override
    public String toString() {
        return String.format("[x=%s y=%s]", coord.x, coord.y);
    }
}

