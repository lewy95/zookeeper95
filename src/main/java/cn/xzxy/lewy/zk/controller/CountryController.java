package cn.xzxy.lewy.zk.controller;

import cn.xzxy.lewy.zk.common.model.JsonResponseEntity;
import cn.xzxy.lewy.zk.dto.CountryDetailReq;
import cn.xzxy.lewy.zk.service.CountryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "城市管理相关接口")
@RestController
@RequestMapping("/rest/country")
public class CountryController {

    @Resource
    private CountryService countryService;

    @PostMapping("/detail")
    @ApiOperation(value = "根据ID查询城市", notes = "根据ID查询城市")
    public JsonResponseEntity getCountry(@RequestBody CountryDetailReq countryDetailReq) {
        return JsonResponseEntity.buildOK(countryService.getCountry(countryDetailReq.getCountryId()));
    }
}