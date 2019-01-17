import java.util.HashMap;

public class PlQuestionParser {
    static HashMap<String,String> questionStates = new HashMap<>();
    static HashMap<String,Integer> classOfBusinessIdToRowOffset = new HashMap<>();
    static HashMap<String,Integer> questionIdToRowOffset = new HashMap<>();


    //TODO implement args
    public  static void main(String[] args)  {
        System.out.println("DECLARE @classOfBusinessId INT;");
        System.out.println("DECLARE @questionId INT;");
        System.out.println("DECLARE @answerId INT;");


        ClassOfBusinessReader classOfBusinessReader = new ClassOfBusinessReader("/Users/doc/projects/colonial/sql/util/plquestions/resources/class_of_business.csv");
        for (ClassOfBusinessReader.ClassOfBusiness classOfBusiness : classOfBusinessReader.getRows()) {
            classOfBusinessIdToRowOffset.put(classOfBusiness.getId(), classOfBusiness.getRow());
            System.out.println(classOfBusiness.getSql());
        }
//        System.out.println("SELECT @classOfBusinessId = SCOPE_IDENTITY();\n");
          System.out.println("SELECT @classOfBusinessId = max(id) from class_of_business;\n");

        //questions

        //System.out.println(" print '@classOfBusinessId = '");
        //System.out.println(" print @classOfBusinessId ");

        KeywordBuilder keywordBuilder = new KeywordBuilder();

        PlQuestionReader plQuestionReader = new PlQuestionReader("/Users/doc/projects/colonial/sql/util/plquestions/resources/questions.csv", keywordBuilder);
        for (PlQuestionReader.PlQuestion plQuestion : plQuestionReader.getRows()) {
            if (!plQuestion.getStatesList().isEmpty()){
                questionStates.put(plQuestion.getId(), plQuestion.getStatesList());
            }

            questionIdToRowOffset.put(plQuestion.getId(), plQuestion.getRow());

            switch (plQuestion.getFieldType()){
                case "checkbox-question": System.out.println(plQuestion.getQuestionSqls("checkbox")); break;
                case "date-question": System.out.println(plQuestion.getQuestionSqls("date")); break;
                case "input-question":  System.out.println(plQuestion.getQuestionSqls("text")); break;
                case "radio-question":  System.out.println(plQuestion.getQuestionSqls("radio")); break;
                case "Text":   System.out.println(plQuestion.getQuestionSqls("text")); break;
                case "YesNo": System.out.println(plQuestion.getQuestionSqls("radio")); break;
                default:  System.out.println(String.format("/** ERROR! NOT IMPLEMENTED: %s FOR ID %s **/ \n",plQuestion.getFieldType(),plQuestion.getId()));
            }
        }
        //pnl_question (spreadsheet: class_of_business_question)
        System.out.println("SELECT @QuestionId = SCOPE_IDENTITY();\n");
        //System.out.println(" print '@QuestionId = '");
        //System.out.println(" print @QuestionId ");

        ClassOfBusinessQuestionReader classOfBusinessQuestionReader = new ClassOfBusinessQuestionReader("/Users/doc/projects/colonial/sql/util/plquestions/resources/class_of_business_questions.csv");
        for (ClassOfBusinessQuestionReader.ClassOfBusinessQuestion classOfBusinessQuestion : classOfBusinessQuestionReader.getRows()) {
            System.out.println(classOfBusinessQuestion.getSql(classOfBusinessReader.getNumberOfRows(), plQuestionReader.getNumberOfRows(), questionStates,classOfBusinessIdToRowOffset,questionIdToRowOffset));
        }
        //answers and chain questions
        PlQuestionReader plQuestionForAnswerReader = new PlQuestionReader("/Users/doc/projects/colonial/sql/util/plquestions/resources/questions.csv", keywordBuilder);
        for (PlQuestionReader.PlQuestion plQuestion : plQuestionForAnswerReader.getRows()) {
            switch (plQuestion.getFieldType()){
                case "checkbox-question": System.out.println(plQuestion.getAnswerSqls(plQuestionForAnswerReader.getNumberOfRows(), questionIdToRowOffset)); break;
                case "radio-question":  System.out.println(plQuestion.getAnswerSqls(plQuestionForAnswerReader.getNumberOfRows(),questionIdToRowOffset)); break;
                case "YesNo": System.out.println(plQuestion.getAnswerSqls(plQuestionForAnswerReader.getNumberOfRows(),questionIdToRowOffset)); break;
                default:  if (plQuestion.getChainQuestionId() > 0  ||
                        !plQuestion.getFieldChoices().isEmpty() ||
                        !plQuestion.getChainingCondition().isEmpty()){
                    System.out.println(String.format("/** ERROR! CHAIN OR ANSWER DETECTED, NOT HANDLED YET FOR  -> TYPE %s,  FOR ID %s **/\n",plQuestion.getFieldType(),plQuestion.getId()));
                }
            }
        }


    }

}

