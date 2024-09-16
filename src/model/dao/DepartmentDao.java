package model.dao;

import model.entities.Department;

public interface DepartmentDao extends GenericDao<Department> {
	
	String INSERT = "INSERT INTO department (Name) VALUES (?)";
	String UPDATE = "UPDATE department SET Name = ? WHERE Id = ? ";
	String DELETE_BY_ID = "DELETE FROM department WHERE Id = ?";
	String FIND_BY_ID = "SELECT * FROM department WHERE department.Id = ?";
	String FIND_ALL = "SELECT * FROM department ORDER BY Name";
	
}//interface DepartmentDao
