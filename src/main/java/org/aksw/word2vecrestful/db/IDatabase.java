package org.aksw.word2vecrestful.db;

import org.aksw.word2vecrestful.word2vec.Word2VecModel;

import java.util.LinkedHashMap;

public interface IDatabase {

  public abstract byte[] getByteVec(final String word);

  public abstract byte[] getByteNormVec(final String word);

  public abstract float[] getVec(final String word);

  public abstract float[] getNormVec(final String word);

  public abstract LinkedHashMap<String, Double> getNBest(String word, final int n);

  public final Word2VecModel model = null;

  public float[] stdDev = new float[300];

  public abstract LinkedHashMap<String, Double> getNClosest(float[] vec, final int n);

  public abstract String getClosest(String word,  float[] queryVector);

  String getClosestBrute(String abc, float[] queryVector);
}
