package com.aixone.llm.infrastructure.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.aixone.llm.domain.services.ModelAdapter;
import com.aixone.llm.domain.services.ModelAdapterFactory;

@Component
public class ModelAdapterFactoryImpl implements ModelAdapterFactory {
    private final Map<String, ModelAdapter> adapterMap = new HashMap<>();

    
    public ModelAdapterFactoryImpl(List<ModelAdapter> adapters) {
        for (ModelAdapter adapter : adapters) {
            if (adapter instanceof ModelNamed modelNamed) {
                List<String> modelNames = modelNamed.getModelNames();
                for (String modelName : modelNames) {
                    adapterMap.put(modelName, adapter);
                }
            }
        }
    }

    @Override
    public ModelAdapter getAdapter(String modelName) {
        return adapterMap.get(modelName);
    }

    /**
     * 适配器需实现该接口以声明自身providerName
     */
    public interface ModelNamed {
        List<String> getModelNames();
    }
}