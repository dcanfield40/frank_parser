import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.HashMap;

public  class PlQuestionReader extends TableReader{


    public PlQuestionReader(String fileName, KeywordBuilder keywordBuilder) {
        this.fileName = fileName;
        this.keywordBuilder = keywordBuilder;
    }

    public ArrayList<PlQuestion> getRows (){
        ArrayList<PlQuestion> rows = new ArrayList<>();
        for (CSVRecord record : getRecords()) {
            PlQuestion PlQuestion = new PlQuestion(record, keywordBuilder);
            rows.add(PlQuestion);
        }
        return rows;
    }

    @Override
    public CSVFormat getHeaderMappings () {
        return CSVFormat.RFC4180.withHeader(ID, STEP_NUMBER, SORT_ORDER,QUESTION_BODY, FIELD_TYPE, 
                FIELD_CHOICES, HOLD_STATUS, HOLD_CONDITION, IS_CHAINING,
                CHAINING_CONDITION, CHAIN_QUESTION_ID, CREATED_AT, CREATED_BY, UPDATED_AT,UPDATED_BY, 
                IS_VERIFIED, STATES_LIST, COMMENT, IS_DELETED_BY).withFirstRecordAsHeader();
    }

    public class PlQuestion {
        KeywordBuilder keywordBuilder;
        String id;
        Integer sortOrder;
        String questionBody;
        String fieldType;
        String fieldChoices;
        String holdStatus;
        String holdCondition;
        String isChaining;
        String chainingCondition;
        Integer chainQuestionId;
        String createdAt;
        String createdBy;
        String updatedAt;
        String updatedBy;
        String isVerified;
        String statesList;
        String comment;
        String isDeletedBy;
        String labelName;
        String name;
        Integer row;


        public PlQuestion (CSVRecord record, KeywordBuilder keywordBuilder) {
            this.id = record.get(ID);
            this.sortOrder = Integer.parseInt(record.get(SORT_ORDER));
            this.questionBody = record.get(QUESTION_BODY);
            this.fieldType = record.get(FIELD_TYPE);
            this.fieldChoices = record.get(FIELD_CHOICES);
            this.holdStatus = record.get(HOLD_STATUS);
            this.holdCondition = record.get(HOLD_CONDITION);
            this.isChaining = record.get(IS_CHAINING);
            this.chainingCondition = record.get(CHAINING_CONDITION);
            this.chainQuestionId = !(record.get(CHAIN_QUESTION_ID)).isEmpty()?Integer.parseInt(record.get(CHAIN_QUESTION_ID)):0;
            this.createdAt = record.get(CREATED_AT);
            this.createdBy = record.get(CREATED_BY);
            this.updatedAt = record.get(UPDATED_AT);
            this.updatedBy = record.get(UPDATED_BY);
            this.isVerified = record.get(IS_VERIFIED);
            this.statesList = record.get(STATES_LIST);
            this.comment = record.get(COMMENT);
            this.isDeletedBy = record.get(IS_DELETED_BY);
            this.labelName = record.get(LABEL_NAME);
            this.name = record.get(NAME);
            this.row = Integer.parseInt(record.get(ROW));
            this.keywordBuilder = keywordBuilder;
        }

        public String getId() {
            return id;
        }


        public Integer getSortOrder() {
            return sortOrder;
        }


        public String getQuestionBody() {
            return questionBody.replace("'","''");
        }


        public String getFieldType() {
            return fieldType;
        }


        public String getFieldChoices() {
            return fieldChoices;
        }

        public String getHoldStatus() {
            return holdStatus;
        }


        public String getHoldCondition() {
            return holdCondition;
        }


        public String getIsChaining() {
            return isChaining;
        }


        public String getChainingCondition() {
            return chainingCondition;
        }


        public Integer getChainQuestionId() {
            return chainQuestionId;
        }


        public String getCreatedAt() {
            return createdAt;
        }


        public String getCreatedBy() {
            return createdBy;
        }


        public String getUpdatedAt() {
            return updatedAt;
        }


        public String getUpdatedBy() {
            return updatedBy;
        }


        public String getIsVerified() {
            return isVerified;
        }


        public String getStatesList() {
            return statesList;
        }


        public String getComment() {
            return comment;
        }


        public String getIsDeletedBy() {
            return isDeletedBy;
        }

        public Integer getDisplayOrder(){
            return sortOrder;
        }

        public Integer getRequired(){
            return 1;
        }

        public String getLabelName (){
            return labelName;
            //return keywordBuilder.getLabelName(getQuestionBody());
         }

        public String getName(){
            return name;
            //return keywordBuilder.getName(getQuestionBody());
        }

        public Integer getRow() {
            return row;
        }

        public String getQuestionSqls(String fieldType){
            StringBuffer sb = new StringBuffer();
            sb.append("INSERT INTO [dbo].[question]");
            sb.append (String.format("(%s, %s, %s, %s, %s, %s, %s, %s)", DISPLAY_ORDER, LABEL_NAME, NAME,QUESTION_BODY, TYPE, REQUIRED,CREATED_BY,UPDATED_BY));
            sb.append (String.format("VALUES (%d, '%s', '%s', '%s', '%s', %s, '%s', '%s');", getDisplayOrder(), getLabelName(), getName(), getQuestionBody(),fieldType, getRequired(),getCreatedBy(),getUpdatedBy()));
            return sb.toString();
        }

        public String getAnswerSqls(int numberOfQuestionRows, HashMap<String,Integer> questionIdToRowOffset) {
            StringBuffer sb = new StringBuffer();
            //System.out.println(" print '@QuestionId = '");
            //System.out.println(" print @QuestionId ");

            boolean chainGenerated = false;

            for (String answer: getFieldChoices().split("\n")){
                sb.append("INSERT INTO [dbo].[answer]");
                sb.append (String.format("(%s, %s, %s, %s)", QUESTION_ID, ANSWER, CREATED_BY,UPDATED_BY));
                sb.append (String.format("VALUES ((@QuestionId - %d) + %d, '%s', '%s', '%s');\n", numberOfQuestionRows, questionIdToRowOffset.get(getId()), answer.trim().replace("'","''"), getCreatedBy(),getUpdatedBy()));


                if (getChainQuestionId() >0){
                    if (getChainingCondition().trim().equals(answer.trim())) {
                        if (questionIdToRowOffset.get(getChainQuestionId().toString()) != null) {
                            sb.append("SELECT @answerId = SCOPE_IDENTITY();\n");
                            sb.append("INSERT INTO [dbo].[child_question]");
                            sb.append(String.format("(%s, %s, %s, %s)", QUESTION_ID, ANSWER_ID, CREATED_BY, UPDATED_BY));
                            sb.append(String.format("VALUES ((@QuestionId - %d) + %d, @answerId, '%s', '%s');\n", numberOfQuestionRows, questionIdToRowOffset.get(getChainQuestionId().toString()), getCreatedBy(), getUpdatedBy()));
                            chainGenerated = true;
                            //System.out.println ("print questionID =");
                            //System.out.println (String.format("print (@QuestionId - %d) + %d)", numberOfQuestionRows, questionIdToRowOffset.get(getId())));
                        }
                    }

                }
            }
            if (getChainQuestionId() > 0 && !chainGenerated){
                sb.append(String.format("/** ERROR - chain question not generated FOR -> ID: %s chain_question_id: %s **/\n", getId(), getChainQuestionId()));
            }
            return sb.toString();
        }
    }
}
