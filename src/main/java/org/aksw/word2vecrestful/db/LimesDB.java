package org.aksw.word2vecrestful.db;

import org.aksw.word2vecrestful.utils.Word2VecMath;
import org.aksw.word2vecrestful.word2vec.Word2VecFactory;
import org.aksw.word2vecrestful.word2vec.Word2VecModel;

import java.util.*;

public class LimesDB implements org.aksw.word2vecrestful.db.IDatabase {

    private final Map<String, List<Map.Entry<String, Double>>> exemplarToWordList = new HashMap<>();
    public final Word2VecModel model = Word2VecFactory.get();
    private Map<String, float[]> vectors = new HashMap<>();
    float[][] array = new float[300][20000];
    public float[] mean = new float[300],stdDev = new float[300];

    public LimesDB() {
        vectors.putAll(model.word2vec);
        Map<String, Double> dissum = new HashMap<>();
        String firstExemplar = (String) vectors.keySet().toArray()[new Random().nextInt(vectors.keySet().toArray().length)];
        float[] wordvec = vectors.get(firstExemplar);
        HashMap<String, float[]> exemplarList = new HashMap<>();
        exemplarList.put(firstExemplar, vectors.get(firstExemplar));
        vectors.remove(firstExemplar);
        Map<String, List<String>> wordToExemplarMap = new HashMap<>();
        for (Map.Entry<String, float[]> vector : vectors.entrySet()) {
            Double dis = (1 - Word2VecMath.cosineSimilarity(wordvec, vector.getValue()));
            wordToExemplarMap.put(vector.getKey(), new ArrayList<String>() {{
                add(firstExemplar);
                add(String.valueOf(dis));
            }});
            dissum.put(vector.getKey(), dis);
        }
        int numExemplar = 150;
        for (int i = 1; i < numExemplar; i++) {
            String key = Collections.max(dissum.entrySet(), Map.Entry.comparingByValue()).getKey();
            dissum.remove(key);

            exemplarList.put(key, vectors.get(key));
            vectors.remove(key);
            wordToExemplarMap.remove(key);
            for (Map.Entry<String, float[]> vector : vectors.entrySet()) {
                double dis = 1 - Word2VecMath.cosineSimilarity(exemplarList.get(key), vector.getValue());
                dissum.put(vector.getKey(), dissum.get(vector.getKey()) + dis);
                if (dis < Double.parseDouble(wordToExemplarMap.get(vector.getKey()).get(1)))
                    wordToExemplarMap.put(vector.getKey(), new ArrayList<String>() {{
                        add(key);
                        add(String.valueOf(dis));
                    }});
            }
        }
        dissum.clear();
        Map<String, Map<String, Double>> exemplarToWordMap = new HashMap<>();
        for (Map.Entry<String, List<String>> word : wordToExemplarMap.entrySet()) {
            String exemplar = word.getValue().get(0);
            if (exemplarToWordMap.containsKey(exemplar)) {
                Map<String, Double> temp = exemplarToWordMap.get(exemplar);
                temp.put(word.getKey(), Double.parseDouble(word.getValue().get(1)));
                exemplarToWordMap.put(exemplar, temp);

            } else {
                Map<String, Double> temp = new HashMap<>();
                temp.put(word.getKey(), Double.parseDouble(word.getValue().get(1)));
                exemplarToWordMap.put(exemplar, temp);
            }
        }
        wordToExemplarMap.clear();
        vectors.clear();
        for (String key : exemplarToWordMap.keySet()) {
            List<Map.Entry<String, Double>> list =
                    new LinkedList<>(exemplarToWordMap.get(key).entrySet());
            list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
            exemplarToWordList.put(key, list);
        }
        exemplarToWordMap.clear();
        sampling();
    }

    public static void main(String arg[]){
        new LimesDB();
    }

    public void closestExemplar(String word)
    {
        double min = 100;
        String result = "";
        vectors.putAll(model.word2vec);
        for ( String key : exemplarToWordList.keySet())
        {
            double dis = 1.00 - Word2VecMath.cosineSimilarity(vectors.get(word), vectors.get(key));
            if( dis<min)
            {
                min = dis;
                result = key;

            }

        }
        System.out.println(result);
    }

