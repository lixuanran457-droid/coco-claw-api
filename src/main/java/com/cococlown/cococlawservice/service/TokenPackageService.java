package com.cococlown.cococlawservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cococlown.cococlawservice.dto.TokenPackageDTO;
import com.cococlown.cococlawservice.entity.TokenPackage;
import java.util.List;

public interface TokenPackageService extends IService<TokenPackage> {
    /**
     * 获取可用的套餐列表
     */
    List<TokenPackageDTO> getAvailablePackages();

    /**
     * 获取套餐详情
     */
    TokenPackageDTO getPackageDetail(Long id);

    /**
     * 创建套餐
     */
    boolean createPackage(TokenPackage tokenPackage);

    /**
     * 更新套餐
     */
    boolean updatePackage(TokenPackage tokenPackage);

    /**
     * 删除套餐
     */
    boolean deletePackage(Long id);
}
