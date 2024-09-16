package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.DepartmentDao;
import model.dao.util.DepartmentFields;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection connection;

	public DepartmentDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void insert(Department department) {
		executeUpdate(department, INSERT, true);
	}

	@Override
	public void update(Department department) {
		executeUpdate(department, UPDATE, false);
	}

	@Override
	public void deleteById(Integer id) {
		executeSimpleQuery(DELETE_BY_ID, id);
	}

	@Override
	public Department findById(Integer id) {
		return executeFindQuery(FIND_BY_ID, id);
	}

	@Override
	public List<Department> findAll() {
		return executeFindAllQuery(FIND_ALL);
	}

	private void executeUpdate(Department department, String sql, boolean isInsert) {

		PreparedStatement preparedStatement = null;

		try {

			preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			prepareStatement(preparedStatement, department, isInsert);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected <= 0)
				throw new DbException(MSG_ERROR);

			if (isInsert) {

				ResultSet resultSet = preparedStatement.getGeneratedKeys();

				if (resultSet.next())
					department.setId(resultSet.getInt(1));

				DB.closeResultSet(resultSet);
			}

		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
		}
	}

	private void prepareStatement(PreparedStatement preparedStatement, Department department, boolean isInsert)
			throws SQLException {

		preparedStatement.setString(1, department.getName());

		if (!isInsert)
			preparedStatement.setInt(2, department.getId());
	}

	private void executeSimpleQuery(String sql, Integer id) {

		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
		}
	}

	private Department executeFindQuery(String sql, Integer id) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return instantiateDepartment(resultSet);
			}

			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
			DB.closeResultSet(resultSet);
		}
	}

	private List<Department> executeFindAllQuery(String sql, Object... params) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(sql);

			if (params != null && params.length > 0)
				preparedStatement.setInt(1, (Integer) params[0]);

			resultSet = preparedStatement.executeQuery();

			List<Department> departments = new ArrayList<>();

			while (resultSet.next())
				departments.add(instantiateDepartment(resultSet));

			return departments;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
			DB.closeResultSet(resultSet);
		}
	}

	private Department instantiateDepartment(ResultSet resultSet) throws SQLException {

		Department department = new Department();

		department.setId(resultSet.getInt(DepartmentFields.ID.getFieldName()));
		department.setName(resultSet.getString(DepartmentFields.NAME.getFieldName()));

		return department;
	}
}// class DepartmentDaoJDBC