    public String getClosestBrute(String word, float[] wordvector) {
        String closestWord = "";
        double min = 100.0;
        for (String key : vectors.keySet()) {
            if (!key.equals(word)) {
                if ((1.00 - Word2VecMath.cosineSimilarity(vectors.get(key), wordvector)) < min) {
                    min = 1.00 - Word2VecMath.cosineSimilarity(vectors.get(key), wordvector);
                    closestWord = key;
                }
            }
        }
        return closestWord;
    }

    @Override
    public String getClosest(String word, float[] wordvector) {
        double min = 100.0;
        String minword = "";
        for (String key : exemplarToWordList.keySet()) {
            double dis = 1.00 - Word2VecMath.cosineSimilarity(vectors.get(key), wordvector);
            if (dis<min && !key.equals(word))
            {
                min = dis;
                minword = key;
            }
            for (Map.Entry<String, Double> Entry : exemplarToWordList.get(key)) {
                if (!word.equals(Entry.getKey())) {
                    if ((dis - Entry.getValue()) < min) {
                        double newDis = 1.00 - Word2VecMath.cosineSimilarity(wordvector, vectors.get(Entry.getKey()));
                        if (newDis < min) {
                            min = newDis;
                            minword = Entry.getKey();
                        }
                    } else {
                        //System.out.println("sds");
                        break;

                    }
                }
            }
        }
        return minword;
    }
    public void find(String word)
    {
        vectors.putAll(model.word2vec);
        float [ ] wordvec = vectors.get(word);
        for(String key : exemplarToWordList.keySet()){
            for (Map.Entry<String, Double> Entry : exemplarToWordList.get(key))
            {
                if(Entry.getKey().equals(word))
                {
                    System.out.print(key);
                }
            }
        }
    }

    public void sampling()
    {

        vectors.putAll(model.word2vec);
        int j =0;
        for ( String key : vectors.keySet())
        {
            float[] wordVec = vectors.get(key);
            for (int i =0;i<wordVec.length;i++)
            {
                array[i][j] = wordVec[i];
            }
            j++;
        }

        for ( int k =0 ; k < 300; k++){
            for (int i =0 ; i <20000;i++)
            {
                mean[k] += array[k][i];
            }
            mean[k]= mean[k]/20000;
            for (int i =0 ; i <20000;i++)
            {
                stdDev[k] += Math.pow(array[k][i] - mean[k],2);
            }
            stdDev[k] = (float) (Math.sqrt(stdDev[k])/20000);
        }
        System.out.println("sds");
    }
    @Override
    public byte[] getByteVec(String word) {
        return new byte[0];
    }

    @Override
    public byte[] getByteNormVec(String word) {
        return new byte[0];
    }

    @Override
    public float[] getVec(String word) {
        return new float[0];
    }

    @Override
    public float[] getNormVec(String word) {
        return new float[0];
    }

    @Override
    public LinkedHashMap<String, Double> getNBest(String word, int n) {
        return null;
    }

    @Override
    public LinkedHashMap<String, Double> getNClosest(float[] vec, int n) {
        return null;
    }

    public Map<String, List<Map.Entry<String, Double>>> getExemplarToWordList() {
        return exemplarToWordList;
    }

    public Word2VecModel getModel() {
        return model;
    }

    public Map<String, float[]> getVectors() {
        return vectors;
    }

    public void setVectors(Map<String, float[]> vectors) {
        this.vectors = vectors;
    }

    public float[][] getArray() {
        return array;
    }

    public void setArray(float[][] array) {
        this.array = array;
    }

    public float[] getMean() {
        return mean;
    }

    public void setMean(float[] mean) {
        this.mean = mean;
    }

    public float[] getStdDev() {
        return stdDev;
    }

    public void setStdDev(float[] stdDev) {
        this.stdDev = stdDev;
    }


}
