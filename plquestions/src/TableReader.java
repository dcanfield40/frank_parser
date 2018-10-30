import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public  abstract class TableReader {
    public static String ID = "id";
    public static String CLASS_OF_BUSINESS_QUESTION_ID = "class_of_business_question_id";
    public static String CLASS_OF_BUSINESS_GROUP_ID = "class_of_business_group_id";
    public static String CLASS_OF_BUSINESS_ID = "class_of_business_id";
    public static String CREATED_AT = "created_at";
    public static String CREATED_BY = "created_by";
    public static String UPDATED_AT = "updated_at";
    public static String UPDATED_BY = "updated_by";
    public static String SORT_ORDER = "sort_order";
    public static String CLASS_OF_BUSINESS_GROUP = "class_of_business_group";
    public static String CLASS_OF_BUSINESS = "class_of_business";
    public static String IS_PL = "IsPL";
    public static String IS_GL = "IsGL";
    public static String IS_BOP = "isBOP";
    public static String IS_ACTIVE = "is_active";
    public static String QUESTION_BODY = "question";
    public static String FIELD_TYPE = "field_type";
    public static String FIELD_CHOICES = "field_choices";
    public static String HOLD_STATUS = "hold_status";
    public static String HOLD_CONDITION = "hold_condition";
    public static String IS_CHAINING = "is_chaining";
    public static String CHAINING_CONDITION = "chaining_condition";
    public static String CHAIN_QUESTION_ID = "chain_question_id";
    public static String IS_VERIFIED = "is_verified";
    public static String STATES_LIST = "states_list";
    public static String COMMENT = "Comment";
    public static String IS_DELETED_BY = "is_deleted_b";
    public static String STEP_NUMBER = "step_number";
    public static String NAME = "name";
    public static String DISPLAY_ORDER = "display_order";
    public static String LABEL_NAME = "label_name";
    public static String TYPE = "type";
    public static String REQUIRED = "required";
    public static String QUESTION_ID = "question_id";
    public static String ANSWER_ID = "answer_id";
    public static String ANSWER = "answer";
    public static String STATE_CODE = "state_code";
    public static String ROW = "row";

    String fileName;
    KeywordBuilder keywordBuilder;

    public Iterable<CSVRecord> getRecords(){
        Iterable<CSVRecord> records = null;
        Reader reader;
        try {
            reader = new FileReader(getFileName());
            records = getHeaderMappings().parse(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

    public int getNumberOfRows(){
        return ((Collection<?>) getRows()).size();
    }

    public abstract ArrayList<?> getRows ();


    public String getFileName() {
        return fileName;
    }

    public CSVFormat getHeaderMappings () { return null;}
}
