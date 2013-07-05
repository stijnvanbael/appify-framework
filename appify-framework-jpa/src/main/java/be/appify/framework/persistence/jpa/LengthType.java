package be.appify.framework.persistence.jpa;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

import be.appify.framework.quantity.Length;

public class LengthType extends AbstractUserType {

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.DECIMAL };
	}

	@Override
	public Class<?> returnedClass() {
		return Length.class;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
		BigDecimal value = rs.getBigDecimal(names[0]);
		return value != null ? Length.meters(value.doubleValue()) : null;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
		if (value != null) {
			st.setDouble(index, ((Length) value).getMeters());
		} else {
			st.setNull(index, Types.DECIMAL);
		}
	}

}
