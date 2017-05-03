/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qnium.atomitybackend;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

public final class InstantTypePersister extends BaseDataType {

    public InstantTypePersister() {
        super(SqlType.DATE, new Class[]{Instant.class});
    }

    @Override
    public boolean isEscapedValue() {
        return true;
    }

    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        return javaToSqlArg(fieldType, Instant.parse(defaultStr));
    }

    @Override
    public Timestamp resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getTimestamp(columnPos);
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
        Instant value = null;
        if (javaObject.getClass().equals(Date.class)) {
            value = ((Date)javaObject).toInstant();
        }
        else {
            value = (Instant)javaObject;
        }
        return Timestamp.valueOf(LocalDateTime.ofInstant(value, ZoneId.of("UTC")));
    }

    @Override
    public Instant sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
        Timestamp value = (Timestamp) sqlArg;
        return value.toLocalDateTime().toInstant(ZoneOffset.UTC);
    }
}
