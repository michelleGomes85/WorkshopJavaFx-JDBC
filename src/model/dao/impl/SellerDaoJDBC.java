package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.dao.util.SellerFields;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection connection;

	public SellerDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void insert(Seller seller) {
		executeUpdate(seller, INSERT, true);
	}

	@Override
	public void update(Seller seller) {
		executeUpdate(seller, UPDATE, false);
	}

	@Override
	public void deleteById(Integer id) {
		executeSimpleQuery(DELETE_BY_ID, id);
	}

	@Override
	public Seller findById(Integer id) {
		return executeFindQuery(FIND_BY_ID, id);
	}

	@Override
	public List<Seller> findAll() {
		return executeFindAllQuery(FIND_ALL);
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		return executeFindAllQuery(FIND_BY_DEPARTMENT, department.getId());
	}
	
	private void executeUpdate(Seller seller, String sql, boolean isInsert) {
		
		PreparedStatement preparedStatement = null;
		
		try {
			
			preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			prepareStatement(preparedStatement, seller, isInsert);
			
			int rowsAffected = preparedStatement.executeUpdate();
			
			if (rowsAffected <= 0)
				throw new DbException(MSG_ERROR);
			
			if (isInsert) {
				
				ResultSet resultSet = preparedStatement.getGeneratedKeys();
				
				if (resultSet.next())
					seller.setId(resultSet.getInt(1));
				
				DB.closeResultSet(resultSet);
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
		}
	}

	private void prepareStatement(PreparedStatement preparedStatement, Seller seller, boolean isInsert) throws SQLException {
		
		preparedStatement.setString(1, seller.getName());
		preparedStatement.setString(2, seller.getEmail());
		preparedStatement.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
		preparedStatement.setDouble(4, seller.getBaseSalary());
		preparedStatement.setInt(5, seller.getDepartment().getId());
		if (!isInsert)
			preparedStatement.setInt(6, seller.getId());
	}

	private void executeSimpleQuery(String sql, Integer id) {
		
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
		}
	}

	private Seller executeFindQuery(String sql, Integer id) {
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				Department department = instantiateDepartment(resultSet);
				return instantiateSeller(resultSet, department);
			}
			
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
			DB.closeResultSet(resultSet);
		}
	}

	private List<Seller> executeFindAllQuery(String sql, Object... params) {
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			
			preparedStatement = connection.prepareStatement(sql);
			
			if (params != null && params.length > 0)
				preparedStatement.setInt(1, (Integer) params[0]);
			
			resultSet = preparedStatement.executeQuery();
			
			List<Seller> sellers = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while (resultSet.next()) {
				
				Department department = map.get(resultSet.getInt(SellerFields.DEPARTMENT_ID.getFieldName()));
				
				if (department == null) {
					department = instantiateDepartment(resultSet);
					map.put(resultSet.getInt(SellerFields.DEPARTMENT_ID.getFieldName()), department);
				}
				
				sellers.add(instantiateSeller(resultSet, department));
			}
			
			return sellers;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
			DB.closeResultSet(resultSet);
		}
	}

	private Seller instantiateSeller(ResultSet resultSet, Department department) throws SQLException {
		
		Seller seller = new Seller();
		
		seller.setId(resultSet.getInt(SellerFields.ID.getFieldName()));
		seller.setName(resultSet.getString(SellerFields.NAME.getFieldName()));
		seller.setEmail(resultSet.getString(SellerFields.EMAIL.getFieldName()));
		seller.setBaseSalary(resultSet.getDouble(SellerFields.BASE_SALARY.getFieldName()));
		seller.setBirthDate(resultSet.getDate(SellerFields.BIRTH_DATE.getFieldName()));
		seller.setDepartment(department);
		
		return seller;
	}

	private Department instantiateDepartment(ResultSet resultSet) throws SQLException {
		
		Department department = new Department();
		
		department.setId(resultSet.getInt(SellerFields.DEPARTMENT_ID.getFieldName()));
		department.setName(resultSet.getString(SellerFields.DEPARTMENT_NAME.getFieldName()));
		
		return department;
	}
	
}//class SellerDaoJDBC