package pgdp.net;

import java.util.*;
import java.util.stream.Collectors;

public class PinguTextCollection {
    //Attributes
    private static Count ID = new Count();
    private Map<Long, PinguText> collection = new HashMap<>();

    //Empty constructor
    public PinguTextCollection() {
    }

    //Synchronized method to add to the collection
    public synchronized PinguText add(String title, String author, String text){
        PinguText result = new PinguText(ID.count,title,author,text);
        collection.put(ID.count, result);
        ID.inc();
        return result;
    }

    //Returns the PinguText by the ID or null
    public PinguText findById(long id){
        if(collection.containsKey(id)){
            return collection.get(id);
        }
        return null;
    }

    //Get all PinguText of the collection
    public List<PinguText> getAll(){
        return collection.values().stream()
                .sorted(Comparator.comparing(PinguText::getId))
                .collect(Collectors.toList());
    }

    //Returns the plagiarism for every PinguText in the collection
    public Map<PinguText, Double> findPlagiarismFor(long id){
        if(collection.containsKey(id)){
            Map<PinguText, Double> result = new HashMap<>();
            PinguText idPinguText = collection.get(id);
            collection.values().stream()
                    .filter(text -> text.getId() != id)//Gets every PinguText except the one that we are comparing
                    .forEach(text -> {
                        double similarity = text.computeSimilarity(idPinguText);
                        if(similarity >= 0.001){
                            result.put(text, text.computeSimilarity(idPinguText));
                        }
                    });
            return result;
        }
        return null;
    }

    //Helper class to count the ID
    private static class Count {
        private long count = 1;
        public synchronized void inc() {
            long y = count;
            count = y+1;
        }
    }
}

