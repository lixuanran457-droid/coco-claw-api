package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cococlown.cococlawservice.dto.TokenPackageDTO;
import com.cococlown.cococlawservice.entity.TokenPackage;
import com.cococlown.cococlawservice.mapper.TokenPackageMapper;
import com.cococlown.cococlawservice.service.TokenPackageService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class TokenPackageServiceImpl extends ServiceImpl<TokenPackageMapper, TokenPackage> implements TokenPackageService {

    @Override
    public List<TokenPackageDTO> getAvailablePackages() {
        LambdaQueryWrapper<TokenPackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TokenPackage::getStatus, 1);
        wrapper.eq(TokenPackage::getRuleType, "SUBSCRIPTION");
        wrapper.orderByAsc(TokenPackage::getSortOrder);
        List<TokenPackage> packages = this.list(wrapper);

        List<TokenPackageDTO> dtoList = new ArrayList<>();
        for (TokenPackage pkg : packages) {
            TokenPackageDTO dto = new TokenPackageDTO();
            BeanUtils.copyProperties(pkg, dto);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public TokenPackageDTO getPackageDetail(Long id) {
        TokenPackage tokenPackage = this.getById(id);
        if (tokenPackage == null) {
            return null;
        }
        TokenPackageDTO dto = new TokenPackageDTO();
        BeanUtils.copyProperties(tokenPackage, dto);
        return dto;
    }

    @Override
    public boolean createPackage(TokenPackage tokenPackage) {
        return this.save(tokenPackage);
    }

    @Override
    public boolean updatePackage(TokenPackage tokenPackage) {
        return this.updateById(tokenPackage);
    }

    @Override
    public boolean deletePackage(Long id) {
        return this.removeById(id);
    }
}
