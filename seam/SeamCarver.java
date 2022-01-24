import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {

    private int height;
    private int width;
    private double[][] energy;
    private int[][] color;
    private boolean transposed;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("imput picture can't be null");
        this.height = picture.height();
        this.width = picture.width();
        this.energy = new double[height][width];
        this.color = new int[height][width];
        this.transposed = false;
        populateArrayWithPixels(picture);
        calculateEnergy();
    }

    // populates the color array
    private void populateArrayWithPixels(Picture picture) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                color[i][j] = picture.get(j, i).getRGB();
            }
        }
    }

    // calculates the energy
    private void calculateEnergy() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (row == 0 || row == height - 1 || col == 0 || col == width - 1) {
                    energy[row][col] = 1000;
                    continue;
                }
                energy[row][col] = getPixelEnergy(row, col);
            }
        }
    }

    // prepares pixel values for calculation
    private double getPixelEnergy(int row, int col) {
        Color left = new Color(color[row - 1][col]);
        Color right = new Color(color[row + 1][col]);
        Color up = new Color(color[row][col - 1]);
        Color down = new Color(color[row][col + 1]);
        return Math.sqrt(calculatePixelEnergy(left, right) + calculatePixelEnergy(up, down));
    }

    // calculates energy for pixel
    private double calculatePixelEnergy(Color x, Color y) {
        return Math.pow(x.getRed() - y.getRed(), 2) +
                Math.pow(x.getGreen() - y.getGreen(), 2) +
                Math.pow(x.getBlue() - y.getBlue(), 2);
    }

    // current picture
    public Picture picture() {
        if (transposed) {
            transponse();
        }
        Picture picture = new Picture(width(), height());
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                picture.set(j, i, new Color(color[i][j]));
            }
        }
        return picture;
    }

    // width of current picture
    public int width() {
        return (transposed) ? height : width;
    }

    // height of current picture
    public int height() {
        return (transposed) ? width : height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (transposed) {
            checkEnergyRowCol(y, x);
            return energy[x][y];
        }
        else {
            checkEnergyRowCol(x, y);
            return energy[y][x];
        }
    }

    // checks row and col for energy row
    private void checkEnergyRowCol(int x, int y) {
        if (x < 0 || y < 0 || x > width - 1 || y > height - 1) {
            throw new IllegalArgumentException("invalid x and y");
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (!transposed) {
            transponse();
        }
        return findSeam();
    }

    // transposes the arrays
    private void transponse() {
        int temp = width;
        width = height;
        height = temp;
        int[][] tempC = new int[height][width];
        double[][] tempE = new double[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                tempC[row][col] = color[col][row];
                tempE[row][col] = energy[col][row];
            }
        }
        this.energy = tempE;
        this.color = tempC;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (transposed) {
            transponse();
        }
        return findSeam();
    }

    // finds seam
    private int[] findSeam() {
        int[][] edgeTo = new int[height][width];
        double[][] distTo = new double[height][width];
        for (int row = 0; row < height; row++) {
            Arrays.fill(distTo[row], Double.POSITIVE_INFINITY);
        }
        Arrays.fill(distTo[0], 0);
        for (int row = 0; row < height - 1; row++) {
            for (int col = 0; col < width; col++) {
                relax(edgeTo, distTo, row, col);
            }
        }
        int lastRowMinCol = getLastRowMin(distTo[height - 1], edgeTo);
        Stack<Integer> seamStack = getSeam(edgeTo, lastRowMinCol);
        int[] seam = new int[height];
        int counter = 0;
        while (!seamStack.isEmpty()) {
            seam[counter++] = seamStack.pop();
        }
        return seam;
    }

    // returns seam
    private Stack<Integer> getSeam(int[][] edgeTo, int lastRowMinCol) {
        Stack<Integer> seam = new Stack<>();
        seam.push(lastRowMinCol);
        for (int row = height - 1; row > 0; row--) {
            lastRowMinCol = edgeTo[row][lastRowMinCol];
            seam.push(lastRowMinCol);
        }
        return seam;
    }

    // returns last row minimum
    private int getLastRowMin(double[] lastRow, int[][] edgeTo) {
        double minEnergy = Double.POSITIVE_INFINITY;
        int minEnergyCol = 0;
        for (int col = 0; col < width; col++) {
            if (minEnergy > lastRow[col]) {
                minEnergy = lastRow[col];
                minEnergyCol = edgeTo[height - 1][col];
            }
        }
        return minEnergyCol;
    }

    // relaxes an edge
    private void relax(int[][] edgeTo, double[][] distTo, int row, int col) {
        for (int i = -1; i < 2; i++) {
            if (checkRowCol(row + 1, col + i)
                    && distTo[row + 1][col + i] > distTo[row][col] + energy[row + 1][col + i]) {
                distTo[row + 1][col + i] = distTo[row][col] + energy[row + 1][col + i];
                edgeTo[row + 1][col + i] = col;
            }
        }
    }

    // checks row col
    private boolean checkRowCol(int row, int col) {
        return (row >= 0 && row < height && col >= 0 && col < width);
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (height <= 1) throw new IllegalArgumentException(
                "can't remove horizontal seam because height is low");
        if (!transposed) {
            transponse();
        }
        removeVerticalSeam(seam);
        calculateEnergy();
        transponse();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        checkInputSeam(seam);
        if (width <= 1)
            throw new IllegalArgumentException("can't remove vertical seam because witdh is low");
        if (transposed) {
            transponse();
        }
        removeFromArrays(seam);
        calculateEnergy();
    }

    // removes seam from array
    private void removeFromArrays(int[] seam) {
        for (int row = 0; row < height; row++) {
            int[] newColorRow = new int[width - 1];
            System.arraycopy(color[row], 0, newColorRow, 0, seam[row]);
            System.arraycopy(color[row], seam[row] + 1, newColorRow, seam[row],
                             width - seam[row] - 1);
            color[row] = newColorRow;
            double[] newEnergyRow = new double[width - 1];
            System.arraycopy(energy[row], 0, newEnergyRow, 0, seam[row]);
            System.arraycopy(energy[row], seam[row] + 1, newEnergyRow, seam[row],
                             width - seam[row] - 1);
            energy[row] = newEnergyRow;
        }
        width--;
    }

    // checks input seam
    private void checkInputSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException("seam can't be null");
        if (seam.length == 0) throw new IllegalArgumentException("seam array can not be empty");
        if (seam.length != height) throw new IllegalArgumentException("too many seams");
        for (int i = 0; i < seam.length - 1; i++) {
            if (seam[i + 1] - seam[i] > 1 || seam[i + 1] - seam[i] < -1)
                throw new IllegalArgumentException("seam entries can not differ more than 1");
        }
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width)
                throw new IllegalArgumentException("seam entry outside range");
        }
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture picture = new Picture("10x10.png");
        System.out.println("Height: " + picture.height() + " Width: " + picture.width());
        SeamCarver carver = new SeamCarver(picture);
        int[] seam = new int[] { 7, 6, 5, 5, 6, 7, 6, 7, 8, 8, 7, 7 };
        carver.removeVerticalSeam(seam);
        System.out.println("Height: " + carver.height() + " Width: " + carver.width());
    }
}
