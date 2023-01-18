package com.psc.sample.q103.custom;

import com.psc.sample.q103.dto.OneDto;
import org.springframework.batch.item.file.transform.LineAggregator;

public class CustomPassThroughLineAggregator<T> implements LineAggregator<T> {
    @Override
    public String aggregate(T item) {

        if(item instanceof OneDto) {
            return item.toString() + "_item";
        }
        return item.toString();
    }
}
