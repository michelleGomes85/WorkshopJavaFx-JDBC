package model.dao;

import java.util.List;

import model.entities.Department;
import model.entities.Seller;

public interface SellerDao extends GenericDao<Seller> {

	String INSERT = "INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId) VALUES (?, ?, ?, ?, ?)";
	String UPDATE = "UPDATE seller SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? WHERE Id = ?";
	String DELETE_BY_ID = "DELETE FROM seller WHERE Id = ?";
	String FIND_BY_ID = "SELECT seller.*, department.Name as DepName FROM seller INNER JOIN department ON seller.DepartmentId = department.Id WHERE seller.Id = ?";
	String FIND_ALL = "SELECT seller.*, department.Name as DepName FROM seller INNER JOIN department ON seller.DepartmentId = department.Id ORDER BY Name";
	String FIND_BY_DEPARTMENT = "SELECT seller.*, department.Name as DepName FROM seller INNER JOIN department ON seller.DepartmentId = department.Id WHERE DepartmentId = ? ORDER BY Name";

	List<Seller> findByDepartment(Department department);

}// interface SellerDao
