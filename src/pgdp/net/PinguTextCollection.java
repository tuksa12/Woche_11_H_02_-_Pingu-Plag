package pgdp.net;

import java.util.*;
import java.util.stream.Collectors;

public class PinguTextCollection {//TODO ver como fayer funcionar com multiplos threads
    private static Count ID = new Count();
    private Map<Integer, PinguText> collection = new HashMap<>();

    public PinguTextCollection() {
    }

    public synchronized PinguText add(String title, String author, String text){
        PinguText result = new PinguText(ID.count,title,author,text);
        collection.put(ID.count, result);
        ID.inc();
        return result;
    }

    public PinguText findById(long id){
        if(collection.containsKey(id)){
            return collection.get(id);
        }
        return null;
    }

    public List<PinguText> getAll(){
        return collection.values().stream()
                .sorted(Comparator.comparing(PinguText::getId))
                .collect(Collectors.toList());
    }

    public Map<PinguText, Double> findPlagiarismFor(long id){
        if(collection.containsKey(id)){
            Map<PinguText, Double> result = new HashMap<>();
            PinguText idPinguText = collection.get(id);
            collection.values().stream()
                    .filter(text -> text.getId() != id)
                    .forEach(text -> {
                        double similarity = text.computeSimilarity(idPinguText);
                        if(similarity >= 0.001){
                            result.put(text, text.computeSimilarity(idPinguText));
                        }
                    });
        }
        return null;
    }

    public static class Count {
        private int count = 0;
        public synchronized void inc() {
            String s = Thread.currentThread().getName();
            int y = count;
            count = y+1;
        }
    }
}

