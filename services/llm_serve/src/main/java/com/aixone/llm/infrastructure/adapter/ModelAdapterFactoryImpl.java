package com.aixone.llm.infrastructure.adapter;

import com.aixone.llm.domain.services.ModelAdapter;
import com.aixone.llm.domain.services.ModelAdapterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ModelAdapterFactoryImpl implements ModelAdapterFactory {
    private final Map<String, ModelAdapter> adapterMap = new HashMap<>();

    @Autowired
    public ModelAdapterFactoryImpl(List<ModelAdapter> adapters) {
        for (ModelAdapter adapter : adapters) {
            if (adapter instanceof ProviderNamed) {
                String provider = ((ProviderNamed) adapter).getProviderName();
                adapterMap.put(provider, adapter);
            }
        }
    }

    @Override
    public ModelAdapter getAdapter(String providerName) {
        return adapterMap.get(providerName);
    }

    /**
     * 适配器需实现该接口以声明自身providerName
     */
    public interface ProviderNamed {
        String getProviderName();
    }
} 