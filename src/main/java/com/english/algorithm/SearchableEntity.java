package com.english.algorithm;

import java.util.List;
import java.util.Map;

//Đây là interface định nghĩa cách đánh giá và tối ưu các entities trong hệ thống
public interface SearchableEntity<T> {
    double scoreCalculation(T entity, Map<String, Object> criteria);
    List<T> filterByCriteria(List<T> entities, Map<String, Object> criteria);
}
