import io.github.crew102.rapidrake.RakeAlgorithm;
import io.github.crew102.rapidrake.data.SmartWords;
import io.github.crew102.rapidrake.model.RakeParams;
import io.github.crew102.rapidrake.model.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class KeywordBuilder{
    RakeAlgorithm rakeAlgorithm;
    public KeywordBuilder() {
        // Create an object to hold algorithm parameters
        String[] stopWords = new SmartWords().getSmartWords();
        String[] stopPOS = {"VB", "VBD", "VBG", "VBN", "VBP", "VBZ"};
        int minWordChar = 1;
        boolean shouldStem = true;
        String phraseDelims = "[-,.?():;\"!/]";
        RakeParams params = new RakeParams(stopWords, stopPOS, minWordChar, shouldStem, phraseDelims);

        // Create a RakeAlgorithm object
        String POStaggerURL = "/Users/doc/projects/colonial/sql/util/plquestions/src/model-bin/en-pos-maxent.bin"; // The path to your POS tagging model
        String SentDetecURL = "/Users/doc/projects/colonial/sql/util/plquestions/src/model-bin/en-sent.bin"; // The path to your sentence detection model
        RakeAlgorithm rakeAlg = null;
        try {
            rakeAlg = new RakeAlgorithm(params, POStaggerURL, SentDetecURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.rakeAlgorithm =  rakeAlg;
    }

    public String getName(String sourceText){
        String description = getLabelName(sourceText).replace(" ","");
        return Character.toLowerCase(description.charAt(0)) + description.substring(1);
    }
    public String getLabelName(String sourceText){
        Result result = getKeywords(sourceText);
        HashMap<Integer,String> results = new HashMap<>();

        for (int i=0; i<result.getScores().length; i++){
            String keyword =  capitalizeWords(result.getFullKeywords()[i]);
            String initCapped = Character.toUpperCase(keyword.charAt(0)) + keyword.substring(1);
            results.put(Math.round(result.getScores()[i]), initCapped);
        }

        int counter = 0;
        ArrayList<Integer> keys = new ArrayList<Integer>(results.keySet());
        StringBuffer sb = new StringBuffer();
        //descending key order
        for(int i=results.size()-1; i>=0;i--){
            sb.append(results.get(keys.get(i)).replaceAll("[^a-zA-Z ]", ""));
            if (counter >2)
                break;
            ++counter;

        }
        //ascending key order
//            for (Integer key : results.keySet().stream().collect(Collectors.toList())) {
//                sb.append(results.get(key).replaceAll("[^a-zA-Z ]", ""));
//                if (counter >2)
//                    break;
//                ++counter;
//            }

        return sb.toString().length()>0?sb.toString():"FIX-ME";
    }

    private String capitalizeWords (String toBeCapped){
        String[] tokens = toBeCapped.split("\\s");
        toBeCapped = "";

        for(int i = 0; i < tokens.length; i++){
            char capLetter = Character.toUpperCase(tokens[i].charAt(0));
            toBeCapped +=  " " + capLetter + tokens[i].substring(1);
        }
        return toBeCapped.trim();
    }

    private Result getKeywords (String text){
        String questionPart = text.split("\\?")[0];
        return rakeAlgorithm.rake((questionPart)).distinct();
    }
}
