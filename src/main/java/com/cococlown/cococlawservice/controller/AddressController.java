package com.cococlown.cococlawservice.controller;

import com.cococlown.cococlawservice.common.Result;
import com.cococlown.cococlawservice.dto.AddressDTO;
import com.cococlown.cococlawservice.entity.Address;
import com.cococlown.cococlawservice.service.AddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器 - P2模块
 */
@Api(tags = "收货地址管理")
@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 获取地址列表
     */
    @ApiOperation("获取地址列表")
    @GetMapping("/list")
    public Result<List<AddressDTO>> getAddressList(@RequestHeader("X-User-Id") Long userId) {
        List<AddressDTO> list = addressService.getAddressList(userId);
        return Result.success(list);
    }

    /**
     * 获取默认地址
     */
    @ApiOperation("获取默认地址")
    @GetMapping("/default")
    public Result<AddressDTO> getDefaultAddress(@RequestHeader("X-User-Id") Long userId) {
        AddressDTO address = addressService.getDefaultAddress(userId);
        return Result.success(address);
    }

    /**
     * 获取地址详情
     */
    @ApiOperation("获取地址详情")
    @GetMapping("/{id}")
    public Result<AddressDTO> getAddressDetail(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        AddressDTO address = addressService.getAddressDetail(userId, id);
        return Result.success(address);
    }

    /**
     * 添加地址
     */
    @ApiOperation("添加地址")
    @PostMapping
    public Result<Long> addAddress(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Address address) {
        Long id = addressService.addAddress(userId, address);
        return Result.success("添加成功", id);
    }

    /**
     * 更新地址
     */
    @ApiOperation("更新地址")
    @PutMapping("/{id}")
    public Result<Boolean> updateAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @RequestBody Address address) {
        address.setId(id);
        boolean success = addressService.updateAddress(userId, address);
        return Result.success(success);
    }

    /**
     * 删除地址
     */
    @ApiOperation("删除地址")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        boolean success = addressService.deleteAddress(userId, id);
        return Result.success(success);
    }

    /**
     * 设置默认地址
     */
    @ApiOperation("设置默认地址")
    @PutMapping("/{id}/default")
    public Result<Boolean> setDefaultAddress(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        boolean success = addressService.setDefaultAddress(userId, id);
        return Result.success(success);
    }
}
