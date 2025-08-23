package com.example.springaiworkshop.config;

import io.qdrant.client.QdrantClient;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.Optional;

@Configuration
public class VectorDbLoaderConfig {

    @Value("file:C:/ai/llm/data/about_nfjs.txt")
    private Resource aboutTxt;

    @Value("file:C:/ai/llm/data/nfjs_faq.txt")
    private Resource faqTxt;

    @Value("file:C:/ai/llm/data/nfjs_schedule.pdf")
    private Resource schedulePdf;

    // If you aren't using Qdrant, can do this:
    // @Bean
    // VectorStore vectorStore(EmbeddingModel embeddingModel) {
    //     return SimpleVectorStore.builder(embeddingModel).build();
    // }

    @Bean
    public ApplicationRunner PopulateVectorDB(VectorStore vectorStore, VectorStoreExistenceChecker checker) {
        return args -> {
            if (checker.exists()) {
                System.out.println("Skipping vector store load - already loaded");
                return;
            }

            var aboutReader = new TikaDocumentReader(aboutTxt);
            var faqReader = new TikaDocumentReader(faqTxt);
            var scheduleReader = new TikaDocumentReader(schedulePdf);

            var splitter = TokenTextSplitter.builder().build();

            // Spring should be configuring embedding clients behind the scenes, and
            // VectorStore should automatically use those - and thus make embedding
            // API calls when adding documents.
            vectorStore.add(splitter.apply(aboutReader.get()));
            vectorStore.add(splitter.apply(faqReader.get()));
            vectorStore.add(splitter.apply(scheduleReader.get()));

            System.out.println("PopulateVectorDB finished");
        };
    }

    public interface VectorStoreExistenceChecker {
        boolean exists();
    }

    @Bean
    public VectorStoreExistenceChecker qdrantExistenceChecker(QdrantVectorStore vectorStore) {
        return () -> {
            Optional<QdrantClient> client = vectorStore.getNativeClient();
            if (client.isEmpty()) {
                return false;
            }

            try {
                var res = client.get().countAsync("vector_store").get();
                return res != null && res > 0;
            } catch (Exception e) {
                return false;
            }
        };
    }
}
