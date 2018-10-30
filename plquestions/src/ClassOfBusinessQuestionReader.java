import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassOfBusinessQuestionReader extends TableReader{
    public ClassOfBusinessQuestionReader(String fileName) {
        this.fileName = fileName;

    }

    public ArrayList<ClassOfBusinessQuestion> getRows (){
        ArrayList<ClassOfBusinessQuestion> rows = new ArrayList<>();
        for (CSVRecord record : getRecords()) {
            ClassOfBusinessQuestion classOfBusiness = new ClassOfBusinessQuestion(record);
            rows.add(classOfBusiness);
        }
        return rows;
    }

    @Override
    public CSVFormat getHeaderMappings () {
        return CSVFormat.RFC4180.withHeader(ID, CLASS_OF_BUSINESS_QUESTION_ID, CLASS_OF_BUSINESS_ID,
                CREATED_AT, CREATED_BY).withFirstRecordAsHeader();
    }

    public class ClassOfBusinessQuestion {
        String id;
        Integer classOfBusinessQuestionId;
        Integer classOfBusinessId;
        String createdAt;
        String createdBy;
        String updatedBy;

        public ClassOfBusinessQuestion(CSVRecord record) {
            this.id = record.get(ID);
            this.classOfBusinessQuestionId = Integer.parseInt(record.get(CLASS_OF_BUSINESS_QUESTION_ID));
            this.classOfBusinessId = Integer.parseInt(record.get(CLASS_OF_BUSINESS_ID));
            this.createdAt = record.get(CREATED_AT);
            this.createdBy = record.get(CREATED_BY);
            this.updatedBy = record.get(UPDATED_BY);
        }

        public String getId() {
            return id;
        }

        public Integer getClassOfBusinessQuestionId() {
            return classOfBusinessQuestionId;
        }

        public Integer getClassOfBusinessId() {
            return classOfBusinessId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public String getUpdatedBy() {
            return updatedBy;
        }

        public String getSql (int numberOfClassBusinessReaderRows, int numberOfQuestionReaderRows, HashMap<String,String> questionStates, HashMap<String,Integer> classOfBusinessIdToRowOffset,
                              HashMap<String,Integer> questionIdToRowOffset) {
            StringBuffer sb = new StringBuffer();
            String stateList = questionStates.get(getClassOfBusinessQuestionId().toString());

            if (stateList!= null && !stateList.isEmpty()){
                for (String state: stateList.split(",")){
                    sb.append(getSqlBody(numberOfClassBusinessReaderRows, numberOfQuestionReaderRows,state,classOfBusinessIdToRowOffset,questionIdToRowOffset));
                }
            }else{
                sb.append(getSqlBody(numberOfClassBusinessReaderRows, numberOfQuestionReaderRows,"",classOfBusinessIdToRowOffset,questionIdToRowOffset));
            }

            return sb.toString();
        }

        private String getSqlBody (int numberOfClassBusinessReaderRows,int numberOfQuestionReaderRows, String state, HashMap<String,Integer> classOfBusinessIdToRowOffset,
                                   HashMap<String,Integer> questionIdToRowOffset){
            StringBuffer sb = new StringBuffer();
            if (!(classOfBusinessIdToRowOffset.get(getClassOfBusinessId().toString()) == null) && !(questionIdToRowOffset.get(getClassOfBusinessQuestionId().toString()) == null)){
                //sb.append(String.format("print 'BEFORE pnl_question: ID: %s'\n", getId()));
                //System.out.println ("print 'Class_of_Business_ID ='");
                //System.out.println (String.format("print (@classOfBusinessId - %d) + %d)", numberOfClassBusinessReaderRows, classOfBusinessIdToRowOffset.get(getClassOfBusinessId().toString())));
                //System.out.println ("print 'QUestion_ID =");
                //System.out.println (String.format("print (@QuestionId  - %d) + %d)", numberOfQuestionReaderRows, questionIdToRowOffset.get(getClassOfBusinessQuestionId().toString())));
                sb.append("INSERT INTO [dbo].[pnl_question]");
                sb.append (String.format("(%s, %s, %s, %s, %s)", STATE_CODE, CLASS_OF_BUSINESS_ID, QUESTION_ID ,CREATED_BY, UPDATED_BY));
                sb.append (String.format("VALUES ('%s',(@classOfBusinessId -  %d) + %d, (@QuestionId -  %d) + %d, '%s', '%s');",
                        state, numberOfClassBusinessReaderRows, classOfBusinessIdToRowOffset.get(getClassOfBusinessId().toString()),
                        numberOfQuestionReaderRows, questionIdToRowOffset.get(getClassOfBusinessQuestionId().toString()),getCreatedBy(), getUpdatedBy()));
            }else{
                if (classOfBusinessIdToRowOffset.get(getClassOfBusinessId().toString()) == null){
                    sb.append (String.format("/* ERROR - CLASS OF BUSINESS ID: %s NOT FOUND ", getClassOfBusinessId()));
                }else{
                    sb.append (String.format("/* ERROR - CLASS OF QUESTION ID: %s NOT FOUND ", getClassOfBusinessQuestionId()));
                }

                sb.append("INSERT INTO [dbo].[pnl_question]");
                sb.append (String.format("(%s, %s, %s, %s, %s)", STATE_CODE, CLASS_OF_BUSINESS_ID, QUESTION_ID ,CREATED_BY, UPDATED_BY));
                sb.append (String.format("VALUES ('%s',(@classOfBusinessId -  %d) + %d, (@QuestionId -  %d) + %d, '%s', '%s');",
                        state, numberOfClassBusinessReaderRows, classOfBusinessIdToRowOffset.get(getClassOfBusinessId().toString()),
                        numberOfQuestionReaderRows, questionIdToRowOffset.get(getClassOfBusinessQuestionId().toString()),getCreatedBy(), getCreatedBy()));
                sb.append("*/");
            }


            return sb.toString();
        }
    }
}
