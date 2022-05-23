/**
 * 地图信息
 * @author wushu
 * @create 2021-07-28 14:50
 */
public class MapInfo {
    /**二维数组的地图*/
    public byte[][] maps;
    /**地图的宽*/
    public int width;
    /**地图的高*/
    public int hight;
    /**起始节点*/
    public Node start;
    /**最终节点*/
    public Node end;

    public MapInfo(byte[][] maps, Node start, Node end) {
        this.maps = maps;
        this.width = maps.length;
        this.hight = maps[0].length;
        this.start = start;
        this.end = end;
    }
}
