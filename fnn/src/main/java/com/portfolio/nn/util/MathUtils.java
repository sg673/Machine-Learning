package com.portfolio.nn.util;

public class MathUtils {

  public static double[][] matrixMultiply(double[][] a, double[][] b) {
    int n = Math.max(Math.max(a.length, a[0].length), b[0].length);
    if (n <= 64)
      return standardMultiply(a, b);

    int size = 1;
    while (size < n)
      size *= 2;

    double[][] aPadded = padMatrix(a, size);
    double[][] bPadded = padMatrix(b, size);
    double[][] result = strassen(aPadded, bPadded);

    return extractResult(result, a.length, b[0].length);
  }

  // https://en.wikipedia.org/wiki/Strassen_algorithm
  private static double[][] strassen(double[][] a, double[][] b) {
    int n = a.length;
    if (n <= 64)
      return standardMultiply(a, b);

    int half = n / 2;
    double[][] a11 = new double[half][half], a12 = new double[half][half];
    double[][] a21 = new double[half][half], a22 = new double[half][half];
    double[][] b11 = new double[half][half], b12 = new double[half][half];
    double[][] b21 = new double[half][half], b22 = new double[half][half];

    split(a, a11, 0, 0);
    split(a, a12, 0, half);
    split(a, a21, half, 0);
    split(a, a22, half, half);
    split(b, b11, 0, 0);
    split(b, b12, 0, half);
    split(b, b21, half, 0);
    split(b, b22, half, half);

    double[][] m1 = strassen(add(a11, a22), add(b11, b22));
    double[][] m2 = strassen(add(a21, a22), b11);
    double[][] m3 = strassen(a11, subtract(b12, b22));
    double[][] m4 = strassen(a22, subtract(b21, b11));
    double[][] m5 = strassen(add(a11, a12), b22);
    double[][] m6 = strassen(subtract(a21, a11), add(b11, b12));
    double[][] m7 = strassen(subtract(a12, a22), add(b21, b22));

    double[][] c11 = add(subtract(add(m1, m4), m5), m7);
    double[][] c12 = add(m3, m5);
    double[][] c21 = add(m2, m4);
    double[][] c22 = add(subtract(add(m1, m3), m2), m6);

    double[][] result = new double[n][n];
    join(c11, result, 0, 0);
    join(c12, result, 0, half);
    join(c21, result, half, 0);
    join(c22, result, half, half);

    return result;
  }

  private static double[][] standardMultiply(double[][] a, double[][] b) {
    int aRows = a.length, aCols = a[0].length, bCols = b[0].length;
    double[][] result = new double[aRows][bCols];
    for (int i = 0; i < aRows; i++)
      for (int j = 0; j < bCols; j++)
        for (int k = 0; k < aCols; k++)
          result[i][j] += a[i][k] * b[k][j];
    return result;
  }

  private static double[][] add(double[][] a, double[][] b) {
    int n = a.length;
    double[][] result = new double[n][n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        result[i][j] = a[i][j] + b[i][j];
    return result;
  }

  private static double[][] subtract(double[][] a, double[][] b) {
    int n = a.length;
    double[][] result = new double[n][n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        result[i][j] = a[i][j] - b[i][j];
    return result;
  }

  private static void split(double[][] parent, double[][] child, int iB, int jB) {
    for (int i = 0; i < child.length; i++)
      for (int j = 0; j < child.length; j++)
        child[i][j] = parent[i + iB][j + jB];
  }

  private static void join(double[][] child, double[][] parent, int iB, int jB) {
    for (int i = 0; i < child.length; i++)
      for (int j = 0; j < child.length; j++)
        parent[i + iB][j + jB] = child[i][j];
  }

  private static double[][] padMatrix(double[][] matrix, int size) {
    double[][] padded = new double[size][size];
    for (int i = 0; i < matrix.length; i++)
      System.arraycopy(matrix[i], 0, padded[i], 0, matrix[i].length);
    return padded;
  }

  private static double[][] extractResult(double[][] matrix, int rows, int cols) {
    double[][] result = new double[rows][cols];
    for (int i = 0; i < rows; i++)
      System.arraycopy(matrix[i], 0, result[i], 0, cols);
    return result;
  }
}
