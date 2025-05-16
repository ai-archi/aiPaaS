package com.aixone.llm.infrastructure.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aixone.llm.domain.services.AudioModelSTTAdapter;
import com.aixone.llm.domain.services.AudioModelTTSAdapter;
import com.aixone.llm.domain.services.ModelAdapter;
import com.aixone.llm.domain.services.ModelAdapterFactory;

@Component
public class ModelAdapterFactoryImpl implements ModelAdapterFactory {
    private final Map<String, ModelAdapter> adapterMap = new HashMap<>();
    private final Map<String, AudioModelTTSAdapter> ttsAdapterMap = new HashMap<>();
    private final Map<String, AudioModelSTTAdapter> sttAdapterMap = new HashMap<>();
    @Autowired
    public ModelAdapterFactoryImpl(List<ModelAdapter> adapters, List<AudioModelTTSAdapter> ttsAdapters, List<AudioModelSTTAdapter> sttAdapters) {
        // 统一注册 ModelAdapter
        for (ModelAdapter adapter : adapters) {
            if (adapter instanceof ModelNamed modelNamed) {
                List<String> modelNames = modelNamed.getModelNames();
                for (String modelName : modelNames) {
                    adapterMap.put(modelName, adapter);
                }
            }
        }
        // 统一注册 ttsAdapter
        for (AudioModelTTSAdapter ttsAdapter : ttsAdapters) {
            if (ttsAdapter instanceof ModelNamed modelNamed) {
                List<String> modelNames = modelNamed.getModelNames();
                for (String modelName : modelNames) {
                    ttsAdapterMap.put(modelName, ttsAdapter);
                }
            }
        }
        // 统一注册 sttAdapter
        for (AudioModelSTTAdapter sttAdapter : sttAdapters) {
            if (sttAdapter instanceof ModelNamed modelNamed) {
                List<String> modelNames = modelNamed.getModelNames();
                for (String modelName : modelNames) {
                    sttAdapterMap.put(modelName, sttAdapter);
                }
            }
        }
    }

    @Override
    public ModelAdapter getAdapter(String modelName) {
        return adapterMap.get(modelName);
    }

    @Override
    public AudioModelTTSAdapter getAudioModelTTSAdapter(String modelName) {
        return ttsAdapterMap.get(modelName);
    }

    @Override
    public AudioModelSTTAdapter getAudioModelSTTAdapter(String modelName) {
        return sttAdapterMap.get(modelName);
    }

    /**
     * 适配器需实现该接口以声明自身providerName
     */
    public interface ModelNamed {
        List<String> getModelNames();
    }
}