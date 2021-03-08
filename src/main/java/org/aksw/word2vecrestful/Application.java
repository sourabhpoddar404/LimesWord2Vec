package org.aksw.word2vecrestful;

import org.aksw.word2vecrestful.db.IDatabase;
import org.aksw.word2vecrestful.db.LimesDB;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Application {

    public static void main(String args[]) {

        IDatabase db = new LimesDB();


        new Application().test(db, 1000);
    }

    public void test(IDatabase db, int N) {

        Map<String, float[]> vectors = new HashMap<>();
        vectors = ((LimesDB) db).getModel().word2vec;
        float queryVectors[][] = new float[2000][300];
        int wrong = 0;
        for (int i = 0; i < N; i++) {

            String word = (String) vectors.keySet().toArray()[new Random().nextInt(vectors.keySet().toArray().length)];
            float[] wordvec = vectors.get(word);
            for (int l = 0; l < 300; l++)
                wordvec[l] = (float) (wordvec[l] + db.stdDev[l]);
            queryVectors[i] = wordvec;
        }
        long limesTime =0;
        long startTime = System.nanoTime();
        for (int i = 0; i<N; i++)
        {
            db.getClosest("abc", queryVectors[i]);
        }
        long stopTime = System.nanoTime();
        limesTime= (stopTime - startTime);

        long bruteTime =0;
        startTime = System.nanoTime();
        for (int i = 0; i<N; i++)
        {
            db.getClosestBrute("abc", queryVectors[i]);
        }
        stopTime = System.nanoTime();
        bruteTime = (stopTime - startTime);
        try {
            FileWriter myWriter = new FileWriter("results.txt", true);
            BufferedWriter bw = new BufferedWriter(myWriter);
            bw.write("Time with Limes :" + limesTime + " Time with Brute Force : " + bruteTime + "Number of Words compared :" + N);
            bw.newLine();
            bw.close();
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

