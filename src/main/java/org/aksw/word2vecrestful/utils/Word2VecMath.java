package org.aksw.word2vecrestful.utils;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public class Word2VecMath {


    public synchronized static float[] sub(final float[] vectorA, final float[] vectorB) {
        if (vectorA.length == vectorB.length) {
            final float[] add = new float[vectorA.length];
            for (int i = 0; i < vectorA.length; i++) {
                add[i] = vectorA[i] - vectorB[i];
            }
            return add;
        }
        return null;
    }

    public synchronized static float[] add(final float[] vectorA, final float[] vectorB) {
        if (vectorA.length == vectorB.length) {
            final float[] add = new float[vectorA.length];
            for (int i = 0; i < vectorA.length; i++) {
                add[i] = vectorA[i] + vectorB[i];
            }
            return add;
        }
        return null;
    }

    public synchronized static double cosineSimilarity(final float[] vectorA, final float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public synchronized static double cosineSimilarityNormalizedVecs(final float[] vectorA, final float[] vectorB) {
        double c = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            c += vectorA[i] * vectorB[i];
        }
        return c;
    }

    public synchronized static double norm(final float[] vectorA) {
        double normA = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            normA += vectorA[i] * vectorA[i];
        }
        return Math.sqrt(normA);
    }

    public synchronized static float[] normalize(final float[] vectorA) {
        final Double normA = norm(vectorA);
        for (int i = 0; i < vectorA.length; i++) {
            vectorA[i] /= normA.floatValue();
        }
        return vectorA;
    }

    // Adding maxval and minval methods
    // Method for getting the maximum value
    public static float getMax(float[] inputArray) {
        float maxValue = inputArray[0];
        for (int i = 1; i < inputArray.length; i++) {
            if (inputArray[i] > maxValue) {
                maxValue = inputArray[i];
            }
        }
        return maxValue;
    }

    // Method for getting the minimum value
    public static float getMin(float[] inputArray) {
        float minValue = inputArray[0];
        for (int i = 1; i < inputArray.length; i++) {
            if (inputArray[i] < minValue) {
                minValue = inputArray[i];
            }
        }
        return minValue;
    }

    // Method to calculate mean
    public static float calcMean(float[] inpArr) {
        float res = 0;
        for (int i = 0; i < inpArr.length; i++) {
            res += inpArr[i];
        }
        res = res / inpArr.length;
        return res;
    }

    // Method to calculate variance
    public static float calcVariance(float[] inpArr) {
        float res = 0;
        float meanVal = calcMean(inpArr);
        float len = inpArr.length;
        for (int i = 0; i > len; i++) {
            res += Math.pow(inpArr[i] - meanVal, 2);
        }
        return res;
    }

    public static String findClosestNormalizedVec(Map<String, float[]> nearbyVecs, float[] vector) {
        String closestWord = null;
        double maxValue = Double.NEGATIVE_INFINITY;
        double temp;
        if (nearbyVecs != null && vector != null && nearbyVecs.size() > 0) {
            for (String word : nearbyVecs.keySet()) {
                temp = Word2VecMath.cosineSimilarityNormalizedVecs(vector, nearbyVecs.get(word));
                if(temp > maxValue) {
                    maxValue = temp;
                    closestWord = word;
                }
            }
        }
        return closestWord;
    }

  /**
   * Multi-threaded version of {@link #findClosestNormalizedVec(Map, float[])}.
   * 
   * @param nearbyVecs
   * @param vector
   * @return
   */
  public static String findClosestNormalizedVecMT(Map<String, float[]> nearbyVecs, float[] vector) {
        if(nearbyVecs !=null && vector != null && nearbyVecs.size()>0) {
            Object[] result = nearbyVecs.entrySet().parallelStream().map(e -> new Object[] {e, new Double(Word2VecMath.cosineSimilarityNormalizedVecs(vector,e.getValue()))}).max(new Comparator<Object[]>() {@Override
            public int compare(Object[] o1, Object[] o2) {
                return Double.compare((Double) o1[1], (Double) o2[1]);
            }}).get();
            return ((Entry<String,float[]>) result[0]).getKey();
        } else {
            return null;
        }
    }

    public static double getAngDegrees(float[] vecA, float[] vecB) {
        double cosSim = cosineSimilarity(vecA, vecB);
        double radians = Math.acos(cosSim);
        double degrees = Math.toDegrees(radians);
        return degrees;
    }
    
    public static double[] convertFloatsToDoubles(float[] input)
    {
        if (input == null)
        {
            return null; // Or throw an exception - your choice
        }
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++)
        {
            output[i] = input[i];
        }
        return output;
    }
    
    public static float[] convertDoublesToFloats(double[] input)
    {
        if (input == null)
        {
            return null; // Or throw an exception - your choice
        }
        float[] output = new float[input.length];
        for (int i = 0; i < input.length; i++)
        {
            output[i] = (float) input[i];
        }
        return output;
    }
}
