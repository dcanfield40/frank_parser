import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;

public  class ClassOfBusinessReader extends TableReader{


    public ClassOfBusinessReader(String fileName) {
        this.fileName = fileName;

    }

    public ArrayList<ClassOfBusiness> getRows (){
        ArrayList<ClassOfBusiness> rows = new ArrayList<>();
        for (CSVRecord record : getRecords()) {
            ClassOfBusiness classOfBusiness = new ClassOfBusiness(record);
            rows.add(classOfBusiness);
        }
        return rows;
    }

    @Override
    public CSVFormat getHeaderMappings () {
        return CSVFormat.RFC4180.withHeader(ID, CLASS_OF_BUSINESS_GROUP_ID, CLASS_OF_BUSINESS,
                CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY, IS_PL, IS_GL,IS_BOP,IS_ACTIVE).withFirstRecordAsHeader();
    }


    public class ClassOfBusiness {
        String id;
        Integer classOfBusinessGroupId;
        String name;
        String createdBy;
        String updatedBy;
        String isPl;
        String isGl;
        String isBop;
        String isActive;
        Integer row;

        public ClassOfBusiness(CSVRecord record) {
            this.id = record.get(ID);
            this.classOfBusinessGroupId = Integer.parseInt(record.get(CLASS_OF_BUSINESS_GROUP_ID));
            this.name = record.get(CLASS_OF_BUSINESS);
            this.createdBy = record.get(CREATED_BY);
            this.updatedBy = record.get(UPDATED_BY);
            this.isPl = record.get(IS_PL);
            this.isGl = record.get(IS_GL);
            this.isBop = record.get(IS_BOP);
            this.isActive = record.get(IS_ACTIVE);
            this.row = Integer.parseInt(record.get(ROW));
        }


        public Integer getClassOfBusinessGroupId() {
            return classOfBusinessGroupId;
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

        public String getIsPl() {
            return isPl;
        }

        public String getIsGl() {
            return isGl;
        }

        public String getIsBop() {
            return isBop;
        }

        public String getIsActive() {
            return isActive;
        }

        public Integer getRow() {
            return row;
        }

        public String getId() {
            return id;
        }

        public String getSql (){
            StringBuffer sb = new StringBuffer();
            sb.append("INSERT INTO [dbo].[class_of_business]");
            sb.append (String.format("(%s, %s, %s)", NAME,CREATED_BY, UPDATED_BY));
            sb.append (String.format(" VALUES ( '%s', '%s', '%s');", getName().replace("'","''"),getCreatedBy(),getUpdatedBy()));
            return sb.toString();
        }

    }

}
