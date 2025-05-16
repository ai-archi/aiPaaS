package com.aixone.llm.infrastructure.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aixone.llm.domain.services.AudioModelTTSAdapter;
import com.aixone.llm.domain.services.ModelAdapter;
import com.aixone.llm.domain.services.ModelAdapterFactory;

@Component
public class ModelAdapterFactoryImpl implements ModelAdapterFactory {
    private final Map<String, ModelAdapter> adapterMap = new HashMap<>();
    private final Map<String, AudioModelTTSAdapter> wsAdapterMap = new HashMap<>();
    
    @Autowired
    public ModelAdapterFactoryImpl(List<ModelAdapter> adapters, List<AudioModelTTSAdapter> wsAdapters) {
        // 统一注册 ModelAdapter
        for (ModelAdapter adapter : adapters) {
            if (adapter instanceof ModelNamed modelNamed) {
                List<String> modelNames = modelNamed.getModelNames();
                for (String modelName : modelNames) {
                    adapterMap.put(modelName, adapter);
                }
            }
        }
        // 统一注册 wsAdapter
        for (AudioModelTTSAdapter wsAdapter : wsAdapters) {
            if (wsAdapter instanceof ModelNamed modelNamed) {
                List<String> modelNames = modelNamed.getModelNames();
                for (String modelName : modelNames) {
                    wsAdapterMap.put(modelName, wsAdapter);
                }
            }
        }
    }

    @Override
    public ModelAdapter getAdapter(String modelName) {
        return adapterMap.get(modelName);
    }

    @Override
    public AudioModelTTSAdapter getSpeechRecognitionWebSocketAdapter(String modelName) {
        return wsAdapterMap.get(modelName);
    }

    /**
     * 适配器需实现该接口以声明自身providerName
     */
    public interface ModelNamed {
        List<String> getModelNames();
    }
}