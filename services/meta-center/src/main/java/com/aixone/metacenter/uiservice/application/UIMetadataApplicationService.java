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
        uiMetadata.setCreatedAt(LocalDateTime.now());
        uiMetadata.setUpdatedAt(LocalDateTime.now());
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
        existingUIMetadata.setUpdatedAt(LocalDateTime.now());
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
    public Page<UIMetadataDTO> getUIMetadataByTenantId(String tenantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UIMetadata> uiMetadataPage = uiMetadataRepository.findByTenantId(tenantId, pageable);
        return uiMetadataPage.map(uiMetadataMapper::toDTO);
    }
}
