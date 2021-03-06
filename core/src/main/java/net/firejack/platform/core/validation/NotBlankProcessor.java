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


import net.firejack.platform.core.utils.ArrayUtils;
import net.firejack.platform.core.utils.MessageResolver;
import net.firejack.platform.core.validation.annotation.NotBlank;
import net.firejack.platform.core.validation.annotation.ValidationMode;
import net.firejack.platform.core.validation.constraint.vo.Constraint;
import net.firejack.platform.core.validation.exception.RuleValidationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@Component
public class NotBlankProcessor implements IMessageRuleProcessor {

    @Override
    public List<ValidationMessage> validate(Method readMethod, String property, Object value, ValidationMode mode)
            throws RuleValidationException {
        List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
        Annotation annotation = readMethod.getAnnotation(NotBlank.class);
        if (annotation != null) {
            NotBlank notBlank = (NotBlank) annotation;
            String parameterName = StringUtils.isNotBlank(notBlank.parameterName()) ?
                                        notBlank.parameterName() : property;
            boolean validate = ArrayUtils.contains(notBlank.modes(), mode);
            if (validate) {
                if (value == null) {
                    validationMessages.add(new ValidationMessage(property, notBlank.msgKey(), parameterName));
                } else if (!(value instanceof String)) {
                    validationMessages.add(new ValidationMessage(property, "Argument '" + parameterName + "' should be of type java.lang.String"));
                } else if (StringUtils.isBlank((String) value)) {
                    validationMessages.add(new ValidationMessage(property, notBlank.msgKey(), parameterName));
                }
            }
        }
        return validationMessages;
    }

    @Override
    public List<Constraint> generate(Method readMethod, String property, Map<String, String> params) {
        List<Constraint> constraints = null;
        Annotation annotation = readMethod.getAnnotation(NotBlank.class);
        if (annotation != null) {
            NotBlank notBlank = (NotBlank) annotation;
            boolean generateConstraint;
            if (params != null && StringUtils.isNotBlank(params.get("id"))) {
                generateConstraint = ArrayUtils.contains(notBlank.modes(), ValidationMode.UPDATE);
            } else {
                generateConstraint = ArrayUtils.contains(notBlank.modes(), ValidationMode.CREATE);
            }
            if (generateConstraint) {
                Constraint constraint = new Constraint(notBlank.annotationType().getSimpleName());
                String errorMessage = MessageResolver.messageFormatting(notBlank.msgKey(), Locale.ENGLISH, property); //TODO need to set real locale
                constraint.setErrorMessage(errorMessage);
                constraints = new ArrayList<Constraint>();
                constraints.add(constraint);
            }
        }
        return constraints;
    }

}
