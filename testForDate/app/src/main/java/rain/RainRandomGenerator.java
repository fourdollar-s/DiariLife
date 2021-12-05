package rain;

import java.util.Random;

public class RainRandomGenerator {
    private final Random RANDOM = new Random();

    // 區間隨機
    public float getRandom(float lower, float upper) {
        float min = Math.min(lower, upper);
        float max = Math.max(lower, upper);
        return getRandom(max - min) + min;
    }

    // 上界隨機
    public float getRandom(float upper) {
        return RANDOM.nextFloat() * upper;
    }

    // 上界隨機
    public int getRandom(int upper) {
        return RANDOM.nextInt(upper);
    }

    /**
     * 獲得一個給定範圍的隨機整數
     * 可以負數到正數
     * @param smallistNum
     * @param BiggestNum
     * @return
     */
    public int getRandomNum(int smallistNum, int BiggestNum) {
        Random random = new Random();
        return (Math.abs(random.nextInt()) % (BiggestNum - smallistNum + 1))+ smallistNum;
    }

    //隨機產生劃線的起始點座標和結束點座標
    public int[] getLine(int height, int width) {
        int[] tempCheckNum = { 0, 0, 0, 0 };
        int temp = getRandomWidth(width);
        for (int i = 0; i < 4; i += 4) {
            tempCheckNum[i] = temp;
            tempCheckNum[i + 1] = (int) (Math.random() * height/4);
            tempCheckNum[i + 2] = temp;
            tempCheckNum[i + 3] = (int) (Math.random() * height/2);
        }
        return tempCheckNum;
    }

    public int getRandomWidth(int width){
        return (int) (Math.random() * width);
    }
}
