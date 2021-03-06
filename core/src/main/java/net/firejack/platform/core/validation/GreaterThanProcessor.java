/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.firejack.platform.core.validation;


import net.firejack.platform.core.validation.annotation.GreaterThan;
import net.firejack.platform.core.validation.annotation.ValidationMode;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class GreaterThanProcessor implements IMessageRuleProcessor {

    @Override
    public List<ValidationMessage> validate(Method readMethod, String property, Object value, ValidationMode mode)
            throws RuleValidationException {
        GreaterThan greaterThanAnnotation = readMethod.getAnnotation(GreaterThan.class);
        List<ValidationMessage> messages = null;
        if (greaterThanAnnotation != null) {
            messages = new ArrayList<ValidationMessage>();
            Class<?> returnType = readMethod.getReturnType();
            String parameterName = StringUtils.isBlank(greaterThanAnnotation.parameterName()) ?
                    property : greaterThanAnnotation.parameterName();
            if (value != null) {
                if (returnType.getSuperclass() == Number.class || returnType.isPrimitive()) {
                    boolean checkEquality = greaterThanAnnotation.checkEquality();
                    Number val = null;
                    if (returnType == Float.class || returnType == float.class) {
                        Float f = (Float) value;
                        if (checkEquality && f < greaterThanAnnotation.floatVal() ||
                                !checkEquality && f <= greaterThanAnnotation.floatVal()) {
                            val = greaterThanAnnotation.floatVal();
                        }
                    } else if (returnType == Double.class || returnType == double.class) {
                        Double d = (Double) value;
                        if (checkEquality && d < greaterThanAnnotation.doubleVal() ||
                                !checkEquality && d <= greaterThanAnnotation.doubleVal()) {
                            val = greaterThanAnnotation.doubleVal();
                        }
                    } else {
                        Long longValue = ((Number) value).longValue();
                        Long rangeValue = ((Integer) greaterThanAnnotation.intVal()).longValue();
                        if (checkEquality && longValue < rangeValue || !checkEquality && longValue <= rangeValue) {
                            val = greaterThanAnnotation.intVal();
                        }
                    }
                    if (val != null) {
                        messages.add(new ValidationMessage(property,
                                checkEquality ? greaterThanAnnotation.equalityMsgKey() :
                                        greaterThanAnnotation.msgKey(), parameterName, val));
                    }
                }
            }
        }
        return messages;
    }

    @Override
    public List<Constraint> generate(Method readMethod, String property, Map<String, String> params) {
        return null;
    }

}