import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // 创建一个xlen*ylen的地图数据
        int xlen = 20;
        int ylen = 30;
        /**
         * mapInfo二维数组的下标对应笛卡尔坐标系上的数字。访问时实际坐标是多少就写多少(向下取整)。例如：
         * - 访问坐标(3,2)       ->   mapInfo[3][2]
         * - 访问坐标(3.2，2.99)  ->   mapInfo[3][2]
         * 实际地图的坐标范围是左闭右开区间: x:[0,xlen) y:[0,ylen)
         */
        byte[][] mapInfo = new byte[xlen+1][ylen+1];
        // 添加障碍物
        addObstacle(mapInfo);

        long begin = System.currentTimeMillis();
        System.out.println("begin:" + begin);
        List<Node> path = new ArrayList<>();

        int time = 10;
        for (int i = 0; i < time; i++) {
            path = AStarService.searchWay(1, 1, xlen, ylen, mapInfo);
            if (path == null || path.isEmpty()){
                System.err.println("fail");
                return;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("end:" + end);
        System.out.println(String.format("%d*%d地图，%d次耗时：%d ms", xlen, ylen, time, end - begin));

        // 地图和路径打印
        byte[][] draw = new byte[xlen+1][ylen+1];
        for (int i = 0; i < mapInfo.length; i++) {
            draw[i] = Arrays.copyOf(mapInfo[i],mapInfo[i].length);
        }
        for (Node node : path) {
            draw[node.coord.x][node.coord.y] = -1;
        }
        // 先打印x再打印y，保证打印的地图图像的正方向是90°(y轴)
        for (int y = 0; y < draw[0].length; y++) {
            for (int x = 0; x < draw.length; x++) {
                byte v = draw[x][y];
                if (v == 1) { // 障碍物
                    System.out.print("\033[30;4m〇\033[0m");
                } else if (v == -1) {
                    System.out.print("\033[31;4m〇\033[0m");
                }else{
                    System.out.print("〇");
                }
            }
            System.out.println();
        }
    }

    // 该障碍物可观察到"穿越墙角"的情况
    private static void addObstacle(byte[][] mapInfo) {
        mapInfo[3][1] = 1;
        mapInfo[3][2] = 1;
        mapInfo[2][2] = 1;
        mapInfo[1][2] = 1;
    }

    // 以(1,1)做为起点，该障碍物可观察到"穿越墙角"的情况
    private static void addObstacle1(byte[][] mapInfo) {
        mapInfo[0][3] = 1;
        mapInfo[1][2] = 1;
        mapInfo[3][0] = 1;
        mapInfo[2][1] = 1;
    }
}
