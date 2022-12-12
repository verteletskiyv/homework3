package part1;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Objects;

public class Parser {
    private static final ObjectMapper JSON_MAPPER;
    private static final XmlMapper XML_MAPPER;
    private static final ExecutorService service;

    static {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JSON_MAPPER = jsonMapper;
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        XML_MAPPER = xmlMapper;
        service = Executors.newWorkStealingPool(8);
    }

    public Map<ViolationType, Double> getFineSummaryFromFolder(File folder) {
        ReentrantLock lock = new ReentrantLock();
        List<File> files = Arrays.asList(
                Objects.requireNonNull(folder.listFiles(pathname -> pathname.getName().endsWith(".json"))));
        Map<ViolationType, Double> violationsTypeDouble = new HashMap<>();
        files.forEach(file -> CompletableFuture.runAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(file));
                 JsonParser jsonParser = JSON_MAPPER.getFactory().createParser(reader)) {
                if (jsonParser.nextToken() != JsonToken.START_ARRAY)
                    throw new IllegalArgumentException("Expected content to be an array");

                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                    Violation tmp = readViolation(jsonParser);
                    lock.lock();
                    violationsTypeDouble.merge(tmp.getType(), tmp.getFineAmount(), Double::sum);
                    lock.unlock();
                }
            } catch (IOException e) {
                throw new RuntimeException("File reading failure");
            }
        }, service));

        shutdownAndAwaitTermination();
        return violationsTypeDouble;
    }

    private Violation readViolation(JsonParser jsonParser) throws IOException {
        if (jsonParser.currentToken() != JsonToken.START_OBJECT)
            throw new IllegalStateException("Expected content to be an object");

        Violation v = new Violation();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String property = jsonParser.getCurrentName();
            jsonParser.nextToken();
            switch (property) {
                case "fine_amount" -> v.setFineAmount(jsonParser.getDoubleValue());
                case "type" -> v.setType(ViolationType.valueOf(jsonParser.getText()));
            }
        }
        return v;
    }

    public Map<ViolationType, Double> sortSummary(Map<ViolationType, Double> summaryMap) {
        return summaryMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void writeToXml(Map<ViolationType, Double> summaryMap, File outPath) {
        try {
            XML_MAPPER.writer().withRootName("ViolationsFineSummary").writeValue(outPath, summaryMap);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to XML");
        }
    }

    private static void shutdownAndAwaitTermination() {
        Parser.service.shutdown();
        try {
            if (!Parser.service.awaitTermination(60, TimeUnit.SECONDS)) {
                Parser.service.shutdownNow();
                if (!Parser.service.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            Parser.service.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
