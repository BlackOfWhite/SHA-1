import java.util.ArrayList;
import java.util.List;

/**
 * Created by niewinskip on 2016-10-01.
 */
class DataBlocksCollection {

    List<char[]> dataBlocks;
    int blockSize;
    String binaryData;
    int numberOfBlocks;

    public DataBlocksCollection(int blockSize, String binaryData) {
        this.dataBlocks = new ArrayList<>();
        this.blockSize = blockSize;
        this.binaryData = binaryData;
        this.numberOfBlocks = binaryData.length() / blockSize + 1;
        boolean flag = false;

        int index = 0;
        for (int x = 0; x < numberOfBlocks; x++) {
            char tab[] = new char[blockSize];
            for (int i = 0; i < this.blockSize; i++) {
                if (index < binaryData.length()) {
                    tab[i] = binaryData.charAt(index);
                } else {
                    if (!flag) {
                        tab[i] = '1';
                        flag = true;
                    } else {
                        tab[i] = '0';
                    }
                }
                index++;
            }
            if (x + 1 == numberOfBlocks) {
                tab = appendBinaryDataLength(tab, binaryData.length());
                System.out.println("\nLast block finished with: " + tab.length + " characters\n");
            }
            this.dataBlocks.add(tab);
        }
    }

    private char[] appendBinaryDataLength(char tab[], int msgLength) {
        int x = tab.length - 1; // 511
        while (msgLength > 0) {
            if (msgLength % 2 == 1) {
                tab[x] = '1';
            }
            x--;
            msgLength /= 2;
        }
        return tab;
    }

    public void printData() {
        int nr = 0;
        int totalLength = 0;
        for (char[] dataBlock : dataBlocks) {
           nr++;
            System.out.println("Chunk " + nr);
            for (int x = 0; x < blockSize; x++) {
                totalLength++;
                if (x > 0 && x % 8 == 0) {
                    System.out.print(" ");
                }
                System.out.print(dataBlock[x]);
            }
            System.out.println();
        }
        System.out.println("Total length: " + totalLength);
    }

    public List<char[]> getDataBlocksCollection() {
        return dataBlocks;
    }
}
