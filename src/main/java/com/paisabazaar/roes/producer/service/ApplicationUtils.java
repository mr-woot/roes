package com.paisabazaar.roes.producer.service;

import com.paisabazaar.roes.producer.payload.ProducerRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

@Service
public class ApplicationUtils {
    void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    public boolean isValidTopic(String s) {
        return s.matches("^com.paisabazaar.([a-zA-Z0-9-_]+).([a-zA-Z0-9-_]+)$");
    }

    public boolean isValidBuNameTypeToTopic(ProducerRequest producerRequest) {
        String buNameDotType = producerRequest.getBuName() + "." + producerRequest.getType();
        String tBuNameDotType = producerRequest.getTopic().substring(16);
        return tBuNameDotType.equals(buNameDotType);
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    boolean validateMessage(String message, String metadata) {
        return false;
    }
}
