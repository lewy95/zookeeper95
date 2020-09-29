package cn.xzxy.lewy.zk.service.impl;

import cn.xzxy.lewy.zk.mapper.CountryMapper;
import cn.xzxy.lewy.zk.pojo.Country;
import cn.xzxy.lewy.zk.service.CountryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("countryService")
public class CountryServiceImpl implements CountryService {

    @Resource
    CountryMapper countryMapper;

    @Override
    public Country getCountry(int id) {
        return countryMapper.selectByPrimaryKey(id);
    }
}
