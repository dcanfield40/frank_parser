import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

public  class ClassOfBusinessGroupReader extends TableReader{
    int readerRows;


    public ClassOfBusinessGroupReader(String fileName) {
        this.fileName = fileName;

    }

    @Override
    public ArrayList<ClassOfBusinessGroup> getRows (){
        ArrayList<ClassOfBusinessGroup> rows = new ArrayList<>();
        for (CSVRecord record : getRecords()) {
            ClassOfBusinessGroup classOfBusinessGroup = new ClassOfBusinessGroup(record);
            rows.add(classOfBusinessGroup);
        }

        return rows;
    }


    @Override
    public CSVFormat getHeaderMappings () {
        return CSVFormat.RFC4180.withHeader(ID, CLASS_OF_BUSINESS_GROUP, SORT_ORDER,CREATED_AT,
                CREATED_BY, UPDATED_AT, UPDATED_BY).withFirstRecordAsHeader();
    }

    public class ClassOfBusinessGroup {
        String name;
        String createdBy;
        String updatedBy;



        public ClassOfBusinessGroup(CSVRecord record) {
            try {
                this.name = record.get(CLASS_OF_BUSINESS_GROUP);
                this.createdBy = record.get(CREATED_BY);
                this.updatedBy = record.get(UPDATED_BY);
            }catch (Exception e){
                System.out.println(e);
            }
        }

        public String getName() {
            return name;
        }


        public String getCreatedBy() {
            return createdBy;
        }


        public String getUpdatedBy() {
            return updatedBy;
        }

        public String getSql (){
            StringBuffer sb = new StringBuffer();
            sb.append("INSERT INTO [dbo].[class_of_business_group]");
            sb.append (String.format("(%s, %s, %s)", NAME, CREATED_BY, UPDATED_BY));
            sb.append (String.format("VALUES ('%s', '%s', '%s');",
                    getName(),getCreatedBy(),getUpdatedBy()));
            return sb.toString();
        }
    }

}
