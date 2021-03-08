package org.aksw.word2vecrestful.word2vec;

import java.io.File;

public class Word2VecFactory {


	public static Word2VecModel get() {
		return new Word2VecModelLoader().loadModel(new File("C:\\AllDataFolder\\Thesis\\LimesWord2Vec\\src\\main\\resources\\GoogleNews-vectors-negative300.bin"), true);
	}
	

}
