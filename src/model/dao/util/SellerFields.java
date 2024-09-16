package model.dao.util;

public enum SellerFields {
	
    ID("Id"),
    NAME("Name"),
    EMAIL("Email"),
    BIRTH_DATE("BirthDate"),
    BASE_SALARY("BaseSalary"),
    DEPARTMENT_ID("DepartmentId"),
    DEPARTMENT_NAME("DepName");

    private String fieldName;

    SellerFields(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
    
}//enum SellerFields 
