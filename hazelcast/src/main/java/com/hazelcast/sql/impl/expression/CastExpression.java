/*
 * Copyright (c) 2008-2020, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.sql.impl.expression;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.sql.impl.row.Row;
import com.hazelcast.sql.impl.type.DataType;
import com.hazelcast.sql.impl.type.DataTypeUtils;
import com.hazelcast.sql.impl.type.converter.Converter;

import java.io.IOException;

/**
 * Expression which converts data from one type to another.
 */
public class CastExpression<T> extends UniCallExpression<T> {
    /** Target type. */
    private DataType type;

    public CastExpression() {
        // No-op.
    }

    private CastExpression(Expression<?> operand, DataType type) {
        super(operand);
        this.type = type;
    }

    public static CastExpression<?> create(Expression<?> operand, DataType type) {
        DataTypeUtils.ensureCanConvertTo(operand.getType(), type);

        return new CastExpression<>(operand, type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T eval(Row row) {
        Object value = operand.eval(row);
        Converter valueConverter = operand.getType().getConverter();

        return (T) cast(value, valueConverter, type);
    }

    @Override
    public DataType getType() {
        return type;
    }

    public static Object cast(Object fromValue, Converter fromValueConverter, DataType toType) {
        if (fromValue == null) {
            return null;
        }

        return toType.getConverter().convertToSelf(fromValueConverter, fromValue);
    }

    public static Expression<?> coerce(Expression<?> from, DataType toType) {
        DataType fromType = from.getType();

        if (fromType.getType() == toType.getType()) {
            return from;
        } else {
            return CastExpression.create(from, toType);
        }
    }

    public static Object coerce(Object fromValue, DataType fromType, DataType toType) {
        if (fromType.getType() == toType.getType()) {
            return fromValue;
        } else {
            return cast(fromValue, fromType.getConverter(), toType);
        }
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);

        out.writeObject(type);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);

        type = in.readObject();
    }
}
