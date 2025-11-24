package com.example.ordenamientos;

import java.util.Arrays;

// Esta clase se encarga de contener y replicar varios algoritmos de ordenamiento
public class MetodosDeOrdenamiento {

    // | Quicksort |
    // Metodo que inicia el ordenamiento Quicksort
    public static void quicksort(double[] arreglo) {
        if (arreglo == null || arreglo.length == 0) return;
        quicksort(arreglo, 0, arreglo.length - 1);
    }
    // Metodo recursivo que implementa la logica de Quicksort
    private static void quicksort(double[] arreglo, int izquierda, int derecha) {
        if (izquierda >= derecha) return;

        double pivote = arreglo[(izquierda + derecha) / 2];
        int i = izquierda, j = derecha;

        while (i <= j) {
            while (arreglo[i] < pivote) i++;
            while (arreglo[j] > pivote) j--;
            if (i <= j) {
                double temporal = arreglo[i];
                arreglo[i] = arreglo[j];
                arreglo[j] = temporal;
                i++;
                j--;
            }
        }

        if (izquierda < j) quicksort(arreglo, izquierda, j);
        if (i < derecha) quicksort(arreglo, i, derecha);
    }


    // | Mergesort |
    // Este metodo es el que inicia el ordenamiento Mergesort
    public static void mergesort(double[] arreglo) {
        if (arreglo == null || arreglo.length < 2) return;
        double[] temporal = new double[arreglo.length];
        mergesort(arreglo, temporal, 0, arreglo.length - 1);
    }
    // Metodo que se encarga de dividir el arreglo para Mergesort
    private static void mergesort(double[] arreglo, double[] temporal, int izquierda, int derecha) {
        if (izquierda < derecha) {
            int medio = (izquierda + derecha) / 2;
            mergesort(arreglo, temporal, izquierda, medio);
            mergesort(arreglo, temporal, medio + 1, derecha);
            fusion(arreglo, temporal, izquierda, medio, derecha);
        }
    }
    // Metodo que fusiona dos sub-arreglos ordenados
    private static void fusion(double[] arreglo, double[] temporal, int izquierda, int medio, int derecha) {
        for (int i = izquierda; i <= derecha; i++) {
            temporal[i] = arreglo[i];
        }

        int i = izquierda;
        int j = medio + 1;
        int k = izquierda;

        while (i <= medio && j <= derecha) {
            if (temporal[i] <= temporal[j]) {
                arreglo[k++] = temporal[i++];
            } else {
                arreglo[k++] = temporal[j++];
            }
        }

        while (i <= medio) {
            arreglo[k++] = temporal[i++];
        }
    }

    // | Shell Sort |
    // Se encarga de ordenar un arreglo usando el algoritmo Shell Sort
    public static void shellSort(double[] arreglo) {
        int n = arreglo.length;
        int salto = 1;
        while (salto < n / 3) {
            salto = 3 * salto + 1;
        }

        while (salto >= 1) {
            for (int i = salto; i < n; i++) {
                double temporal = arreglo[i];
                int j;
                for (j = i; j >= salto && arreglo[j - salto] > temporal; j -= salto) {
                    arreglo[j] = arreglo[j - salto];
                }
                arreglo[j] = temporal;
            }
            salto = salto / 3;
        }
    }

    // | Seleccion Directa |
    // Metodo el cual ordena un arreglo usando el algoritmo de Seleccion Directa
    public static void seleccionDirecta(double[] arreglo) {
        int n = arreglo.length;
        for (int i = 0; i < n - 1; i++) {
            int indiceMinimo = i;
            for (int j = i + 1; j < n; j++) {
                if (arreglo[j] < arreglo[indiceMinimo]) {
                    indiceMinimo = j;
                }
            }
            double temporal = arreglo[indiceMinimo];
            arreglo[indiceMinimo] = arreglo[i];
            arreglo[i] = temporal;
        }
    }

    // | Radix Sort |
    // Metodo el cual inicia el ordenamiento Radix Sort
    public static void radixSort(long[] arreglo) {
        if (arreglo.length == 0) return;

        long[] negativos = Arrays.stream(arreglo).filter(x -> x < 0).toArray();
        long[] positivos = Arrays.stream(arreglo).filter(x -> x >= 0).toArray();

        radixSortPositivos(positivos);

        for (int i = 0; i < negativos.length; i++) {
            negativos[i] = Math.abs(negativos[i]);
        }
        radixSortPositivos(negativos);

        int idx = 0;
        for (int i = negativos.length - 1; i >= 0; i--) {
            arreglo[idx++] = -negativos[i];
        }
        for (long positivo : positivos) {
            arreglo[idx++] = positivo;
        }
    }
    // Este metodo pertenece al Radix Sort pero solo opera sobre numeros positivos.
    private static void radixSortPositivos(long[] arreglo) {
        if (arreglo.length == 0) return;

        long maximo = 0;
        try {
            maximo = Arrays.stream(arreglo).max().getAsLong();
        } catch (Exception e) {
            return;
        }

        for (long exponente = 1; maximo / exponente > 0; exponente *= 10) {
            countSort(arreglo, exponente);
        }
    }

    // Metodo que es usado por Radix Sort y sirve para ordenar por un digito
    private static void countSort(long[] arreglo, long exponente) {
        int n = arreglo.length;
        long[] salida = new long[n];
        int[] conteo = new int[10];
        Arrays.fill(conteo, 0);

        for (int i = 0; i < n; i++) {
            conteo[(int)((arreglo[i] / exponente) % 10)]++;
        }

        for (int i = 1; i < 10; i++) {
            conteo[i] += conteo[i - 1];
        }

        for (int i = n - 1; i >= 0; i--) {
            int indiceDigito = (int)((arreglo[i] / exponente) % 10);
            salida[conteo[indiceDigito] - 1] = arreglo[i];
            conteo[indiceDigito]--;
        }

        System.arraycopy(salida, 0, arreglo, 0, n);
    }
}