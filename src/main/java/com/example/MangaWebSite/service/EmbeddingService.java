package com.example.MangaWebSite.service;

import ai.djl.ModelException;
import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmbeddingService {
    private Predictor<String, float[]> predictor;

    @PostConstruct
    public void init() throws ModelException, IOException {
        // Використання моделі paraphrase-multilingual-mpnet-base-v2, яка підтримує українську
        Criteria<String, float[]> criteria = Criteria.builder()
                .setTypes(String.class, float[].class)
                .optModelUrls("djl://ai.djl.huggingface.pytorch/sentence-transformers/paraphrase-multilingual-mpnet-base-v2")
                .optEngine("PyTorch")
                .optTranslatorFactory(new TextEmbeddingTranslatorFactory()) // Створіть або використайте готовий Translator для тексту
                .build();
        ZooModel<String, float[]> model = criteria.loadModel();
        predictor = model.newPredictor();
    }

    public float[] getEmbedding(String text) throws TranslateException {
        return predictor.predict(text);
    }
}
