package com.cococlown.cococlawservice.service;

import com.cococlown.cococlawservice.dto.AddressDTO;
import com.cococlown.cococlawservice.entity.Address;

import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {

    /**
     * 获取用户地址列表
     */
    List<AddressDTO> getAddressList(Long userId);

    /**
     * 获取默认地址
     */
    AddressDTO getDefaultAddress(Long userId);

    /**
     * 获取地址详情
     */
    AddressDTO getAddressDetail(Long userId, Long addressId);

    /**
     * 添加地址
     */
    Long addAddress(Long userId, Address address);

    /**
     * 更新地址
     */
    boolean updateAddress(Long userId, Address address);

    /**
     * 删除地址
     */
    boolean deleteAddress(Long userId, Long addressId);

    /**
     * 设置默认地址
     */
    boolean setDefaultAddress(Long userId, Long addressId);
}
