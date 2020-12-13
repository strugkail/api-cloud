package com.fn.bi.report.login.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class OrderValidator implements ConstraintValidator<Order, String> {
    private List<String> valueList;

    @Override
    public void initialize(Order order) {
//        valueList = Lists.newArrayList();
        valueList = new ArrayList();
        for (String val : order.accepts()) {
            valueList.add(val.toUpperCase());
        }
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (!valueList.contains(s.toUpperCase())) {
            return false;
        }
        return true;
    }
}
