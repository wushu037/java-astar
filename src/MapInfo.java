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
    /**起始结点*/
    public Node start;
    /**最终结点*/
    public Node end;

    public MapInfo(byte[][] maps, Node start, Node end) {
        this.maps = maps;
        this.width = maps[0].length;
        this.hight = maps.length;
        this.start = start;
        this.end = end;
    }
}
