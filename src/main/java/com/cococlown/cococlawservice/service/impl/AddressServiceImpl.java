package com.cococlown.cococlawservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cococlown.cococlawservice.dto.AddressDTO;
import com.cococlown.cococlawservice.entity.Address;
import com.cococlown.cococlawservice.mapper.AddressMapper;
import com.cococlown.cococlawservice.service.AddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 地址服务实现类
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<AddressDTO> getAddressList(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
               .orderByDesc(Address::getIsDefault)
               .orderByDesc(Address::getCreateTime);
        
        List<Address> addresses = addressMapper.selectList(wrapper);
        return addresses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public AddressDTO getDefaultAddress(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
               .eq(Address::getIsDefault, 1);
        
        Address address = addressMapper.selectOne(wrapper);
        return address != null ? convertToDTO(address) : null;
    }

    @Override
    public AddressDTO getAddressDetail(Long userId, Long addressId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
               .eq(Address::getId, addressId);
        
        Address address = addressMapper.selectOne(wrapper);
        return address != null ? convertToDTO(address) : null;
    }

    @Override
    @Transactional
    public Long addAddress(Long userId, Address address) {
        address.setUserId(userId);
        
        // 如果是第一个地址，自动设为默认
        LambdaQueryWrapper<Address> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(Address::getUserId, userId);
        long count = addressMapper.selectCount(countWrapper);
        
        if (count == 0) {
            address.setIsDefault(1);
        }
        
        // 如果设为默认，先取消其他默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            cancelDefaultAddress(userId);
        }
        
        addressMapper.insert(address);
        return address.getId();
    }

    @Override
    @Transactional
    public boolean updateAddress(Long userId, Address address) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
               .eq(Address::getId, address.getId());
        
        Address existing = addressMapper.selectOne(wrapper);
        if (existing == null) {
            throw new RuntimeException("地址不存在");
        }
        
        // 如果设为默认，先取消其他默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            cancelDefaultAddress(userId);
        }
        
        address.setUserId(userId);
        return addressMapper.updateById(address) > 0;
    }

    @Override
    @Transactional
    public boolean deleteAddress(Long userId, Long addressId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
               .eq(Address::getId, addressId);
        
        Address address = addressMapper.selectOne(wrapper);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        
        boolean deleted = addressMapper.delete(wrapper) > 0;
        
        // 如果删除的是默认地址，且还有其他地址，则把第一个设为默认
        if (deleted && address.getIsDefault() == 1) {
            LambdaQueryWrapper<Address> newDefaultWrapper = new LambdaQueryWrapper<>();
            newDefaultWrapper.eq(Address::getUserId, userId)
                           .orderByAsc(Address::getCreateTime())
                           .last("LIMIT 1");
            Address newDefault = addressMapper.selectOne(newDefaultWrapper);
            if (newDefault != null) {
                newDefault.setIsDefault(1);
                addressMapper.updateById(newDefault);
            }
        }
        
        return deleted;
    }

    @Override
    @Transactional
    public boolean setDefaultAddress(Long userId, Long addressId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
               .eq(Address::getId, addressId);
        
        Address address = addressMapper.selectOne(wrapper);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        
        // 取消所有默认
        cancelDefaultAddress(userId);
        
        // 设置新的默认
        address.setIsDefault(1);
        return addressMapper.updateById(address) > 0;
    }

    /**
     * 取消所有默认地址
     */
    private void cancelDefaultAddress(Long userId) {
        LambdaUpdateWrapper<Address> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Address::getUserId, userId)
               .eq(Address::getIsDefault, 1)
               .set(Address::getIsDefault, 0);
        addressMapper.update(null, wrapper);
    }

    private AddressDTO convertToDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        BeanUtils.copyProperties(address, dto);
        // 拼接完整地址
        String fullAddress = address.getProvince() + address.getCity() + 
                            address.getDistrict() + address.getDetail();
        dto.setFullAddress(fullAddress);
        return dto;
    }
}
