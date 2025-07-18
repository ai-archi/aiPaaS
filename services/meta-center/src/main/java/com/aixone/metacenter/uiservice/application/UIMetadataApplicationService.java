package com.aixone.metacenter.uiservice.application;

import com.aixone.metacenter.uiservice.application.dto.UIMetadataDTO;
import com.aixone.metacenter.uiservice.application.dto.UIMetadataQuery;
import com.aixone.metacenter.uiservice.domain.UIMetadata;
import com.aixone.metacenter.uiservice.domain.UIMetadataRepository;
import com.aixone.metacenter.uiservice.application.UIMetadataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UIMetadataApplicationService {

    @Autowired
    private UIMetadataRepository uiMetadataRepository;

    @Autowired
    private UIMetadataMapper uiMetadataMapper;

    public UIMetadataDTO createUIMetadata(UIMetadataDTO uiMetadataDTO) {
        UIMetadata uiMetadata = uiMetadataMapper.toEntity(uiMetadataDTO);
        uiMetadata.setCreatedTime(LocalDateTime.now());
        uiMetadata.setUpdatedTime(LocalDateTime.now());
        UIMetadata savedUIMetadata = uiMetadataRepository.save(uiMetadata);
        return uiMetadataMapper.toDTO(savedUIMetadata);
    }

    public UIMetadataDTO updateUIMetadata(Long id, UIMetadataDTO uiMetadataDTO) {
        Optional<UIMetadata> optional = uiMetadataRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("UI元数据不存在: " + id);
        }
        UIMetadata existingUIMetadata = optional.get();
        uiMetadataMapper.updateEntityFromDTO(uiMetadataDTO, existingUIMetadata);
        existingUIMetadata.setUpdatedTime(LocalDateTime.now());
        UIMetadata updatedUIMetadata = uiMetadataRepository.save(existingUIMetadata);
        return uiMetadataMapper.toDTO(updatedUIMetadata);
    }

    public void deleteUIMetadata(Long id) {
        if (!uiMetadataRepository.existsById(id)) {
            throw new RuntimeException("UI元数据不存在: " + id);
        }
        uiMetadataRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public UIMetadataDTO getUIMetadataById(Long id) {
        Optional<UIMetadata> optional = uiMetadataRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("UI元数据不存在: " + id);
        }
        return uiMetadataMapper.toDTO(optional.get());
    }

    @Transactional(readOnly = true)
    public List<UIMetadataDTO> getUIMetadataByTenantId(String tenantId) {
        List<UIMetadata> uiMetadataList = uiMetadataRepository.findByTenantId(tenantId);
        return uiMetadataList.stream()
                .map(uiMetadataMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UIMetadataDTO> getUIMetadata(UIMetadataQuery query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UIMetadata> uiMetadataPage = uiMetadataRepository.findByQuery(query, pageable);
        return uiMetadataPage.map(uiMetadataMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<UIMetadataDTO> getUIMetadataByPageType(String pageType) {
        List<UIMetadata> uiMetadataList = uiMetadataRepository.findByPageType(pageType);
        return uiMetadataList.stream()
                .map(uiMetadataMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UIMetadataDTO> getUIMetadataByComponentType(String componentType) {
        List<UIMetadata> uiMetadataList = uiMetadataRepository.findByComponentType(componentType);
        return uiMetadataList.stream()
                .map(uiMetadataMapper::toDTO)
                .collect(Collectors.toList());
    }
}
