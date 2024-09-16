package model.dao.util;

public enum DepartmentFields {
	
	ID("Id"),
    NAME("Name");
    
    private String fieldName;

	private DepartmentFields(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}
}//enum DepartmentFields
