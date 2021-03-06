package com.example.damian.fouriertransform;

/**
 * Created by Damian on 2018-03-20.
 */

public class FFT {

    // Do not instantiate.
    private FFT() {
    }

    /**
     * Returns spectrum of the signal.
     *
     * @param inSignal the int array
     * @return the FFT of the complex array {@code x}
     */
    public static float[] fft(final float[] inSignal) {
        // - n is the dimension of the problem
        // - nu is its logarithm in base e
        int n = inSignal.length;

        // If n is a power of 2, then ld is an integer (_without_ decimals)
        double ld = Math.log(n) / Math.log(2.0);

        // Here I check if n is a power of 2. If exist decimals in ld, I quit
        // from the function returning null.
        if (((int) ld) - ld != 0) {
            System.out.println("The number of elements is not a power of 2.");
            return null;
        }

        // Declaration and initialization of the variables
        // ld should be an integer, actually, so I don't lose any information in
        // the cast
        int nu = (int) ld;
        int n2 = n / 2;
        int nu1 = nu - 1;
        float[] xReal = new float[n];
        float[] xImag = new float[n];
        float tReal, tImag, p, arg, c, s;

        // Here I check if I'm going to do the direct transform or the inverse
        // transform.
        float constant;
            constant =  -2 * (float) Math.PI;

        // I don't want to overwrite the input arrays, so here I copy them. This
        // choice adds \Theta(2n) to the complexity.
        for (int i = 0; i < n; i++) {
            xReal[i] = inSignal[i];
        }

        // First phase - calculation
        int k = 0;
        for (int l = 1; l <= nu; l++) {
            while (k < n) {
                for (int i = 1; i <= n2; i++) {
                    p = bitreverseReference(k >> nu1, nu);
                    // direct FFT or inverse FFT
                    arg = constant * p / n;
                    c = (float) Math.cos(arg);
                    s = (float) Math.sin(arg);
                    tReal = xReal[k + n2] * c + xImag[k + n2] * s;
                    tImag = xImag[k + n2] * c - xReal[k + n2] * s;
                    xReal[k + n2] = xReal[k] - tReal;
                    xImag[k + n2] = xImag[k] - tImag;
                    xReal[k] += tReal;
                    xImag[k] += tImag;
                    k++;
                }
                k += n2;
            }
            k = 0;
            nu1--;
            n2 /= 2;
        }

        // Second phase - recombination
        k = 0;
        int r;
        while (k < n) {
            r = bitreverseReference(k, nu);
            if (r > k) {
                tReal = xReal[k];
                tImag = xImag[k];
                xReal[k] = xReal[r];
                xImag[k] = xImag[r];
                xReal[r] = tReal;
                xImag[r] = tImag;
            }
            k++;
        }

        // Here I have to mix xReal and xImag to have an array (yes, it should
        // be possible to do this stuff in the earlier parts of the code, but
        // it's here to readibility).
        float[] newArray = new float[xReal.length * 2];
        float radice = 1 / (float) Math.sqrt(n);
        for (int i = 0; i < newArray.length; i += 2) {
            int i2 = i / 2;
            // I used Stephen Wolfram's Mathematica as a reference so I'm going
            // to normalize the output while I'm copying the elements.
            newArray[i] = xReal[i2] * radice;
            newArray[i + 1] = xImag[i2] * radice;
        }
        return newArray;
    }

    /**
     * The reference bitreverse function.
     */
    private static int bitreverseReference(int j, int nu) {
        int j2;
        int j1 = j;
        int k = 0;
        for (int i = 1; i <= nu; i++) {
            j2 = j1 / 2;
            k = 2 * k + j1 - 2 * j2;
            j1 = j2;
        }
        return k;
    }
}
